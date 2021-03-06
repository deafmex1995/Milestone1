import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import java.io.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import java.util.*;

public class EdgeConvertFileParserTest {

  private int numberOfTables = 0;
  private int numberOfFields = 0;
  private FileReader fr;
  private BufferedReader br;
  private String fileName = "./Courses.edg";
  private ArrayList<Integer> figure1IntArray = new ArrayList<Integer>();
  private ArrayList<Integer> figure2IntArray = new ArrayList<Integer>();
  private ArrayList<Integer> figure1TableIntArray = new ArrayList<Integer>();
  private ArrayList<Integer> figure2TableIntArray = new ArrayList<Integer>();
  private ArrayList<String> fieldNames = new ArrayList<String>();
  private ArrayList<String> tableNames = new ArrayList<String>();
  
  private void getTablesandFieldNames() {
    // Find all the entity and Field items in the EDG
    // file
  File file = new File(fileName);
  try {
  fr = new FileReader(file);
  br = new BufferedReader(fr);
  String line;
  while ((line = br.readLine()) !=null) {
   line = line.trim();
   if(line.startsWith ("Figure ")) {
    line = br.readLine().trim();
    line = br.readLine().trim();
    if (line.contains("Style")) {
      if (line.contains("Entity")) {
	// if the Figure is an Entity, save that Name
	// into Table names
	line = br.readLine().trim();
	String[] tempStringArray = line.split(" ");
	tempStringArray[1] = tempStringArray[1].replace("\"", "");
	tableNames.add(tempStringArray[1]);

	
      } 
      else if (line.contains("Attribute")) {
	// if the Figures is an Attribute, add the name
	// to the fieldTables
	line = br.readLine().trim();
	String[] tempStringArray = line.split(" ");
	tempStringArray[1] = tempStringArray[1].replace("\"", "");
	fieldNames.add(tempStringArray[1]);
      }
     }
    }
   }
  }
  catch (FileNotFoundException ex) {
   System.out.println("Cannot find file");
  }
  catch (IOException ioe) {
    System.out.println(ioe);
  }
  }
  // Saves the Connections combinations into two arrays
  // depending on whether or not it is table to field or
  // table to table
  private void getTableandFieldNumfromConnectors() {
  
    File file = new File(fileName);
    try{
    fr = new FileReader(file);
    br = new BufferedReader(fr);
    String line;
    while ((line = br.readLine()) != null) {
      line = line.trim();
      if(line.startsWith("Connector ")) {
	line = br.readLine().trim();
	line = br.readLine().trim();
	line = br.readLine().trim();
	int tempFigure1Int = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
	line = br.readLine().trim();
	int tempFigure2Int = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
	line = br.readLine().trim();
	line = br.readLine().trim();
	line = br.readLine().trim();
	line = br.readLine().trim();
	line = br.readLine().trim();
	String EndStyle1 = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
	line = br.readLine().trim();
	String EndStyle2 = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
	int hasMany = 0;
	if (EndStyle1.indexOf("many") >= 0) {
	  hasMany++;
	}
	if (EndStyle2.indexOf("many") >= 0) {
	  hasMany++;
	}
	if(hasMany < 2 && !EndStyle1.equals("null") && !EndStyle2.equals("null"))
	{
	  figure1TableIntArray.add(tempFigure1Int);
	  figure2TableIntArray.add(tempFigure2Int);
	}
	else if (tempFigure1Int > 0 && tempFigure2Int > 0 && EndStyle1.equals("null") && EndStyle2.equals("null"))
	{
	  figure1IntArray.add(tempFigure1Int);
	  figure2IntArray.add(tempFigure2Int);
	}
      }
     }
    }
    catch (FileNotFoundException ex) {
    System.out.println("Cannot find file");
    }
    catch (IOException ioe) {
      System.out.println(ioe);
    }

  }
  // Test to test that the parser is able to parse Edge
  // Tables from EDG File by seeing that the names of
  // the entities are the names of the edge Tables
  @Test
  public void testGetEdgeTables () {
  File file = new File(fileName);
  EdgeConvertFileParser Converter = new EdgeConvertFileParser(file);
  getTablesandFieldNames();
  EdgeTable[] edgeTables = Converter.getEdgeTables(); 
  for(int x =0; x < tableNames.size(); x++) {
    String expected = tableNames.get(x);
    boolean tableExists = false;
    for (int y =0; y < edgeTables.length; y++) {
	if (edgeTables[y].getName().equals(expected)) {
	tableExists = true;
	}
      }
    assertEquals(true, tableExists);
    }
  
  
  }
  // Test to test that the parser is able to parse the
  // Edge Fields from EDG File by seeing that the names
  // of the attributes are the names of the Edge Fields
  @Test
  public void testGetEdgeFields () {
  File file = new File(fileName);
  EdgeConvertFileParser Converter = new EdgeConvertFileParser(file);
  getTablesandFieldNames();
  EdgeField[] edgeFields = Converter.getEdgeFields();
  for(int x =0; x < fieldNames.size(); x++) {
    String expected = fieldNames.get(x);
    boolean fieldExists = false;
    for (int y =0; y < edgeFields.length; y++) {
    if (edgeFields[y].getName().equals(expected)) {
      fieldExists = true;
      }
    }
    assertEquals(true, fieldExists);
  }
  
  }

