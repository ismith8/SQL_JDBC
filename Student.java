import java.io.*;
import java.sql.*;
import java.util.*;
import oracle.jdbc.driver.*;
 


public class Student {
	
	// Attributes for connection
    private static Connection con;
    private static Statement stmt;
    private static GUI gui;
    protected static boolean run;
    
    // Attribute for gathering rows
    private static List<String[]> data;
    
    public static void main(String argv[]) {
    	connectToDatabase();
    	generateOutput();
    	gui = new GUI();
    	menu();
    	return;
    }
    
    private static void menu() {
    	
		boolean main = true;
		
		while(main) {
			System.out.println("\n\n--------------------------------------------");
			System.out.println("What would you like to do?");
			System.out.println("1. Clear all data");
			System.out.println("2. Create tables");
			System.out.println("3. Fill tables with data");
			System.out.println("4. Generate output");
			System.out.println("5. Exit\n\n");
			
			Scanner userInput = new Scanner(System.in);
			int input = Integer.parseInt(userInput.nextLine());
			
			if (input > 5 || input < 1) {
				System.out.println("Invalid input, try again");
				continue;
	   		}
			else if (input == 1) { 
				System.out.println("Clearing data ... ");
				clearData(); 
				System.out.println("Cleared.");
				continue; 
			}
			else if (input == 2) {
				System.out.println("Creating tables ... ");
				instantiateTables(); 
				System.out.println("Created.");	
				continue; 
			}
			else if (input == 3) { 
				System.out.println("Filling data ... ");
				fillData(); 
				System.out.println("Filled.");
				continue; 
			}
			else if (input == 4) {
				System.out.println("Generating output ... ");
				generateOutput();
				System.out.println("Output generated, saved to output.txt.");
				continue;
			}
			else if (input == 5) {
				System.out.println("Exiting.");
				main = false;			
				break;
			}
		}
    }

    /*
     * Used to connect to the SQL database
     * MUST BE RUN
     */
    private static void connectToDatabase()
    {
	String driverPrefixURL="jdbc:oracle:thin:@";
        String username=null;
        String password=null;
        String dataSource=null;
        try{
	    //Register Oracle driver
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        } catch (Exception e) {
        System.out.println("Failed to load JDBC/ODBC driver.");
        return;
     }
       try{
		   //look for resource file 'odbc.datasource'
	        InputStream is=ClassLoader.getSystemResourceAsStream("odbc.datasource");
			Properties p=new Properties();
			p.load(is);
			dataSource=p.getProperty("datasource.name");
			if (dataSource==null) throw new Exception();
			 username=p.getProperty("datasource.username","");
			 password=p.getProperty("datasource.password","");
			}
			 catch (Exception e){
			   System.out.println("Unable to read resouce file to get data source");
			return;
			}
       
			
			try{
			System.out.println(driverPrefixURL+dataSource);
			con=DriverManager.getConnection(driverPrefixURL+dataSource, username, password);
			DatabaseMetaData dbmd=con.getMetaData();
			stmt=con.createStatement();
			
			System.out.println("Connected.");
			
			if(dbmd==null){
			  System.out.println("No database meta data");
			}
			
			else {
			   System.out.println("Database Product Name: "+dbmd.getDatabaseProductName());
			   System.out.println("Database Product Version: "+dbmd.getDatabaseProductVersion());
			   System.out.println("Database Driver Name: "+dbmd.getDriverName());
			   System.out.println("Database Driver Version: "+dbmd.getDriverVersion());
			}
			
			}catch( Exception e) {e.printStackTrace();}
			System.out.println("--------------------------------------------\n\n");
	 return;
    }
    
