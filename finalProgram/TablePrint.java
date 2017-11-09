import java.util.*;


/* Allows printing of formatted tables, with either simple characters (+|-) when TablePrint(true) or
 * unicode box characters (not compatible with all terminals) when TablePrint(false). */
class TablePrint
{
    private int[] printWidth;
    private List<String[]> printData;
    // Unicode characters for drawing table borders. First index is pos (top/mid/bot of table),  
    // second index is character type (left/mid/right corners & horizontal line).
    private static String[][] boxChars = {
        { "\u2554", "\u2566", "\u2557", "\u2550" },
        { "\u2560", "\u256C", "\u2563", "\u2550" },
        { "\u255A", "\u2569", "\u255D", "\u2550" },
    };
    private static String vertChar = "\u2551";

    // Main function for use only when testing TablePrint by itself
    public static void main(String[] args) 
    {
        boolean testing = false;
        assert(testing = true);
        if (testing) {
            test();
        }
    }
    
    TablePrint(Boolean basic)
    {
        if (basic == true)
        {
            for (int i = 0; i < 3; i++) 
            {
                for (int j = 0; j < 3; j++) boxChars[i][j] = "+";
            }
            for (int i = 0; i < 3; i++) boxChars[i][3] = "-";
            vertChar = "|";
        }
    }
    
    // Print out the records in a formatted table ( with attribute names). Return the number of
    // records that were printed.
    public int printTable(List<String[]> printData) 
    {
        this.printData = printData;
        setPrintWidth();
        
        int lineCount = 0;
        
        printDivider(0);
        printRow(0);
        printDivider(1);
        for (int i = 1; i < printData.size(); i++) 
        {
            printRow(i);
            lineCount++;
        }
        printDivider(2);
        
        return lineCount;
    }
    
    // For the table being printed set printWidth to equal the highest number of characters for
    // each column (including the attribute names). This is required for correct table formatting.
    private void setPrintWidth() 
    {
        int numCols = printData.get(0).length;
        printWidth = new int[numCols];
        
        for (String[] line : printData) 
        {
            for (int col = 0; col < numCols; col++) {
                int newLength = line[col].length() + 1;
                if (newLength > printWidth[col]) printWidth[col] = newLength;
            }
        }
    }
    
    // Print the table divider, correctly formatted based on printWidth with pos indicating where 
    // the divider lies on the table. Return the length of the printed string (for testing).
    // pos = 0 (top table divider), pos = 1 (middle table divider), pos = 2 (bottom table divider)
    private int printDivider(int pos) 
    {
        String divider = " " + boxChars[pos][0]; 
        
        for(int col = 0; col < printWidth.length; col++) 
        {
            for (int i = 0; i < printWidth[col]; i++) divider += boxChars[pos][3];
            divider += boxChars[pos][3] + boxChars[pos][1];
        }
        
        System.out.println(divider.substring(0, divider.length() - 1) + boxChars[pos][2]);
        
        return divider.length();
    }
    
    // Print the row at the given index, with space padding based on printWidth. Return the length
    // of the printed string (for testing).
    private int printRow(int index) 
    {
        String line = " ";
        
        for(int col = 0; col < printData.get(index).length; col++) {
            line += vertChar;
            line += " " + printData.get(index)[col];
            
            int spaces = printWidth[col] - printData.get(index)[col].length();
            for (int i = 0; i < spaces; i++) line += " ";
            
        }
        line += vertChar;
        System.out.println(line);
        
        return line.length();
    }
    
    private static void test()
    {
        List<String[]> testInput = new ArrayList<String[]>();
        testInput.add(new String[]{"ID", "Name", "Kind", "Owner"});
        testInput.add(new String[]{"1", "Fido", "dog", "ab123"});
        testInput.add(new String[]{"2", "Wanda", "Hippopotamus", "ef789"});
        int lineCharLength = 38;
        
        TablePrint testDisplay = new TablePrint(true);
        
        // Test printTable prints the correct number of records
        assert(testDisplay.printTable(testInput) == 2);
        
        // Test printWidth calculated values against hard-coded expected values
        assert(testDisplay.printWidth[0] == 3);
        assert(testDisplay.printWidth[1] == 6);
        assert(testDisplay.printWidth[2] == 13);
        assert(testDisplay.printWidth[3] == 6);
        
        // Test printDivider prints the expected number of characters
        assert(testDisplay.printDivider(0) == lineCharLength);
        assert(testDisplay.printDivider(1) == lineCharLength);
        assert(testDisplay.printDivider(2) == lineCharLength);
        
        // Test printRow prints the expected number of characters
        assert(testDisplay.printRow(0) == lineCharLength);
        assert(testDisplay.printRow(1) == lineCharLength);
        assert(testDisplay.printRow(2) == lineCharLength);
    }
}