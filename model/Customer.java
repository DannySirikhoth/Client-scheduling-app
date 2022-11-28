/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import helper.JDBCDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Customer {
    private int divisionId;
    private String id;
    private String name;
    private String address;
    private String zip;
    private String phone;

    private String divisionName;


    public Customer() {

    }

    /**
     * constructor
     *
     * @param id
     * @param name
     * @param address
     * @param zip
     * @param phone
     * @param divisionId
     * @param divisionName
     */
    public Customer(String id, String name, String address, String zip, String phone, int divisionId, String divisionName) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.zip = zip;
        this.phone = phone;
        this.divisionId = divisionId;
        this.divisionName = divisionName;

    }

    /**
     * returns the customer ID
     *
     * @return customer id
     */
    public String getId() {

        return id;
    }

    /**
     * returns the customer name
     *
     * @return customer name
     */
    public String getName() {

        return name;
    }

    /**
     * returns the address
     *
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * returns the zip code
     *
     * @return postal code
     */
    public String getZip() {
        return zip;
    }

    /**
     * returns the phone number
     *
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Adds a new customer to the database
     *
     * @param name
     * @param address
     * @param zipcode
     * @param phone
     * @param division_id
     * @return
     * @throws SQLException
     */
    public static int addNewCustomer(String name, String address, Integer zipcode, String phone, int division_id) throws SQLException {


        String sql = "INSERT INTO customers ( Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = JDBCDAO.connection.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, address);
        ps.setInt(3, zipcode);
        ps.setString(4, phone);
        ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(6, "admin");
        ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(8, "admin");
        ps.setInt(9, division_id);

        int rowsAffected = ps.executeUpdate();
        return rowsAffected;

    }

    /**
     * Updates a customer record in the database
     *
     * @param customerID
     * @param name
     * @param address
     * @param zipcode
     * @param phone
     * @param DivisionID
     * @return
     * @throws SQLException
     */
    public static int updateCustomer(int customerID, String name, String address, String zipcode, String phone, int DivisionID) throws SQLException {

        String sql = " Update Customers Set Customer_name =?, Address =?, Postal_Code =?, Phone =?, Division_ID =? Where Customer_id = ?";
        PreparedStatement ps = JDBCDAO.connection.prepareStatement(sql);

        ps.setString(1, name);
        ps.setString(2, address);
        ps.setString(3, zipcode);
        ps.setString(4, phone);
        ps.setInt(5, DivisionID);
        ps.setInt(6, customerID);

        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /**
     * Deletes a customer from the databasea
     *
     * @param customer_id
     * @return
     * @throws SQLException
     */
    public static int deleteCustomer(int customer_id) throws SQLException {

        String sql2 = "DELETE FROM Appointments WHERE Customer_ID = ?";
        PreparedStatement ps2 = JDBCDAO.connection.prepareStatement(sql2);
        ps2.setInt(1, customer_id);
        int appDeleted = ps2.executeUpdate();

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alert Dialog");
        alert.setContentText(appDeleted + " Appointments deleted where customer Id is " + customer_id);
        alert.showAndWait();

        String sql = "DELETE FROM Customers WHERE Customer_ID = ?";
        PreparedStatement ps = JDBCDAO.connection.prepareStatement(sql);
        ps.setInt(1, customer_id);
        int rowsAffected = ps.executeUpdate();


        return rowsAffected;
    }

    /**
     * makes an observable list that takes the customer data
     *
     * @return
     * @throws SQLException
     */
    public static ObservableList<Customer> getAllCustomers() throws SQLException {

        String sql = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, " +
                "customers.Division_ID, first_level_divisions.Division from " +
                "customers INNER JOIN  first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID";

        PreparedStatement ps = JDBCDAO.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        ObservableList<Customer> customersObservableList = FXCollections.observableArrayList();

        while (rs.next()) {
            String customerID = rs.getString("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            String address = rs.getString("Address");
            String zip = rs.getString("Postal_Code");
            String phone = rs.getString("Phone");
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            Customer customer = new Customer(customerID, customerName, address, zip, phone, divisionID, divisionName);
            customersObservableList.add(customer);

            //String id, String name, String address, String zip, String phone, String divisionName
        }
        return customersObservableList;
    }

    public static ObservableList<Customer> getAllCustomersWithName(String name) throws SQLException{

        String sql = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, " +
                "customers.Division_ID, first_level_divisions.Division from " +
                "customers INNER JOIN  first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID Where customers.Customer_Name = ?";

        PreparedStatement ps = JDBCDAO.connection.prepareStatement(sql);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();

        ObservableList<Customer> customersObservableList = FXCollections.observableArrayList();

        while (rs.next()) {
            String customerID = rs.getString("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            String address = rs.getString("Address");
            String zip = rs.getString("Postal_Code");
            String phone = rs.getString("Phone");
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            Customer customer = new Customer(customerID, customerName, address, zip, phone, divisionID, divisionName);
            customersObservableList.add(customer);

            //String id, String name, String address, String zip, String phone, String divisionName
        }
        return customersObservableList;
    }

    /**
     * returns the first level division
     *
     * @return division name
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * returns the division name
     *
     * @param divisionName
     */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * returns the division id
     *
     * @return division Id
     */
    public int getDivisionId() {
        return divisionId;
    }

}