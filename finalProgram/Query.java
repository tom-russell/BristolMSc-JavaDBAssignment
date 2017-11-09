import java.util.*;
import java.io.*;

/* Performs the requested queries on the table at the top of the stack */
class Query
{
    private Stack<Table> queryStack;
    private String dbName;
    
    Query(String dbName)
    {
        queryStack = new Stack<Table>();
        this.dbName = dbName;
    }
    
    // Return the size of the stack
    public int getSize()
    {
        return queryStack.size();
    }
    
    // Return a list of string arrays of the table from the top of the stack
    public List<String[]> getStackTable()
    {
        Table table = queryStack.peek();
        if (table == null) return null;
        
        List<String[]> printData = new ArrayList<String[]>();
        for (int i = 0; i < table.getSize(); i++)
        {
            printData.add(table.select(i));
        }
        return printData;
    }
    
    // Complete the "load" query, by loading the given table to the queryStack
    public void load(Table table)
    {
        queryStack.push(table);
    }
    
    // Return a table to be saved to file
    public Table save()
    {
        return queryStack.pop();
    }
    
    // Empty the queryStack
    public void clear()
    {
        queryStack.removeAllElements();
        System.out.println("Stack has been cleared.");
    }
    
    // Select the specified columns from the table, remove the rest
    public void project(String[] colNames)
    {
        Table table = queryStack.pop();
        
        if (table.projectColumns(colNames) == false) {
            System.out.println("Projection failed, invalid column name.");
        }
        
        queryStack.push(table);
    }
    
    // Create the specified empty table and add to the top of the stack
    public void create(String[] attributeNames, Boolean suppress)
    {
        queryStack.push(new Table("temp", attributeNames, false));
        if (suppress == false) System.out.println("New table has been created.");
    }
    
    // Add the given row to the current table
    public void add(String[] attributes)
    {
        Table table = queryStack.peek();
        if (table.insert(attributes, false) == true) System.out.println("Row added successfully.");
        else System.out.println("Error adding row, incorrect number of attributes");
    }
    
    // Rename the specified column
    public void rename(String[] rename)
    {
        Table table = queryStack.peek();
        int colIndex;
        
        if ((colIndex = table.columnExists(rename[0])) != -1)
        {
            String[] newAttributes = table.getRecord(0);
            newAttributes[colIndex] = rename[1];
            table.update(0, Arrays.copyOfRange(newAttributes, 1, newAttributes.length));
            System.out.println("Column renamed successfully.");
        }
        else {
            System.out.println("Column rename failed. Specified column not found.");
        }
    }
    
    // Join the top two tables on the stack by the two column names given
    public Boolean join(String[] colNames)
    {
        if (colNames.length != 2) 
        {
            System.out.println("Two column names are required for a join.");
            return false;
        }
        
        Table table1 = queryStack.pop();
        Table table2 = queryStack.pop();
        int t1joinCol = table1.columnExists(colNames[0]);
        int t2joinCol = table2.columnExists(colNames[1]);
        
        // Check both column names exist
        if (t1joinCol == -1 || t2joinCol == -1 ) 
        {
            System.out.println("Specified column names not found in tables.");
            queryStack.push(table2);
            queryStack.push(table1);
            
            return false;
        }
        
        String[] joinTableAttributes = createJoinAttributes(table1, table2, 0, 0, t2joinCol);
        create(joinTableAttributes, false);
        Table joinTable = queryStack.pop();
        
        for (int i = 1; i < table1.getSize(); i++)
        {
            for (int j = 1; j < table2.getSize(); j++)
            {
                if (table1.getRecord(i)[t1joinCol].equals(table2.getRecord(j)[t2joinCol]))
                { 
                    joinTable.insert(createJoinAttributes(table1, table2, i, j, t2joinCol), false);
                }
            }
        }
        
        queryStack.push(joinTable);
        return true;
    }
    
    // Create an array of attributes from the tables being joined at the matching indexes
    private String[] createJoinAttributes (Table table1, Table table2, int t1index, int t2index, int t2joinCol)
    {
        String[] t1rec = table1.getRecord(t1index);
        String[] t2rec = table2.getRecord(t2index);
        
        // Join table is the length of both tables combined -1 (for the repeated attribute) and 
        // -2 (remove previous ID columns)
        int joinTableSize = t1rec.length + t2rec.length - 3;
        String[] joinTableAttributes = new String[joinTableSize];
        
        for (int i = 1; i < t1rec.length; i++)
        {
            joinTableAttributes[i - 1] = t1rec[i];
        }
        int index = t1rec.length;
        
        for (int i = 1; i < t2rec.length; i++)
        {
            if (i != t2joinCol)
            {
                if (t1index == 0) joinTableAttributes[index - 1] = table2.getName() + "." + t2rec[i];
                else joinTableAttributes[index - 1] = t2rec[i];
                index++;
            }
        }
        
        return joinTableAttributes;
    }
}