import java.util.*;
import java.io.*;

/* The Database class is a generic class for storing a database. The database holds a collection of
 * tables. */
class Database 
{
    private String dbName;
    private List<Table> tables;
    Query dbQuery;
    
    Database(String name)
    {
        dbName = name;
        dbQuery = new Query(dbName);
        tables = new ArrayList<Table>();
        
        loadDbTables();
    }
    
    // Set currentTable to the correct table from Tables using the given table name
    private Table findTable(String tableName)
    {
        for (Table table : tables)
        {
            if (table.getName().equals(tableName))
            {
                return table;
            }
        }
        return null;
    }
    
    // Load all database tables from this database directory
    private void loadDbTables()
    {
        File DbFolder = new File("databases/" + dbName);
        File[] tableFiles = DbFolder.listFiles();
        
        for (File file : tableFiles) 
        {
            tables.add(loadTable(file, false));
        }
        
        if (tableFiles.length == 0) System.out.println("Database is empty, no tables to load!");
    }
    
    // Load one table from file
    private Table loadTable(File file, Boolean suppress)
    {
        String tableName = file.getName();
        FileIO fLoad = new FileIO(file, true);
        
        int pos = tableName.lastIndexOf(".");
        tableName = tableName.substring(0, pos);
        Table table = new Table(tableName, fLoad.getNext(), true);
        
        String[] next;
        int highestId, validLines;
        highestId = validLines = 0;
        
        while ((next = fLoad.getNext()) != null)
        {
            int thisId = Integer.parseInt(next[0]);
            if (thisId > highestId) highestId = thisId;
            
            if (table.insert(next, true) == true) validLines++;
        }
        
        table.setNextId(highestId + 1); 
        if (suppress == false) 
        {
            System.out.print("Table loaded (" + validLines);
            System.out.println(" Entries)\t" + tableName);
        }
        
        return table;
    }
    
    // Save all database tables from this database directory
    public void saveDbTables()
    {
        for (Table table : tables) 
        {
            saveTable(table.getName(), table);
        }
    }
    
    // Save one table to the filename specified
    public void saveTable(String tableName, Table table)
    {
        String filePath = "databases/" + dbName + "/" + tableName + ".csv";
        File file = new File(filePath);
        FileIO fSave = new FileIO(file, false);
        
        table.checkForId();
        
        for (int i = 0; i < table.getSize(); i++)
        {
            String[] record = table.getRecord(i);
            fSave.saveLineToFile(record);
        }
        
        System.out.println("Table saved: " + tableName);
    }
    
    
    // For the given input table name print out a formatted table of all records
    public List<String[]> getTableRecords(String tableName)
    {
        Table table = findTable(tableName);
        if (table == null) return null;
        
        List<String[]> printData = new ArrayList<String[]>();
        for (int i = 0; i < table.getSize(); i++)
        {
            printData.add(table.select(i));
        }
        return printData;
    }
    
    // Returns a string array containing all the names of tables within this database
    public String[] getTableList()
    {
        String[] nameList = new String[tables.size()];
        
        for (int i = 0; i < tables.size(); i++)
        {
            nameList[i] = tables.get(i).getName();
        }
        return nameList;
    }
    
    // return true if the table is in the current database
    public Boolean tableExists(String name)
    {
        String[] tableList = getTableList();
        
        for (String table : tableList)
        {
            if (table.equals(name)) return true;
        }
        
        System.out.println("Table \"" + name + "\" does not exist.");
        return false;
    }
    
    // Remove the table from the tables list and delete the file from the database
    private void deleteTable(String name)
    {
        Table table = findTable(name);
        tables.remove(table);
        
        try {
            File file = new File("databases/" + dbName + "/" + name + ".csv");
            file.delete();
            System.out.println(name + ".csv has been deleted.");
        }
        catch (SecurityException e)
        {
            System.out.println("File cannot be deleted. Database does not have access.");
        }
        
    }
    
    // Process a query and call the correct querying function
    public Boolean query(String[] query)
    {
        String keyword = query[0];
        String[] otherWords = Arrays.copyOfRange(query, 1, query.length);
        
        if (keyword.equals("load")) 
        {
            if (tableExists(query[1])) 
            {
                File file = new File("databases/" + dbName + "/" + query[1] + ".csv");
                dbQuery.load(loadTable(file, true));
            }
        }
        else if (query[0].equals("save"))
        {
            saveTable(query[1], dbQuery.save());
            File file = new File("databases/" + dbName + "/" + query[1] + ".csv");
            tables.add(loadTable(file, true));
            return false;
        }
        else if (keyword.equals("delete")) 
        {
            if (tableExists(query[1])) deleteTable(query[1]);
            return false;
        }
        else if (keyword.equals("clear")) 
        {
            dbQuery.clear(); 
            return false;
        }
        else if (keyword.equals("create")) dbQuery.create(otherWords, false);
        else if (keyword.equals("add")) dbQuery.add(otherWords);
        else if (keyword.equals("rename")) dbQuery.rename(otherWords);
        else if (keyword.equals("project")) 
        {
            if (dbQuery.getSize() < 1)
            {
                System.out.println("One loaded table is required for a project.");
                return false;
            }
            dbQuery.project(otherWords);
        }
        else if (keyword.equals("join")) 
        {
            if (dbQuery.getSize() < 2)
            {
                System.out.println("Two loaded tables are required for a join.");
                return false;
            }
            if (dbQuery.join(otherWords) == false) return false;
        }
        return true;
    }
    
    // Retrieve the print data from the table at the top of the query stack
    public List<String[]> queryGetTable()
    {
        return dbQuery.getStackTable();
    }
}