    /*
     * Used to clear any existing data in the db
     * Prevents errors from occuring when running 
     * the program multiple times
     */
    private static void clearData() {
	
	try {
		// Clear all rows from Ratings
		String sql = "DELETE FROM Ratings";
		stmt.executeUpdate(sql);
		System.out.println("Deleted all rows from Ratings\n");
 	} catch (Exception e) {System.out.println("No rows to delete in Ratings\n");}
	
	try {
		// Clear all rows from Movies
		String sql = "DELETE FROM Movies";
		stmt.executeUpdate(sql);
		System.out.println("Deleted all rows from Movies\n");
	} catch (Exception e) {System.out.println("No rows to delete in Movies\n");}
	
	try {
		// Drop table ratings
		String sql = "DROP TABLE Ratings cascade constraints";
		stmt.executeUpdate(sql);
		System.out.println("Dropped existing Ratings table\n");
     	} catch (Exception e) {System.out.println("No table Ratings to drop\n");}
	
	try {
		// Drop table movies
		String sql = "DROP TABLE Movies cascade constraints";
		stmt.executeUpdate(sql);
		System.out.println("Dropped existing table Movies\n");
     	} catch (Exception e) {System.out.println("No table Movies to drop\n");}
     }


    /*
     * Instanciates all the SQL
     * tables needed for this database
     */
    private static void instantiateTables() {

	try {
		// Create Movies table
		String sql = "CREATE TABLE Movies " +
		     "(MovieID int, " +
		     "Title varchar(800), " + 
		     "Language varchar(2), " +
		     "Production_company varchar(800), " +
		     "Production_country varchar(800), " +
		     "Release_date DATE, " + 
		     "Runtime int)";
		stmt.executeUpdate(sql);
		System.out.println("Created table Movies\n");
	} catch (Exception e) {
		System.out.println("Could not create table, aborting\n");
		return; 
    	}
	
 	try {	
		// Add primary key
		String sql = "ALTER TABLE Movies " + 
				"ADD PRIMARY KEY(MovieID)";
		stmt.executeUpdate(sql);
	 	System.out.println("Added primary key MovieID to Movies\n");
	} catch ( Exception e ) {
    	System.out.println("Could not add primary key to Movies, aborting\n");
		return;
       }


	try {
		// Create Ratings table
		String sql = "CREATE TABLE Ratings " + 
			"(ReviewerID int, " + 
			"MovieID int, " + 
			"Rating float)";
		stmt.executeUpdate(sql);
		System.out.println("Created table Ratings");
	} catch ( Exception e) {
		System.out.println("Could not create table Ratings, aborting\n");
		return;
	}
	
	try {
		// Add Ratings foreign key
		String sql = "ALTER TABLE Ratings " + 
		     "ADD FOREIGN KEY(MovieID) REFERENCES Movies(MovieID)";
		stmt.executeUpdate(sql);
		System.out.println("Added foreign key to Ratings\n");
	} catch ( Exception e ) {
		System.out.println("Could not create Ratings foreign key, aborting\n");
		return;
	   }
    }

