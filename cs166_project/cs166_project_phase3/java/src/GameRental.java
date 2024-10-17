/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


 import java.sql.DriverManager;
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.io.File;
 import java.io.FileReader;
 import java.io.BufferedReader;
 import java.io.InputStreamReader;
 import java.util.List;
 import java.util.ArrayList;
 import java.lang.Math;
 import java.sql.Timestamp; //helper for updating tracking info
 import java.time.LocalDateTime; // helper for updating tracking info 
 import java.util.Random; // helper for random in rental order id

 /**
  * This class defines a simple embedded SQL utility class that is designed to
  * work with PostgreSQL JDBC drivers.
  *
  */
 public class GameRental {
 
    // reference to physical database connection.
    private Connection _connection = null;
 
    // handling the keyboard inputs through a BufferedReader
    // This variable can be global for convenience.
    static BufferedReader in = new BufferedReader(
                                 new InputStreamReader(System.in));
 
    /**
     * Creates a new instance of GameRental store
     *
     * @param hostname the MySQL or PostgreSQL server hostname
     * @param database the name of the database
     * @param username the user name used to login to the database
     * @param password the user login password
     * @throws java.sql.SQLException when failed to make a connection.
     */
    public GameRental(String dbname, String dbport, String user, String passwd) throws SQLException {
 
       System.out.print("Connecting to database...");
       try{
          // constructs the connection URL
          String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
          System.out.println ("Connection URL: " + url + "\n");
 
          // obtain a physical connection
          this._connection = DriverManager.getConnection(url, user, passwd);
          System.out.println("Done");
       }catch (Exception e){
          System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
          System.out.println("Make sure you started postgres on this machine");
          System.exit(-1);
       }//end catch
    }//end GameRental
 
    /**
     * Method to execute an update SQL statement.  Update SQL instructions
     * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
     *
     * @param sql the input SQL string
     * @throws java.sql.SQLException when update failed
     */
    public void executeUpdate (String sql) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the update instruction
       stmt.executeUpdate (sql);
 
       // close the instruction
       stmt.close ();
    }//end executeUpdate
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and outputs the results to
     * standard out.
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQueryAndPrintResult (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);
 
       /*
        ** obtains the metadata object for the returned result set.  The metadata
        ** contains row and column info.
        */
       ResultSetMetaData rsmd = rs.getMetaData ();
       int numCol = rsmd.getColumnCount ();
       int rowCount = 0;
 
       // iterates through the result set and output them to standard out.
       boolean outputHeader = true;
       while (rs.next()){
        if(outputHeader){
          for(int i = 1; i <= numCol; i++){
          System.out.print(rsmd.getColumnName(i) + "\t");
          }
          System.out.println();
          outputHeader = false;
        }
          for (int i=1; i<=numCol; ++i)
             System.out.print (rs.getString (i) + "\t");
          System.out.println ();
          ++rowCount;
       }//end while
       stmt.close();
       return rowCount;
    }//end executeQuery
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the results as
     * a list of records. Each record in turn is a list of attribute values
     *
     * @param query the input query string
     * @return the query result as a list of records
     * @throws java.sql.SQLException when failed to execute the query
     */
    public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);
 
       /*
        ** obtains the metadata object for the returned result set.  The metadata
        ** contains row and column info.
        */
       ResultSetMetaData rsmd = rs.getMetaData ();
       int numCol = rsmd.getColumnCount ();
       int rowCount = 0;
 
       // iterates through the result set and saves the data returned by the query.
       boolean outputHeader = false;
       List<List<String>> result  = new ArrayList<List<String>>();
       while (rs.next()){
         List<String> record = new ArrayList<String>();
       for (int i=1; i<=numCol; ++i)
          record.add(rs.getString (i));
         result.add(record);
       }//end while
       stmt.close ();
       return result;
    }//end executeQueryAndReturnResult
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the number of results
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQuery (String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement ();
 
        // issues the query instruction
        ResultSet rs = stmt.executeQuery (query);
 
        int rowCount = 0;
 
        // iterates through the result set and count nuber of results.
        while (rs.next()){
           rowCount++;
        }//end while
        stmt.close ();
        return rowCount;
    }
 
    /**
     * Method to fetch the last value from sequence. This
     * method issues the query to the DBMS and returns the current
     * value of sequence used for autogenerated keys
     *
     * @param sequence name of the DB sequence
     * @return current value of a sequence
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int getCurrSeqVal(String sequence) throws SQLException {
    Statement stmt = this._connection.createStatement ();
 
    ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
    if (rs.next())
       return rs.getInt(1);
    return -1;
    }
 
    /**
     * Method to close the physical connection if it is open.
     */
    public void cleanup(){
       try{
          if (this._connection != null){
             this._connection.close ();
          }//end if
       }catch (SQLException e){
          // ignored.
       }//end try
    }//end cleanup
 
    /**
     * The main execution method
     *
     * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
     */
    public static void main (String[] args) {
       if (args.length != 3) {
          System.err.println (
             "Usage: " +
             "java [-classpath <classpath>] " +
             GameRental.class.getName () +
             " <dbname> <port> <user>");
          return;
       }//end if
 
       Greeting();
       GameRental esql = null;
       try{
          // use postgres JDBC driver.
          Class.forName ("org.postgresql.Driver").newInstance ();
          // instantiate the GameRental object and creates a physical
          // connection.
          String dbname = args[0];
          String dbport = args[1];
          String user = args[2];
          esql = new GameRental (dbname, dbport, user, "");
 
          boolean keepon = true;
          while(keepon) {
             // These are sample SQL statements
             System.out.println("MAIN MENU");
             System.out.println("---------");
             System.out.println("1. Create user");
             System.out.println("2. Log in");
             System.out.println("9. < EXIT");
             String authorisedUser = null;
             switch (readChoice()){
                case 1: CreateUser(esql); break;
                case 2: authorisedUser = LogIn(esql); break;
                case 9: keepon = false; break;
                default : System.out.println("Unrecognized choice!"); break;
             }//end switch
             if (authorisedUser != null) {
               boolean usermenu = true;
               while(usermenu) {
                 System.out.println("\nMAIN MENU");
                 System.out.println("---------");
                 System.out.println("1. View Profile");
                 System.out.println("2. Update Profile");
                 System.out.println("3. View Catalog");
                 System.out.println("4. Place Rental Order");
                 System.out.println("5. View Full Rental Order History");
                 System.out.println("6. View Past 5 Rental Orders");
                 System.out.println("7. View Rental Order Information");
                 System.out.println("8. View Tracking Information");
 
                 //the following functionalities basically used by employees & managers
                 System.out.println("9. Update Tracking Information");
 
                 //the following functionalities basically used by managers
                 System.out.println("10. Update Catalog");
                 System.out.println("11. Update User");
 
                 System.out.println(".........................");
                 System.out.println("20. Log out");
                 switch (readChoice()){
                    case 1: viewProfile(esql, authorisedUser); break;
                    case 2: updateProfile(esql, authorisedUser); break;
                    case 3: viewCatalog(esql, authorisedUser); break;
                    case 4: placeOrder(esql, authorisedUser); break;
                    case 5: viewAllOrders(esql, authorisedUser); break;
                    case 6: viewRecentOrders(esql, authorisedUser); break;
                    case 7: viewOrderInfo(esql, authorisedUser); break;
                    case 8: viewTrackingInfo(esql, authorisedUser); break;
                    case 9: updateTrackingInfo(esql, authorisedUser); break;
                    case 10: updateCatalog(esql, authorisedUser); break;
                    case 11: updateUser(esql, authorisedUser); break;
 
 
 
                    case 20: usermenu = false; break;
                    default : System.out.println("Unrecognized choice!"); break;
                 }
               }
             }
          }//end while
       }catch(Exception e) {
          System.err.println (e.getMessage ());
       }finally{
          // make sure to cleanup the created table and close the connection.
          try{
             if(esql != null) {
                System.out.print("Disconnecting from database...");
                esql.cleanup ();
                System.out.println("Done\n\nBye !");
             }//end if
          }catch (Exception e) {
             // ignored.
          }//end try
       }//end try
    }//end main
 
    public static void Greeting(){
       System.out.println(
          "\n\n*******************************************************\n" +
          "              User Interface      	               \n" +
          "*******************************************************\n");
    }//end Greeting
 
    /*
     * Reads the users choice given from the keyboard
     * @int
     **/
    public static int readChoice() {
       int input;
       // returns only if a correct value is given.
       do {
          System.out.print("\nPlease make your choice: ");
          try { // read the integer, parse it and break.
             input = Integer.parseInt(in.readLine());
             break;
          }catch (Exception e) {
             System.out.println("Your input is invalid!");
             continue;
          }//end try
       }while (true);
       return input;
    }//end readChoice
 
 
 
 
     // account setup: user needs to provide necessary information
     // user will automatically be a customer
     // favorite games will be empty
     // numOverdueGames will be 0
    public static void CreateUser(GameRental esql){
       try {
          // prompting user for input
          System.out.print("\n \tACCOUNT SETUP\n");
          System.out.print("\tEnter login: ");
          String user_login = in.readLine();
          System.out.print("\tEnter password: ");
          String user_password = in.readLine(); 
          System.out.print("\tEnter phone number: ");
          String user_phonenumber = in.readLine();

          // checking if login already exists
          String login_query = String.format("SELECT * FROM Users WHERE login = '%s';", user_login);
          int userTrue = esql.executeQuery(login_query);
 
          if (userTrue > 0) {
            System.out.print("\nLogin already taken. Please try again.\n");
            return;
         }
 
          // initializing query
          String add_user_query = String.format("INSERT INTO Users(login, password, role, favGames, phoneNum, numOverDueGames) VALUES('%s', '%s', 'customer', NULL, '%s', 0)", user_login, user_password, user_phonenumber);
         
          // executing query
          esql.executeUpdate(add_user_query);
          System.out.print("\nUser succesfully created. Welcome, " + user_login + "! \n");
          System.out.print("\n");
 
       } catch(Exception e){
          System.err.println (e.getMessage ());
       }
    }//end CreateUser
 
     // check log in credentials for an existing user, return user login or null if the user DNE
    public static String LogIn(GameRental esql){
       try {
          // prompting user for login info
          System.out.print("\n");
          System.out.print("\tEnter login: ");
          String user_login = in.readLine();
          System.out.print("\tEnter password: ");
          String user_password = in.readLine(); 
 
          // initializing sql query
          String login_query = String.format("SELECT * FROM Users WHERE login = '%s' AND password = '%s';", user_login, user_password);
 
          // executing query
          int userTrue = esql.executeQuery(login_query);
 
          // checking if login exists
          if (userTrue > 0) {
             System.out.print("\nLogin successful. Welcome, " + user_login + "!\n");
             return user_login;
          }
          else {
             System.out.print("\nLogin unsuccesful. Please re-enter your login/password.\n");
             return null;
          }
 
       } catch(Exception e){
          System.err.println (e.getMessage ());
          return null;
       }
    } //end logIn
 
    // view favGames, numOverdueGames, and phoneNum
    public static void viewProfile(GameRental esql, String user_login) {
       try{
          // skip line
          System.out.print("\n");
 
          // view favGames
          String view_favGames = String.format("SELECT favGames AS \"Favorite Games\" FROM Users U WHERE U.login = '%s'", user_login);
          esql.executeQueryAndPrintResult(view_favGames);
          System.out.print("\n");
 
          // view numOverDueGames
          String view_numOverDueGames = String.format("SELECT numOverDueGames AS \"Number of Overdue Games:\" FROM Users U WHERE U.login = '%s'", user_login);
          esql.executeQueryAndPrintResult(view_numOverDueGames);
          System.out.print("\n");
 
          // view phoneNum
          String view_phoneNum = String.format("SELECT phoneNum AS \"Phone Number\" FROM Users U WHERE U.login = '%s'", user_login);
          esql.executeQueryAndPrintResult(view_phoneNum);
 
       } catch(Exception e){
       System.err.println (e.getMessage ());
    }
 } // end viewProfile
 
    // users are able to: update favgames, change password, change phoneNum
    // ONLY managers are able to: edit users login, role, or numoverduegames
    public static void updateProfile(GameRental esql, String user_login) {
      try{ 
         boolean update = true; 

         while(update == true) { // iterate while user wants to update
            System.out.print("\n What would you like to update? \n1. Favorite Games \n2. Password \n3. Phone Number \n");
            switch(readChoice()) {
               case 1: System.out.print("\nFavorite Games: ");
               String new_games = in.readLine();
               String query1 = String.format("UPDATE Users SET favGames = '%s' WHERE login = '%s'", new_games, user_login);
               esql.executeUpdate(query1); break;
   
               case 2: System.out.print("\nNew Password: ");
               String new_password = in.readLine();
               String query2 = String.format("UPDATE Users SET password = '%s' WHERE login = '%s'", new_password, user_login);
               esql.executeUpdate(query2); break;
               
               case 3: System.out.print("\nNew Phone Number: ");
               String new_phoneNum = in.readLine();
               String query3 = String.format("UPDATE Users SET phoneNum = '%s' WHERE login = '%s'", new_phoneNum, user_login);
               esql.executeUpdate(query3); break;
            }
            System.out.print("\nProfile updated succesfully. Would you like to update again? \n1. Yes \n2. No\n");
            switch(readChoice()) {
               case 1: break;
               case 2: update = false; break;
            }
         }
      } catch(Exception e){
         System.err.println (e.getMessage ());
      }
   } // end updateProfile

    public static void viewCatalog(GameRental esql, String user_login) {
      try {
         boolean keepLooking = true; 
         while (keepLooking) {
            System.out.println("\nBROWSE CATALOG");
            System.out.println("------------");
            System.out.println("1. View all Games");
            System.out.println("2. Filter by Genre");
            System.out.println("3. Filter by Price");
            System.out.println("4. Sort by Price (low to high)");
            System.out.println("5. Sort by price (high to low)");
            System.out.println("6. Exit Catalog");

            switch (readChoice()){
               case 1: viewAllGames(esql); break;
               case 2: filterByGenre(esql); break; 
               case 3: filterByPrice(esql); break;
               case 4: sortByPrice(esql, true); break;
               case 5: sortByPrice(esql, false); break;
               case 6: keepLooking = false; break; 
               default: System.out.println("Invalid Input choice!!"); break; 
            }
         }
         
      } catch (Exception e) {
            System.err.println(e.getMessage()); 
         }
   } // end viewCatalog
 
    public static void placeOrder(GameRental esql, String user_login) {
      try {
         boolean getGame = true;
         double totalPrice = 0.0;
         int totalGames = 0;

         String orderID = generateUniqueRental(esql); // initialize unique orderID 
         Timestamp dueDate = generateRandomDueDate(); // initialize dueDate 
         Timestamp timeStamp = getCurrentTimestamp(); // initialize timeStamp 
         String trackingid = generateUniquetrackingID(esql); // initialize trackingID

         // query
         String add_order_id = String.format("INSERT INTO rentalorder(rentalorderid, login, noOfGames, totalprice, orderTimestamp, dueDate) VALUES('%s', '%s', 0, 0.0, '%s', '%s')", orderID, user_login, timeStamp, dueDate);
         esql.executeUpdate(add_order_id);

         while (getGame == true) { // iterate until user no longer wants to purchase a game
            System.out.print("\n--PLACE RENTAL ORDER--\n");
            System.out.print("Enter the game ID of the game you'd like to order: ");
            String user_game = in.readLine(); 

            System.out.print("How many units would you like?: ");
            int unitsToOrder = Integer.parseInt(in.readLine());
            totalGames = unitsToOrder + totalGames;

            String query = String.format("SELECT price FROM Catalog WHERE gameID = '%s'", user_game);
            List<List<String>> price = esql.executeQueryAndReturnResult(query);
            String stringPrice = price.get(0).get(0);
            double intPrice = Double.parseDouble(stringPrice);
            totalPrice = (unitsToOrder * intPrice) + totalPrice;
            System.out.print("\nCurrent Price: $" + totalPrice + "\n");

            String add_into_games = String.format("INSERT INTO gamesinorder(rentalorderid, gameID, unitsOrdered) VALUES('%s', '%s', '%s')", orderID, user_game, unitsToOrder);
            esql.executeUpdate(add_into_games);

            System.out.print ("\nWould you like to order more? (Y or N): ");
            String response = in.readLine();
            if (response.equalsIgnoreCase("N")) {
               getGame = false;
            }
         }

         // add into rental order
         String add_order_query = String.format("UPDATE rentalorder SET noOfGames = '%s', totalprice = '%s' WHERE rentalorderid = '%s'", totalGames, totalPrice, orderID);
         esql.executeUpdate(add_order_query);

         // add into tracking info
         String add_tracking_query = String.format("INSERT INTO trackinginfo(trackingID, rentalorderid, status, currentLocation, courierName, lastUpdateDate, additionalComments) VALUES ('%s', '%s', 'Processing', 'Los Angeles, CA', 'USPS', '%s', '')", trackingid, orderID, timeStamp);
         esql.executeUpdate(add_tracking_query);

         System.out.print("\nThe total cost of your order is: $" + totalPrice + "\n\nYour order has been placed.\nOrder ID: " + orderID + "\nTracking ID: " + trackingid + "\n");

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
    } // end placeOrder

    public static void viewAllOrders(GameRental esql, String user_login) {
      try {
         String query = "SELECT r1.rentalOrderID, c.gameName, r1.orderTimestamp, r1.dueDate " + 
                  "FROM RentalOrder r1 " + 
                  "JOIN GamesInOrder gm ON r1.rentalOrderID = gm.rentalOrderID " +
                  "JOIN Catalog c ON gm.gameID = c.gameID " + 
                  "WHERE r1.login = '" + user_login + "'" +
                  "ORDER BY r1.orderTimestamp"; 
         System.out.print("Your order history: ");
         esql.executeQueryAndPrintResult(query);

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   } // end viewAllOrders

    public static void viewRecentOrders(GameRental esql, String user_login) {
      try{ 
         String query = "SELECT r1.rentalOrderID, c.gameName, r1.orderTimestamp, r1.dueDate " + 
                  "FROM RentalOrder r1 " + 
                  "JOIN GamesInOrder gm ON r1.rentalOrderID = gm.rentalOrderID " +
                  "JOIN Catalog c ON gm.gameID = c.gameID " + 
                  "WHERE r1.login = '" + user_login + "'" +
                  "ORDER BY r1.orderTimestamp DESC " + 
                  "LIMIT 5 ";
         System.out.print("Your recent 5 orders: ");
         esql.executeQueryAndPrintResult(query);
         System.out.print("\n");

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   } // end viewRecentOrders

   public static void viewOrderInfo(GameRental esql, String user_login) {
      try {
          System.out.print("\nEnter your rental Order ID: ");
          String rentalOrderID = in.readLine(); 
          System.out.print("\n");
  
          // Check if the user is a manager or employee
          boolean isEmployeeOrManager = isEmployeeOrManager(esql, user_login); 
  
          // Query to get the login associated with the rental order ID
          String query = String.format("SELECT login FROM RentalOrder WHERE rentalOrderID = '%s'", rentalOrderID);
          List<List<String>> result = esql.executeQueryAndReturnResult(query); 
  
          if (result.isEmpty()) {
              System.out.println("Order ID not found. Returning to main menu.");
              return;
          }
  
          String orderLogin = result.get(0).get(0);
  
          // If user is not a manager or employee, check if the order belongs to them
          if (!isEmployeeOrManager && !orderLogin.equals(user_login)) {
              System.out.println("You do not have permission to view this order. Please input another order ID or quit to the main menu.");
              System.out.print("Enter '1' to input another order ID or '2' to quit: ");
              int choice = Integer.parseInt(in.readLine());
              if (choice == 1) {
                  viewOrderInfo(esql, user_login);
              } else {
                  return;
              }
          }
  
          // Query to get the order information
          query = "SELECT r1.rentalOrderID, r1.login, r1.noOfGames, r1.totalPrice, r1.orderTimestamp, r1.dueDate, " +
                  "ti.trackingID, ti.status, ti.currentLocation, ti.courierName, ti.lastUpdateDate, ti.additionalComments " +
                  "FROM RentalOrder r1 " +
                  "JOIN TrackingInfo ti ON r1.rentalOrderID = ti.rentalOrderID " +
                  "WHERE r1.rentalOrderID = '" + rentalOrderID + "'";
  
          System.out.print("\n");
          esql.executeQueryAndPrintResult(query);
  
      } catch (Exception e) {
          System.err.println(e.getMessage());
      }
  } // end viewOrderInfo

   public static void viewTrackingInfo(GameRental esql, String user_login) {  
      try {
          System.out.print("\nEnter your tracking ID: ");
          String trackingID = in.readLine(); 
          System.out.print("Enter your rental order ID: ");
          String rentalOrderID = in.readLine();
          System.out.print("\n");
  
          // Check if the user is a manager or employee
          boolean isEmployeeOrManager = isEmployeeOrManager(esql, user_login);
  
          // Query to get the login associated with the rental order ID
          String loginQuery = String.format("SELECT login FROM RentalOrder WHERE rentalOrderID = '%s'", rentalOrderID);
          List<List<String>> result = esql.executeQueryAndReturnResult(loginQuery);
          
          if (result.isEmpty()) {
              System.out.println("Order ID not found. Returning to main menu.");
              return;
          }
  
          String orderLogin = result.get(0).get(0);
  
          // If user is not a manager or employee, check if the order belongs to them
          if (!isEmployeeOrManager && !orderLogin.equals(user_login)) {
              System.out.println("You do not have permission to view this tracking information.");
              return;
          }
  
          // Query to get the tracking information
          String query = "SELECT ti.trackingID, ti.rentalOrderID, ti.courierName, ti.currentLocation, " +
                         "ti.status, ti.lastUpdateDate, ti.additionalComments " +
                         "FROM TrackingInfo ti " +
                         "JOIN RentalOrder ro ON ti.rentalOrderID = ro.rentalOrderID " +
                         "WHERE ti.trackingID = '" + trackingID + "' AND ro.rentalOrderID = '" + rentalOrderID + "'";
  
          System.out.print("\n");
          esql.executeQueryAndPrintResult(query);   
  
      } catch (Exception e) {
          System.err.println(e.getMessage());
      }
  } // end viewTrackingInfo
 
   public static void updateTrackingInfo(GameRental esql, String user_login) {
      try {

         if (!isEmployeeOrManager(esql, user_login)) {
            System.out.println("Access Denied: Only employees or managers can update the tracking information.");
            return;
         }

         System.out.print("\nEnter your tracking ID: ");
         String trackingID = in.readLine(); 

         System.out.print("Enter new status: ");
         String newStatus = in.readLine(); 

         System.out.print("Enter new current Location: ");
         String newLocation = in.readLine(); 

         System.out.print("Enter new courier name:");
         String newCourierName = in.readLine(); 

         System.out.print("Enter new additional comments: ");
         String newComments = in.readLine(); 

         Timestamp currentTimestamp = getCurrentTimestamp(); 

         String query = "UPDATE TrackingInfo " + 
           "SET status = '" + newStatus + "', " +
           "currentLocation = '" + newLocation + "', " +
          "courierName = '" + newCourierName + "', " +
          "additionalComments = '" + newComments + "', " +
           "lastUpdateDate = '" + currentTimestamp + "' " +
           "WHERE trackingID = '" + trackingID + "'";
              
                     esql.executeUpdate(query);
                  System.out.println("Tracking Information updated successfully.");
      }
      catch (Exception e) {
         System.err.println(e.getMessage());

      }
   } // end updateTrackingInfo

    public static void updateCatalog(GameRental esql, String user_login) {
      try{
         if (isManager(esql, user_login)) {
            boolean update = true;

            while(update == true) {
               System.out.print("\nPlease input the gameID of the game you would like to update: ");
               String game_update = in.readLine();

               String game_name = String.format("SELECT gameName FROM Catalog WHERE gameID = '%s'", game_update);
               String game_genre = String.format("SELECT genre FROM Catalog WHERE gameID = '%s'", game_update);
               String game_price = String.format("SELECT price FROM Catalog WHERE gameID = '%s'", game_update);
               String game_description = String.format("SELECT description FROM Catalog WHERE gameID = '%s'", game_update);
               String game_imageURL = String.format("SELECT imageURL FROM Catalog WHERE gameID = '%s'", game_update);
               
               System.out.print("\n");
               esql.executeQueryAndPrintResult(game_name);

               System.out.print("\n");
               esql.executeQueryAndPrintResult(game_genre);

               System.out.print("\n");
               esql.executeQueryAndPrintResult(game_price);

               System.out.print("\n");
               esql.executeQueryAndPrintResult(game_description);

               System.out.print("\n");
               esql.executeQueryAndPrintResult(game_imageURL);
            
               System.out.print("\nWhat would you like to update? \n1. Game Name \n2. Genre \n3. Price \n4. Description \n5. imageURL \n");

               switch(readChoice()) {
                  case 1: System.out.print("\nGame Name: ");
                  String gamename_update = in.readLine();
                  String query1 = String.format("UPDATE Catalog SET gameName = '%s' WHERE gameID = '%s'", gamename_update, game_update);
                  esql.executeUpdate(query1); break;

                  case 2: System.out.print("\nGenre: ");
                  String genre_update = in.readLine();
                  String query2 = String.format("UPDATE Catalog SET role = '%s' WHERE gameID = '%s'", genre_update, game_update);
                  esql.executeUpdate(query2); break;

                  case 3: System.out.print("\nPrice: ");
                  String price_update = in.readLine();
                  String query3 = String.format("UPDATE Catalog SET price = '%s' WHERE gameID = '%s'", price_update, game_update);
                  esql.executeUpdate(query3); break;

                  case 4: System.out.print("\nDescription: ");
                  String description_update = in.readLine();
                  String query4 = String.format("UPDATE Catalog SET description = '%s' WHERE gameID = '%s'", description_update, game_update);
                  esql.executeUpdate(query4); break;

                  case 5: System.out.print("\nImage URL: ");
                  String image_update = in.readLine();
                  String query5 = String.format("UPDATE Catalog SET imageURL = '%s' WHERE gameID = '%s'", image_update, game_update);
                  esql.executeUpdate(query5); break;
               }

               System.out.print("\nGame updated succesfully. Would you like to update again? \n1. Yes \n2. No\n");
               switch(readChoice()) {
                  case 1: break;
                  case 2: update = false; break;
               }
            }
         }
         else { // not manager user
            System.out.print("\nYou do not have permision to access this.\n");
            return;
         }
      } catch(Exception e){
         System.err.println (e.getMessage ());
      }
    } // end updateCatalog

    public static void updateUser(GameRental esql, String user_login) {
      try{ 
         if (isManager(esql, user_login)) { // choices for a manager
            boolean update = true;

            while(update == true) {
               System.out.print("\nEnter the user's login to update: ");
               String user_update = in.readLine();

               if (isUser(esql,user_update)) { // if valid user found
                  String userlogin = String.format("SELECT login FROM Users WHERE login = '%s'", user_update);
                  String user_password = String.format("SELECT password FROM Users WHERE login = '%s'", user_update);
                  String user_role = String.format("SELECT role FROM Users WHERE login = '%s'", user_update);
                  String user_games = String.format("SELECT favGames FROM Users WHERE login = '%s'", user_update);
                  String user_phone = String.format("SELECT phoneNum FROM Users WHERE login = '%s'", user_update);
                  String user_overdue = String.format("SELECT numOverDueGames FROM Users WHERE login = '%s'", user_update);
               
                  System.out.print("\n");
                  esql.executeQueryAndPrintResult(userlogin);
                  System.out.print("\n");
                  esql.executeQueryAndPrintResult(user_password);
                  System.out.print("\n");
                  esql.executeQueryAndPrintResult(user_role);
                  System.out.print("\n");
                  esql.executeQueryAndPrintResult(user_games);
                  System.out.print("\n");
                  esql.executeQueryAndPrintResult(user_phone);
                  System.out.print("\n");
                  esql.executeQueryAndPrintResult(user_overdue);

                  System.out.print("\nWhat would you like to update? \n1. User's Role \n2. User's Number of Overdue Games \n");

                  switch(readChoice()) {
                     case 1: System.out.print("\nUser's New Role: ");
                     String role_update = in.readLine();
                     String query1 = String.format("UPDATE Users SET role = '%s' WHERE login = '%s'", role_update, user_update);
                     esql.executeUpdate(query1); break;

                     case 2: System.out.print("\nUser's New Number of Overdue Games: ");
                     String overdue_update = in.readLine();
                     String query2 = String.format("UPDATE Users SET numOverDueGames = '%s' WHERE login = '%s'", overdue_update, user_update);
                     esql.executeUpdate(query2); break;
                  }

                  System.out.print("\nProfile updated succesfully. Would you like to update again? \n1. Yes \n2. No\n");
                  switch(readChoice()) {
                     case 1: break;
                     case 2: update = false; break;
                  }
               }
               else {
                  System.out.print("\nUser not found. Please try again.\n");
               }
            }
         }
         else { // not manager!
            System.out.print("\nYou do not have permission to access this.\n");
            return;
         } 
      } catch(Exception e){
         System.err.println (e.getMessage ());
      }
   } // end updateUser



    // helper functions

   // check if user is manager
    public static boolean isManager(GameRental esql, String user_login) {
      try{
         String query = String.format("SELECT * FROM Users WHERE login = '%s' AND role = 'manager'", user_login);
         int user_isManager = esql.executeQuery(query);
         if (user_isManager > 0) {
            return true;
         }
         else {
            return false;
         }

      } catch(Exception e){
         System.err.println (e.getMessage ());
         return false;
      }
   } // end isManager

   public static boolean isUser(GameRental esql, String user_login) {
      try{
         String query = String.format("SELECT * FROM Users WHERE login = '%s'", user_login);
         int is_user = esql.executeQuery(query);
         if (is_user > 0) {
            return true;
         }
         else {
            return false;
         }
      } catch(Exception e){
         System.err.println (e.getMessage ());
         return false;
      }
   } // end isUser

   public static boolean isEmployee(GameRental esql, String user_login) {
      try {
         String query = String.format("SELECT * FROM Users WHERE login = '%s' AND role = 'employee'", user_login);
         int user_isEmployee = esql.executeQuery(query);
         return user_isEmployee > 0;
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return false;
      }
   } // end check if the role is employee or not 

   public static boolean isEmployeeOrManager(GameRental esql, String user_login) {
      return isManager(esql, user_login) || isEmployee(esql, user_login);
   }
   // a helpler combining both isEmployee or manager. 

   // filter by maximum price
   private static void filterByPrice (GameRental esql) {
      try {
         System.out.print("Enter Maximum Price: ");
         String price = in.readLine(); 
         String query = String.format ("SELECT gameID, gameName,genre, price, description, imageURL FROM CATALOG WHERE price <= %s", price);
         esql.executeQueryAndPrintResult(query);

      }
      catch (Exception e){
         System.err.println (e.getMessage()); 
      }
   } // end filterByPrice

   // filter games either by ascending or descending 
   private static void sortByPrice(GameRental esql, boolean ascending) {
      try {
            String query = "SELECT gameID, gameName, genre, price, description, imageURL FROM Catalog ORDER BY price";
            if (!ascending) {
                 query += " DESC";
             }
             esql.executeQueryAndPrintResult(query);
         } catch (Exception e) {
             System.err.println(e.getMessage());
         }
   } // end sortByPrice 

   // displays all games in catalog 
   private static void viewAllGames (GameRental esql){
      try{
         String query = "SELECT gameID, gameName, genre, price, description, imageURL FROM Catalog";
         esql.executeQueryAndPrintResult(query);
      }
      catch (Exception e) {
         System.err.println (e.getMessage ());
      }
   } // end viewAllGames

   // allows filtering by genre
   private static void filterByGenre (GameRental esql) {
      try {
         System.out.print("Enter the genre you are looking: ");
         String genre = in.readLine();
         String query = String.format("SELECT gameID, gameName, genre, description, imageURL FROM Catalog WHERE genre = '%s'", genre);
         esql.executeQueryAndPrintResult(query);
         
      }
      catch (Exception e) {
         System.err.println (e.getMessage());
      }
   } // end filterbygenre

   // timestamp fxn
   private static Timestamp getCurrentTimestamp() {
      LocalDateTime now = LocalDateTime.now();
      return Timestamp.valueOf(now);
  } // end timestamp

   private static String generateUniqueRental (GameRental esql) {
      Random random = new Random(); 
      String rentalOrderID;  
      boolean isUnique = false;
   
      do {
         int randomDigits = 1000 + random.nextInt(9000); //used stack overflow
         rentalOrderID = "gamerentalorder" + randomDigits; 
   
         String query = String.format("SELECT COUNT(*) FROM RentalOrder WHERE rentalOrderID = '%s'", rentalOrderID);
    
         try {
            List<List<String>> result = esql.executeQueryAndReturnResult(query);
            isUnique = result.get(0).get(0).equals("0"); //stack overflow
         
         }
         catch (Exception e) {
            System.err.println(e.getMessage()); 
      
         }
    
      }
      while (!isUnique) ; 
      return rentalOrderID;   
   } //generate uniqueRental

   //generate unique tracking id 
   private static String generateUniquetrackingID (GameRental esql) {
      Random random = new Random(); 
      String trackingID; 
      boolean isUnique = false; 
      do { 
         int randomDigits = 1000 + random.nextInt(9000); 
         trackingID = "trackingid" + randomDigits; 

         String query = String.format("SELECT COUNT(*) FROM TrackingInfo WHERE trackingID = '%s'", trackingID); 

         try { 
                  List<List<String>> result = esql.executeQueryAndReturnResult(query); 
                  isUnique = result.get(0).get(0).equals("0"); 
         }
         catch (Exception e) {
            System.err.println(e.getMessage()); 

         }
      }
      while (!isUnique); 
      return trackingID;  
   } // end generateUniquetrackingID

   // generate random due date
   private static Timestamp generateRandomDueDate() {
      
      Random random = new Random(); 
         
      int randomDays = 5 + random.nextInt(22); // generate anything between 5 and 26 
      LocalDateTime now = LocalDateTime.now(); 
      LocalDateTime dueDate = now.plusDays(randomDays); 

      return Timestamp.valueOf(dueDate); 
   } // end generateRamndomDueDate

}//end GameRental