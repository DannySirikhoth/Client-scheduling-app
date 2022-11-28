package model;

import helper.JDBCDAO;

import java.sql.ResultSet;
import java.sql.Statement;

abstract class AdminUser {

    private int userId;
    private String userName;
    private String password;

    /**
     * Constructor
     * @param userId
     * @param userName
     * @param password
     */
    public AdminUser(int userId, String userName, String password) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
    }

    /**
     * Polymorphism this method validates if the adminuser username and password are in the database.
      * @param user
     * @param pass
     * @return
     */
    public static Boolean checkCredentials(String user,String pass){

        //verify this method is called
        System.out.println("check credentials called");

        //runs the query
        try {
            Statement statement = JDBCDAO.getConn().createStatement();
            ResultSet rs = statement.executeQuery("SELECT User_name FROM adminusers;");
            System.out.println("Check Credentials query ran");

            //goes through all values
            while(rs.next())
            {
                if(rs.getString("User_name").equals(user) )
                {
                    System.out.println("User found");

                    try
                    {
                        Statement statement2 = JDBCDAO.getConn().createStatement();
                        ResultSet rs2 = statement2.executeQuery("SELECT Password FROM adminusers;");
                        //System.out.println("users was found so password query ran");

                        while(rs2.next())
                        {
                            if(rs2.getString("Password").equals(pass) )
                            {
                                System.out.println("password and user match returning true");
                                return true;
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        System.out.println("Error:" + e.getMessage());
                    }
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }

        return false;
    }
}