    /*
     * Reads movies from movies.csv and
     * Ratings from reviews.csv. Fills 
     * the data by iterating through and
     * creating callable SQL statements
     */
    private static void fillData() {
	
	// Fill Movies
	int i = 0;
	int count = 0;
	
	try {
		File file = new File("movies.csv");
		Scanner readr = new Scanner(file);
		String words[] = {"", "", "", "", "", "", ""};
		String line = "";
		line = readr.nextLine();
		
		while (readr.hasNextLine()) {
			
			if (count % 1000 == 0) { System.out.print(". "); }
	
			//try {
			line = readr.nextLine();	
			
			// The formatting of the input data is not consistant
			// enough for a word.split() method, using a loop 
			// to assure maximum data is kept
			String temp = "";
			int index = 1;
			char c;
			char[] spcl_chrs = {',', '\"', '\''};
			int e = 0;
			
			while (e < line.length()) {
				c = line.charAt(e);
				
				// Checks for building word NULL
				if (temp.equals("NUL") && (c == 'L')) {
					words[index] = "NULL_VALUE";
					index++;
					temp = "";
					e+=2;
					continue;
				}
			
				// End of entry
				else if (c == spcl_chrs[0]) { 
					words[index] = temp;	
					index++;
					temp = "";
					e++;
					continue;
				}
				// if char is ", read up to next " as one entry
				else if (c == spcl_chrs[1]) {
					e++; 
					c = line.charAt(e);
					temp = "";
					
					boolean brk = true;
					while (brk) {
						// Special handeling for appostrophe
						// utilizes SQL '' read as '
						if (c == spcl_chrs[2]) { 
							temp += c;	
							temp += c;
							e++;
							c = line.charAt(e);
							continue; 
						}
						// Special handeling for double quotes
						else if (c == spcl_chrs[1]) {
							// Special handleing
							if (line.charAt(e+1) == spcl_chrs[1]) {
								temp += "\"";
								e+=2;
								c = line.charAt(e);
								continue;
							}
							else { brk = false; }
						}
						else {
							temp += c;
							e++;
							c = line.charAt(e);
						}
					}
					words[index] = temp;
					index++;
					temp = "";
					e+=2;
					continue;
				}
				else { 
					if (c == spcl_chrs[2]) { temp += c; }
					temp += c; 
					e++; 
				}
	
			} // end of inner loop
			 words[0] = temp;	
	
			// Fix dates
			String[] temp_date = words[5].split("/");
			String adj_date= "";
		
			if (temp_date[2].length() == 2) {
				int temp_year = Integer.parseInt(temp_date[2]);			
				adj_date += temp_date[0];
				adj_date += "/";
				adj_date += temp_date[1];
				adj_date += "/";
				if (temp_year == 0) { adj_date += "2000"; }
				else if (temp_year <= 9) {
					adj_date += "200";
					adj_date += temp_year;
				}
				else if (temp_year <= 21 && temp_year > 9) { 
					adj_date += "20"; 
					adj_date += temp_year;
				}
				else { 
					adj_date += "19"; 
					adj_date += temp_year;
				}
				words[5] = adj_date;
			}
	
			// Insert into table
			String sql = "INSERT INTO Movies VALUES(\'";
				if (words[0] == null || words[0] == "0") { 
					throw new Exception("No movie id, failed to add entry"); 
				}
				else { sql += words[0] + "\',"; }
					
				if (words[1] == "NULL_VALUE") { sql += "NULL,"; }
				else { sql += "\'" + words[1] + "\',"; }
			
				if (words[2] == "NULL_VALUE") { sql += "NULL,"; }
				else { sql += "\'" + words[2] + "\',"; }
			
				if (words[3] == "NULL_VALUE") { sql += "NULL,"; }
				else { sql += "\'" + words[3] + "\',"; }
			
				if (words[4] == "NULL_VALUE") { sql += "NULL,"; }
				else { sql += "\'" + words[4] + "\',"; }
				
				sql += "TO_DATE(\'" + words[5] + "\', \'MM/DD/YYYY\'),";
				
				if (words[6] == "NULL_VALUE") { sql += "NULL)"; }
				else { sql += "\'" + words[6] + "\')"; }
		
	
			try { stmt.executeUpdate(sql); i++;	}
			// end of inner try
			catch (Exception ee) { 
				System.out.print("\nCould not add line ");
				System.out.print(i);
				System.out.print(": ");
				System.out.print(line);
				System.out.print("--------------------------------------------------------\n\n");
				ee.printStackTrace();
				return;
			}
			count++;
		} // end of outer while loop
		readr.close();
	} // end of outer try 
	catch (Exception ec) { ec.printStackTrace(); return; }

	System.out.println("\n");
	count = 0;

	System.out.print("Filled Movies with ");
	System.out.print(i);
	System.out.print(" rows\n\n");

	// Fill reviews
	i = 0;
	try {
	File file2 = new File("ratings.csv");
	Scanner readr2 = new Scanner(file2);
	String line = "";
	String words[];
	

	line = readr2.nextLine();
	while (readr2.hasNextLine()) {

		if (count % 1000 == 0) { System.out.print(". "); }

		line = readr2.nextLine();
		words = line.split(",");
		
		int userId = Integer.parseInt(words[0]);

		if (words.length != 3) continue;
		if (Integer.parseInt(words[0]) < 0 || words[0] == null) continue;
		if (words[1] == null) continue;
		if (Float.parseFloat(words[2]) > 5 || Float.parseFloat(words[2]) < 1)
		if(words[2] == null) continue;
		String sql = "INSERT INTO Ratings " + 
			     "VALUES('" + words[0] + "', '" +
			     words[1] + "', '" + words[2] + "')";
		
		stmt.executeUpdate(sql);
		i++;
		
		count++;
	} // end of while

	readr2.close();
        } // end of try
	catch (Exception e) { e.printStackTrace(); return; }


	System.out.print("\n\nFilled Reviews with ");
	System.out.print(i);
	System.out.print(" rows\n\n");
    }

