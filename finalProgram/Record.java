import java.util.*;

/* The Record class is a generic class for storing a single row from a single database table. The
 * record data is private but can be accessed through the supplied functions. */ 
class Record 
{
    private List<String> elements;
    
    // Main function for use only when testing Record by itself.
    public static void main(String[] args) 
    {
        boolean testing = false;
        assert(testing = true);
        if (testing) {
            test();
        }
    }
    
    // Populate the List object with the inputs strings.
    Record(String[] inputs) 
    {
        elements = new ArrayList<String>();
        
        for (int i = 0; i < inputs.length; i++) {
            elements.add(inputs[i]);
        }
    }
    
    // Used to determine if two records are the same
    public boolean equals(Record other)
    {
        if (this.getSize() != other.getSize()) return false;
        
        for (int i = 0; i < this.getSize(); i++) {
            if (this.getElement(i).equals(other.getElement(i)) == false) return false;
        }
        
        return true;
    }
    
    // Retrieve an individual element from the record, at given index. Return null if out of bounds
    public String getElement(int index) 
    { 
        String element = "";
        try { element = elements.get(index); }
        catch (IndexOutOfBoundsException e) { fatalError(e); }
        
        return element;
    }
    
    // Update the element at the given index with the input string.
    public Boolean setElement(int index, String update) 
    { 
        try { elements.set(index, update); }
        catch (IndexOutOfBoundsException e) { fatalError(e); }
        
        return true;
    }
    
    // Update the element at the given index with the input string.
    public Boolean addElement(int index, String element) 
    { 
        try { elements.add(index, element); }
        catch (IndexOutOfBoundsException e) { fatalError(e); }
        
        return true;
    }
    
    // Return the size of the record as an integer (number of elements/columns).
    public int getSize() { return elements.size(); }
    
    public void removeElement(int index) 
    {
        elements.remove(index);
    }
    
    // Fatal error is called when an exception occurs in record. These exceptions should be 
    // avoided by the classes that use Record.
    private void fatalError(Exception e)
    {
        e.printStackTrace();
        System.exit(1);
    }
    
    // Tests for the Record class, this function is only called when Record is run by itself.
    private static void test() 
    {
        // Two test records for testing the other functions
        Record testRecord1 = new Record(new String[]{"1", "Fido", "dog", "ab123"});
        Record testRecord2 = new Record(new String[]{"ab123", "Jo"});
        Record testRecord3 = new Record(new String[]{"1", "Fido", "dog", "ab123"});
        
        // Test the equals function
        assert(testRecord1.equals(testRecord3) == true);
        assert(testRecord3.equals(testRecord1) == true);
        assert(testRecord1.equals(testRecord2) == false);
        assert(testRecord2.equals(testRecord3) == false);
        
        // Test that getElement() functions correctly for valid/invalid inputs
        assert(testRecord1.getElement(0).equals("1"));
        assert(testRecord1.getElement(1).equals("Fido"));
        assert(testRecord1.getElement(2).equals("dog"));
        assert(testRecord1.getElement(3).equals("ab123"));
        assert(testRecord2.getElement(0).equals("ab123"));
        assert(testRecord2.getElement(1).equals("Jo"));
        
        // Test the setElement() function
        assert(testRecord1.setElement(0, "5") == true);
        assert(testRecord1.getElement(0).equals("5"));
        assert(testRecord1.setElement(1, "Jeff") == true);
        assert(testRecord1.getElement(1).equals("Jeff"));
        
        // Test the getSize() function
        assert(testRecord1.getSize() == 4);
        assert(testRecord2.getSize() == 2);
    }
}