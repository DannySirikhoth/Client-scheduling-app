package model;

import helper.JDBCDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Contact {
    public int contactID;
    public String contactName;
    public String contactEmail;

    /**
     * constructor
     * @param contactID
     * @param contactName
     * @param contactEmail
     */
    public Contact(int contactID, String contactName, String contactEmail) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    /**
     * Makes an observable list of contacts
     * @return
     * @throws SQLException
     */
    public static ObservableList<Contact> getAllContacts() throws SQLException {
        ObservableList<Contact> contactsObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * from contacts";
        PreparedStatement ps = JDBCDAO.getConn().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int contactID = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");
            String contactEmail = rs.getString("Email");
            Contact contact = new Contact(contactID, contactName, contactEmail);
            contactsObservableList.add(contact);
        }
        return contactsObservableList;
    }

    /**
     * This method retrieves the contact Id so it can be used as a paremeter for the add appointment method
     * @param
     * @throws SQLException
     */
    public static int getContactIDfromName(String contactName) throws SQLException {


        //
        int contact_id =0 ;
        String sql = "SELECT Contact_ID from contacts where Contact_name =?";
        PreparedStatement ps = JDBCDAO.getConn().prepareStatement(sql);
        ps.setString(1,contactName);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {

            contact_id = rs.getInt("Contact_ID");
        }

        System.out.println("contact ID retrieved from add appointment is " + contact_id);


        return  contact_id;
    }

    /**
     *gets the contact id
     * @return contactID
     */
    public int getId() {

        return contactID;
    }

    /**
     *gets the contact name
     * @return contactName
     */
    public String getContactName() {

        return contactName;
    }


}