    /*
     * Queries the data for the description of tables
     * and the count in each
     *
     */
    private static void generateOutput() {

	try {
		// Open the file for writing
		File output_file = new File("output.txt");	
		
		if (output_file.createNewFile()) { System.out.println("Created file"); }
		else { System.out.println("File already exists, opening"); }
		
		FileWriter fw = new FileWriter("output.txt");
		ResultSet queryOutput;
		String output = "";
		String sql = "";
		
		// Describe Movies
		output = describeTable("Movies");		
		fw.write(output);
		fw.write("\n");
	
		// Count Movies
		sql = "SELECT COUNT(*) FROM Movies";
		try { queryOutput = stmt.executeQuery(sql); }
		catch (Exception e) {
			System.out.println("Query failed");
			fw.close();
			return;
		}
		queryOutput.next();
		fw.write("Count: ");
		fw.write(queryOutput.getString(1));
		fw.write("\n\n");
		
		// Describe Ratings 
		output = describeTable("Ratings");
		fw.write(output);
		fw.write("\n");
		
		// Count Ratings
		sql = "SELECT COUNT(*) FROM Ratings";
		try { queryOutput = stmt.executeQuery(sql); }
		catch (Exception e) {
			System.out.println("Query failed");
			fw.close();
			return;
		}
		queryOutput.next();
		fw.write("Count: ");
		fw.write(queryOutput.getString(1));
		fw.write("\n\n");
	
		// Close file
		fw.close();
		
	  } catch (Exception e) {
		System.out.println("File reader failed, aborting");
		e.printStackTrace();
		return;
	  }	
    }
        
	/* 
	 * Responsible for creating the search based on 
	 * the inputs from the user 
	 */
	protected static Object[][] processInput(String[] inputs) {
						
		// Stores values from query
		data = new ArrayList<String[]>();
		List<String> movieIds;
		int null_count = 0;		
		
		// Grab all the data from Movies table
		
		String sql = "SELECT * FROM Movies WHERE (";
		String[] colNames = { "movieid", "title", "language", "production_company",
				"production_country", "release_date", "runtime" };
		
		// CREATE QUERY
		int num_conditions = 0;
		// Loop for each column 
		
		System.out.println("\nInputs from user: ");
		for (int i = 0; i < 7; i++) {

			System.out.println(inputs[i]);
			
			if (inputs[i] == null) { null_count++; continue; }
			else { num_conditions++; }
			if (num_conditions > 1) { sql += " AND "; }
			sql += colNames[i];
			if (inputs[i].contains("%"))  { sql += " LIKE \'"; }
			else { sql += " = \'"; }
			sql += inputs[i];
			sql += "\'";
		}
		System.out.println("\n");
		sql += ")";

		
		// If there is no input, return all rows
		if (null_count == 7) {
			System.out.println("No user parameters");
			sql = "SELECT * FROM movies";
		}		
		
		movieIds = getRows(sql);
		getAvgRating(movieIds);
		
		Object[][] obj_arr = new String[data.size()][8];
		
		// Converts to Object[][]
		for (int i = 0; i < data.size(); i++) {
			for (int e = 0; e < 8; e++) {
				obj_arr[i][e] = data.get(i)[e];
			}
		}
		return obj_arr;
	}
	
