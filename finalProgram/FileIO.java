import java.util.*;
import java.io.*;

/* Provides functionality for saving and loading tables to/from file. A new FileIO object is 
 * required for each save/load. */
class FileIO
{
    private File file;
    private List<String[]> fileLines;
    private int lineCount;
    
    public static void main(String[] args)
    {
        // Run the testing function when assertions are enabled
        boolean testing = false;
        assert(testing = true);
        if (testing) {
            test();
        }
    }
    
    FileIO(File file, Boolean loading) 
    {
        this.file = file;
        
        // Load the file data into fileLines if  loading a table
        if (loading == true) 
        {
            fileLines = new ArrayList<String[]>();
            lineCount = 0;
            loadFromFile();
        }
        // Empty the table file if saving a table
        else {
            try { 
                new PrintWriter(file).close(); 
            }
            catch (IOException e) { System.out.println("File not found!"); }
        }
    }
    
    // Load all the lines from the file into the fileLines list.
    public void loadFromFile()
    {
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line = "";
            
            while ((line = br.readLine()) != null)
            {
                String[] lineSplit = line.split(",");
                fileLines.add(lineSplit);
            }
        }
        catch (IOException e) 
        {
            System.out.println("Failed to load file: " + file.getName());
        }
    }
    
    // If there are still lines left, return the next line. Otherwise return null
    public String[] getNext()
    {
        if (lineCount > fileLines.size() - 1) return null;
        else if (fileLines.get(lineCount)[0].equals("")) return null;
        else return fileLines.get(lineCount++);
    }
    
    // Append the line to the file being saved to
    public Boolean saveLineToFile(String[] line)
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true)))
        {
            String csvLine = "";
            for (int i = 0; i < line.length; i++) { csvLine += line[i] + ","; }
            csvLine = csvLine.substring(0, csvLine.length() - 1);
            
            bw.append(csvLine);
            bw.newLine();
            return true;
        }
        catch (IOException e)
        {
            System.out.println("Failed to save file: " + file.getName());
            return false;
        }
    }
    
    // Testing function for FileIO, run when assertions are enabled
    private static void test() 
    {
        // Test file and test String arrays for saving and loading
        File file = new File("test.tab");
        String[] testString1 = new String[]{"Id","Name","Kind","Owner"};
        String[] testString2 = new String[]{"1","Fido","dog","ab123"};
        String[] testString3 = new String[]{"2","Wanda","Hippopotamus","ef789"};
        
        // Testing FileIO with saving
        FileIO sFile = new FileIO(file, false);
        assert(sFile.saveLineToFile(testString1) == true);
        assert(sFile.saveLineToFile(testString2) == true);
        assert(sFile.saveLineToFile(testString3) == true);
        
        // Testing FileIO with loading
        FileIO lFile = new FileIO(file, true);
        assert(lFile.fileLines.size() == 3);
        assert(lFile.lineCount == 0);
        assert(Arrays.equals(lFile.getNext(), testString1) == true);
        assert(Arrays.equals(lFile.getNext(), testString2) == true);
        assert(Arrays.equals(lFile.getNext(), testString3) == true);
        assert(lFile.lineCount == 3);
        assert(lFile.getNext() == null);
        assert(lFile.getNext() == null);
        assert(lFile.lineCount == 3);
        
        // Clean-up test file at the end
        try { file.delete(); }
        catch (SecurityException e) {
            System.out.println("Error deleting test file, SecurityException.");
        }
    }
}