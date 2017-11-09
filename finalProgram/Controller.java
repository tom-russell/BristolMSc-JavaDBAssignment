import java.util.*;
import java.io.*;

/* Controlling class for the database program */
class Controller
{
    String[] dbList;
    Database db;
    Boolean basicPrint = false;
    
   
    public static void main(String[] args) 
    {
        Controller program = new Controller();
        program.run();
    }
    
    private void run() 
    {
        Parser parse = new Parser();
        String dbName;
        String[] query;
        
        getDatabaseList();
        
        while ((dbName = parse.parseName()).equals("quit") != true)
        {
            if (inList(dbName) != true) 
            {
                System.out.println("Database does not exist!");
                Boolean yes;
                do { System.out.println("Create a new database with this name (Y/N)?"); }
                while ((yes = parse.yesNo()) == null);
                
                if (yes == true) createDatabase(dbName);
                
            }
            else 
            {
                db = new Database(dbName);
                Boolean quit = false;
                
                do 
                {
                    query = parse.nextQuery();
                    if (query == null) System.out.println("Invalid query, try again.");
                    else 
                    {
                        // Check if the user has attempted to quit
                        if (query[0].equals("quit")) quit = true;
                        else quit = false;
                        
                        if (quit == false) performQuery(query);
                    }
                }
                while (quit == false);

                db.saveDbTables();
            }
            getDatabaseList();
        }
        
        System.out.println("Program closing. Bye!");
    }
    
    // Create a new database folder
    private void createDatabase(String newDbName)
    {
        Boolean success = new File("databases/" + newDbName).mkdir();
        
        if (success == true) System.out.println("New database " + newDbName + " created.");
        else System.out.println("Database folder cannot be created.");
    }
    
    // Perform a query inputted by the user.
    private void performQuery(String[] query)
    {
        String keyword = query[0];
        // "list" query
        if      (keyword.equals("list")) listTables();
        // "show" query
        else if (keyword.equals("show")) 
        {
            if (db.tableExists(query[1])) showTable(query[1]);
        }
        // "basicprint" query
        else if (keyword.equals("basicprint"))
        {
            if (query[1].equals("true")) basicPrint = true;
            else if (query[1].equals("false")) basicPrint = false;
            else System.out.println("basicprint must be followed by true/false.");
        }
        // clear/load/project/save/delete/create/add queries
        else {
            Boolean print = db.query(query);
            
            // For these queries reprint the updated table after completing the query
            if (print == true &&
               (keyword.equals("load") ||
                keyword.equals("project") ||
                keyword.equals("create") ||
                keyword.equals("add") ||
                keyword.equals("join") ||
                keyword.equals("rename")))
            {
                TablePrint tp = new TablePrint(basicPrint);
                tp.printTable(db.queryGetTable());
            }
        }
    }
    
    // Checks if the database name specified currently exists.
    private Boolean inList(String dbName)
    {
        for (int i = 0; i < dbList.length; i++)
        {
            if (dbName.equals(dbList[i])) return true;
        }
        return false;
    }
    
    // Print out the specified table from the database
    private void showTable(String tName)
    {
        TablePrint tp = new TablePrint(basicPrint);
        tp.printTable(db.getTableRecords(tName));
    }
    
    // Print out a list of all the tables in the current database
    private void listTables()
    {
        System.out.println(Arrays.toString(db.getTableList()));
    }
    
    // Create a string array of all the databases currently in the directory
    private void getDatabaseList()
    {
        File databases = new File("databases");
        File[] dbFolders = databases.listFiles();
        dbList = new String[dbFolders.length];
        
        for (int i = 0; i < dbFolders.length; i++)
        {
            dbList[i] = dbFolders[i].getName();
        }
        
        System.out.println("\nWelcome! These are the available databases:");
        System.out.println(Arrays.toString(dbList));
        System.out.println("\nChoose a Database to load:");
    }
}