	/*
	 * Helper function for processInput
	 * Takes in a ResultSet and creates 
	 * String[] of row data and leaves
	 * empty space for rating calculation
	 * @return List<String> movieIds
	 */
	private static List<String> getRows(String sql_stmt) {
			
		List<String>  movieIds = new ArrayList<String>();
		ResultSet rows;
		String[] row = new String[8];
		String temp;
		int id;
		
		System.out.println("\n\nGet rows query:");
		System.out.println(sql_stmt);

		try { rows = stmt.executeQuery(sql_stmt); }
		catch(Exception e) { System.out.println("Query failed"); return null; }
		
		// Check for null
		if (rows == null) {
			System.out.println("No data present");
			return null;
		}
				
		try {
			while(rows.next()) {
				id = rows.getInt("movieid");
				temp = String.valueOf(id);
				movieIds.add(temp);
				
				row[0] = temp;
				row[1] = rows.getString("title");
				row[2] = rows.getString("language");
				row[3] = rows.getString("production_company");
				row[4] = rows.getString("production_country");
				row[5] = String.valueOf(rows.getDate("release_date"));
				row[6] = String.valueOf(rows.getInt("runtime"));
				row[7] = "";
				
				data.add(row);
				row = new String[8];
			}
		} catch (Exception e) { e.printStackTrace(); }
		
		System.out.print("\nAdded ");
		System.out.print(data.size());
		System.out.println(" rows to data, sans the 8th spot\n");
		return movieIds;
	}
	
	
	/*
	 * Takes in an ArrayList of movie IDs and 
	 * queries the ratings table for matching 
	 * entires, then calculates the average
	 * and adds to data array
	 */
	private static void getAvgRating(List<String> movieIds) {
		
		ResultSet rs = null;
		String sql = "SELECT * FROM Ratings WHERE MovieID = \'";
		String temp = "";
		float avgRating = 0;
		int count = 0;
		
		System.out.println("\n\nAvergae Ratings Queries:");
		for (int i = 0; i < data.size(); i++) {
			if (movieIds.contains(data.get(i)[0])) {

				temp += sql;
				temp += data.get(i)[0];
				temp += "\'";
				System.out.println(temp);
				
				try { 
					rs = stmt.executeQuery(temp);
					avgRating = 0;
					count = 0;
					
					while (rs.next()) {
						avgRating += rs.getFloat("Rating");
						count++;
					}
				}
				catch (Exception e) {
					System.out.print("Query on: ");
					System.out.print(data.get(i)[0]);
					System.out.println(" failed."); 
					continue;
				}
				
				avgRating /= count;
				temp = String.format("%.2f", avgRating);
				data.get(i)[7] = temp;
				temp = "";
			}
			
			System.out.print("Added ");
			System.out.print(count);
			System.out.println(" reviews to the table");
		} return;
	}
	
	
	/*
     * Helper function used by generateOutput to 
     * clean up the function and reuse complex code
     * @return generated String to be written
     */
    private static String describeTable(String tableName) throws SQLException {
    	
		String sql = "SELECT * FROM ";
		ResultSet output;	
		
		if (tableName != null || tableName != "") { sql += tableName; }
		
		try { output = stmt.executeQuery(sql); }
		catch (Exception e) {
			System.out.println("Query failed");
			return null;
		}
		
		// Get col names and types
		ResultSetMetaData outputData = output.getMetaData();
		String describeOutput = "Name                ";
				describeOutput += "    Null            ";
				describeOutput += "Type    \n";
				describeOutput += "------------------------------------------------------------\n";
		String temp = "";
		
		int lng = 0;
		for (int i = 1; i <= outputData.getColumnCount(); i++) {
			
			temp += outputData.getColumnName(i);
			// Helps to make it formatted nicer
			lng = outputData.getColumnName(i).length();
			lng -= 20;
			lng = Math.abs(lng);
						
			while(lng > 0) { temp += " "; lng--; }
						
			// Checks for key value
			if(!(outputData.columnNullable == outputData.isNullable(i))) {
				temp += "    not null        ";
			} else { temp += "                    "; }
			
			temp += outputData.getColumnTypeName(i);
			temp += "\n"; 
			describeOutput += temp;
			temp = "";
		}
		return describeOutput;
    }
    
    
}


