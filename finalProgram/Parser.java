import java.util.*;
import java.io.*;
import java.lang.String;

/* Parses the next command input from the user */
class Parser 
{
    // Keywords that are valid by themselves
    private static final String[] singleKeywords = {
        "list", "quit", "clear"
    };
    // Keywords that must be followed by one column or table names
    private static final String[] doubleKeywords = {
        "load", "basicprint", "show", "project", "save", "delete"
    };
    // Keywords that must be followed by multiple column or table names
    private static final String[] multiKeywords = {
        "add", "create", "join", "rename"
    };
    
    // Scan in the name of a table or database from the user. Return null if multiple words are 
    // entered (valid names are one word only).
    public String parseName()
    {
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine().trim();
        if (name.split(" ").length == 1) return name;
        return "\0";
    }
    
    // Scan in a table query from the user. If the first word is a valid keyword, check if it has 
    // the correct number of following words. Return the query if valid, else return null.
    public String[] nextQuery()
    {
        System.out.println("");
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine().trim();
        String[] words = line.split(" ");
        
        for (String keyword : singleKeywords)
        {
            if (words[0].equals(keyword)) 
            {
                if (words.length == 1) return words;
                else return null;
            }
        }
        for (String keyword : doubleKeywords)
        {
            if (words[0].equals(keyword)) 
            {
                if (words.length > 1) return words;
                else return null;
            }
        }
        for (String keyword : multiKeywords)
        {
            if (words[0].equals(keyword)) 
            {
                if (words.length > 2) return words;
                else return null;
            }
        }
        return null;
    }
    
    
    // If the user inputs Y/y then return true, if N/n return false, else return null.
    public Boolean yesNo()
    {
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine().trim().toLowerCase();
        
        if (line.equals("y")) return true;
        else if (line.equals("n")) return false;
        
        return null;
    }
}