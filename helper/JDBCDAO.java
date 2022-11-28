package helper;

import java.sql.*;

public class JDBCDAO {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = UTC"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
     private static final String password = "Passw0rd!"; // Password
     public static Connection connection;  // Connection Interface


    /**
     * Starts a connection
     */
    public static void  openConnection()
    {

        try {
            Class.forName(driver); // Locate Driver
            System.out.println("before connection attempt");
            connection = DriverManager.getConnection(jdbcUrl, userName, password ); // Reference Connection object
            System.out.println("Connection successful!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }

    }

    /**
     * closes a connection
     * @throws SQLException
     */
    public static void closeConnection() throws SQLException {
        try
        {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }

    }

    /**
     * Connection object for other classes to use
     * @return
     */
    public static Connection getConn(){

        return connection;
    }
}
