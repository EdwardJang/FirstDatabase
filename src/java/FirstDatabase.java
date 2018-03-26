import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FirstDatabase {

    public static void main(String[] args) {
        FirstDatabase a_database = new FirstDatabase();
        List<String[]> list_of_nodes = new ArrayList<String[]>();
        List<String[]> list_of_edges = new ArrayList<String[]>();

        list_of_nodes = a_database.parseCsvFile("./src/resources/MapBnodes.csv");
        list_of_edges = a_database.parseCsvFile("./src/resources/MapBedges.csv");

        System.out.println("-------- Oracle JDBC Connection Testing ------");
        System.out.println("-------- Step 1: Registering Oracle Driver ------");
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your Oracle JDBC Driver? Did you follow the execution steps. ");
            System.out.println("");
            System.out.println("*****Open the file and read the comments in the beginning of the file****");
            System.out.println("");
            e.printStackTrace();
            return;
        }

        System.out.println("Oracle JDBC Driver Registered Successfully !");

        //-------------------Print parsed array--------------------------
        // This portion can be used to send each row to database also.
        String node_id;
        String xcoord;
        String ycoord;
        String floor;
        String building;
        String node_type;
        String long_name;
        String short_name;
        String team_assigned;

        Iterator<String[]> iterator = list_of_nodes.iterator();
        iterator.next(); // get rid of header of csv file

        try {
            Connection connection = null;
            connection = DriverManager.getConnection("jdbc:derby:./src/resources/NodesDB2;create=true");

            //delete table
            System.out.println("Deleting table...");
            Statement stmt = connection.createStatement();

            String delete_sql = "DROP TABLE map_nodes";
            stmt.executeUpdate(delete_sql);
            System.out.println("Table deleted successfully...");

            //create table
            System.out.println("Creating table...");
            String create_sql = "CREATE TABLE map_nodes (" +
                    " node_id             CHAR(10) PRIMARY KEY," +
                    "  xcoord              INTEGER," +
                    "  ycoord              INTEGER," +
                    "  floor               INTEGER," +
                    "  building            VARCHAR(255)," +
                    "  node_type           VARCHAR(4)," +
                    "  long_name           VARCHAR(255)," +
                    "  short_name          VARCHAR(255)," +
                    "  team_assigned       VARCHAR(255))";

            stmt.executeUpdate(create_sql);
            System.out.println("Table created successfully...");

            //insert data for every row
            while (iterator.hasNext()) {
                String[] node_row = iterator.next();
                node_id = node_row[0];
                xcoord = node_row[1];
                ycoord = node_row[2];
                floor = node_row[3];
                building = node_row[4];
                node_type = node_row[5];
                long_name = node_row[6];
                short_name = node_row[7];
                team_assigned = node_row[8];
                System.out.println("row is: " + node_id + " " + xcoord + " " + ycoord + " " + floor + " " + building + " " + node_type + " " + long_name + " " + short_name + " " + team_assigned);

                // Add to the database table
                String str = "INSERT INTO map_nodes(node_id,xcoord,ycoord,floor,building,node_type,long_name,short_name,team_assigned) values (?,?,?,?,?,?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(str);
                statement.setString(1, node_id);
                statement.setInt(2, Integer.parseInt(xcoord));
                statement.setInt(3, Integer.parseInt(ycoord));
                statement.setInt(4, Integer.parseInt(floor));
                statement.setString(5, building);
                statement.setString(6, node_type);
                statement.setString(7, long_name);
                statement.setString(8, short_name);
                statement.setString(9, team_assigned);
                statement.executeUpdate();
            }// while loop ends

            System.out.println("----------------------------------------------------");
            Iterator<String[]> iterator2 = list_of_edges.iterator();
            iterator2.next(); // get rid of the header

            //delete table
            System.out.println("Deleting table...");
            delete_sql = "DROP TABLE map_edges";
            stmt.executeUpdate(delete_sql);
            System.out.println("Database table successfully...");

            //create table
            System.out.println("Creating table...");
            create_sql = "CREATE TABLE map_edges (" +
                    "  node_id              VARCHAR(255)," +
                    "  start_node           VARCHAR(255)," +
                    "  end_node             VARCHAR(255))";

            stmt.executeUpdate(create_sql);
            System.out.println("table created successfully...");

            //insert rows
            while (iterator2.hasNext()) {
                String[] node_row = iterator2.next();
                System.out.println("row is: " + node_row[0] + " " + node_row[1] + " " + node_row[2]);

                String str = "INSERT INTO map_edges(node_id,start_node, end_node) values (?,?,?)";
                PreparedStatement statement = connection.prepareStatement(str);
                statement.setString(1, node_row[0]);
                statement.setString(2, node_row[1]);
                statement.setString(3, node_row[2]);
                statement.executeUpdate();
            }
        }// try ends
        catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        } // catch ends
    }// main ends

    //-----------------------------parse csv file------------------------------
    //http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html
    //https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
    public List<String[]> parseCsvFile(String csv_file_name) {

        List<String[]> list_of_rows = new ArrayList<String[]>();

        try {
            File file = new File(csv_file_name);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                // use comma as separator
                String[] node_row = line.split(",");
                list_of_rows.add(node_row);
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list_of_rows;
    }//parseCsvFile() ends

} // class ends

