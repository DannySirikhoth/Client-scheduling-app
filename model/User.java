package model;

import helper.JDBCDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User extends AdminUser{

    private int userId;
    private String userName;
    private String password;

    /**
     * Constructor
     * Inheritance
     *
     * @param userId
     * @param userName
     * @param password
     */
    public User(int userId, String userName, String password) {
        super(userId, userName, password);
    }

    /**
     * creates a list of users
     * @return
     * @throws SQLException
     */
    public static ObservableList<User> getAllUsers() throws SQLException {
        ObservableList<User> usersObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * from users";
        PreparedStatement ps = JDBCDAO.getConn().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int userID = rs.getInt("User_ID");
            String userName = rs.getString("User_Name");
            String userPassword = rs.getString("Password");
            User user = new User(userID, userName, userPassword);
            usersObservableList.add(user);
            System.out.println(" the user id is " + userID );
        }

        return usersObservableList;
    }

    public static ObservableList<Integer> getAllUserIds() throws SQLException {

        ObservableList<Integer> usersIdList = FXCollections.observableArrayList();
        String sql = "SELECT user_id from users";
        PreparedStatement ps = JDBCDAO.getConn().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int userID = rs.getInt("User_ID");
            usersIdList.add(userID);
            System.out.println(" the user id is " + userID );
        }

        return usersIdList;

    }


    /**
     * verifies login credentials
     * Polymorphism this method validates if the user username and password are in the database.
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
            ResultSet rs = statement.executeQuery("SELECT User_name FROM users;");
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
                        ResultSet rs2 = statement2.executeQuery("SELECT Password FROM users;");
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

    /**
     *returns the username
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     *returns the password
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * returns the userId
     * @return
     */
    public int getUserId() {
        return userId;
    }



}