  // Test to test that the parser is able to parse the
  // connection between the tables and fields from EDG
  // File
  @Test
  public void testCheckConnectionsTableField () {
  File file = new File(fileName);
  EdgeConvertFileParser Converter = new EdgeConvertFileParser(file);
  getTableandFieldNumfromConnectors();
  EdgeTable[] edgeTables = Converter.getEdgeTables();
  EdgeField[] edgeFields = Converter.getEdgeFields();
  // Make arrays for EdgeTables
  for (int g =0; g < edgeTables.length; g++) {
    edgeTables[g].makeArrays();
  }
  for (int y =0; y < figure1IntArray.size(); y++) {
    int Figure1 = (Integer)figure1IntArray.get(y);
    int Figure2 = (Integer)figure2IntArray.get(y);
    EdgeTable edgeTable = null;
    EdgeField edgeField = null;
    //These check which one of the two figures are
    //tables
    boolean figure1isTable = false;
    boolean figure2isTable = false;
    //get the EdgeTable or EdgeField from both Figures
    for(int z =0; z < edgeTables.length; z++) {
      if (edgeTables[z].getNumFigure() == Figure1)
      {
	// check both Figures to see which one would get
	// the table
	if (edgeTable == null) {
	  figure1isTable = true;
	  edgeTable = edgeTables[z];
	} else {
	  fail("Both Figures in Connector were Tables");
	}
      } else if (edgeTables[z].getNumFigure() == Figure2) {
	if (edgeTable == null) {
	  figure2isTable = true;
	  edgeTable = edgeTables[z];
	} else {
	  fail("Both Figures in Connector were Tables");
	}
      }
    }
    for(int a =0; a < edgeFields.length; a++) {
      if (edgeFields[a].getNumFigure() == Figure1) {
	//Check both Figures to see which one would get
	//the field
	if (edgeField == null) {
	  edgeField = edgeFields[a];
	} else {
	    fail("Both Figures in Connector were Fields");
	}
      } else if (edgeFields[a].getNumFigure() == Figure2) {
	if (edgeField == null) {
	  edgeField = edgeFields[a];
	} else {
	    fail("Both Figures in Connector were Fields");
	}
      }
    }
    // if figure 1 is a Table, check that figure 2 is in
    // the NativeField array of the table and that the
    // Figure 1 is the int in the edgeFields tableID
    if (figure1isTable) {
      int[] fieldsinTable = edgeTable.getNativeFieldsArray();
      boolean isinfieldTable = false;
      for (int j = 0; j < fieldsinTable.length; j++) {
	if (fieldsinTable[j] == Figure2) {
	  isinfieldTable = true;
	}
      }
      assertEquals(true, isinfieldTable);
      assertEquals(Figure1, edgeField.getTableID());
      // Vice versa from above
    } else if (figure2isTable) {
      int[] fieldsinTable = edgeTable.getNativeFieldsArray();
      boolean isinfieldTable = false;
      for (int j = 0; j < fieldsinTable.length; j++) {
	if (fieldsinTable[j] == Figure1) {
	  isinfieldTable = true;
	}
      }
      assertEquals(true, isinfieldTable);
      assertEquals(Figure2, edgeField.getTableID());
    }
    }
  }

  //Test that the connection between Tables are made
  //from the Connectors
  @Test
  public void testCheckConnectionTableTable()
  {
    // look at the connection between the tables
    File file = new File(fileName);
    EdgeConvertFileParser Converter = new EdgeConvertFileParser(file);
    getTableandFieldNumfromConnectors();
    EdgeTable [] edgeTables = Converter.getEdgeTables();
    // make arrays for edgeTables
    for (int c = 0;c < edgeTables.length; c++) {
      edgeTables[c].makeArrays();
    }
    for(int e = 0; e < figure1TableIntArray.size(); e++) {
      int numFigForTable1 = (Integer)figure1TableIntArray.get(e);
      int numFigForTable2 = (Integer)figure2TableIntArray.get(e);
      EdgeTable edgeTable1;
      EdgeTable edgeTable2;
      boolean isIntable1 = false;
      boolean isIntable2 = false;
      // find both tables from the 2 numFigures and see
      // if the other is in the relatedTablesArray
      for (int h =0; h < edgeTables.length; h++) {
      if (edgeTables[h].getNumFigure() == numFigForTable1) {
	edgeTable1 = edgeTables[h];
	int[] relatedTablesArray = edgeTable1.getRelatedTablesArray();
	for(int l = 0; l < relatedTablesArray.length; l++) {
	  // see if the relatedTablesArray has the num
	  // in it 
	  if (relatedTablesArray[l] == numFigForTable2) {
	    isIntable1 = true;
	  }
	}
	
      }
      if (edgeTables[h].getNumFigure() == numFigForTable2) {
	edgeTable2 = edgeTables[h];
	int[] relatedTablesArray = edgeTable2.getRelatedTablesArray();
	for(int p =0; p < relatedTablesArray.length; p++) {
	  if (relatedTablesArray[p] == numFigForTable1) {
	  isIntable2 = true;
	  }
	}
      }
    }
    assertEquals(true, isIntable1);
    assertEquals(true, isIntable2);
  }
  }
}
