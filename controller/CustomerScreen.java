/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import helper.JDBCDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Country;
import model.Customer;
import model.firstLevelDivision;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static model.Appointment.getAllAppointments;
import static model.Customer.getAllCustomers;
import static model.Customer.getAllCustomersWithName;


public class CustomerScreen implements Initializable {

    public Button AddButton;
    public Button UpdateButton;
    public Button DeleteButton;
    public Button viewAppointmentsButton;
    @FXML
    public TableColumn<Customer, String> customerIdColumn;
    @FXML
    public TableColumn<Customer, String> customerNameColumn;

    public TableView <Customer> custTable;
    public TextField NameText;
    public TextField AddressText;
    public TextField PostalText;
    public TextField PhoneText;
    public TextField DivisionIdText;
    public ComboBox customerStateComboBox;
    public ComboBox customerCountryComboBox;
    public TableColumn customerAddressColumn;
    public TableColumn customerPostalCodeColumn;
    public TableColumn customerPhoneColumn;
    public Button Exitbutton;
    public Button FormButton;
    public TextField SearchText;
    public Button SearchButton;


    /**
     * Adds the customer object info to the form
     * @param selectedCustomer
     */
    public void showCustomerDetails(Customer selectedCustomer) {

        if(custTable.getSelectionModel().getSelectedItem() != null) {
            NameText.setText(selectedCustomer.getName());
            AddressText.setText(selectedCustomer.getAddress());
            PostalText.setText(selectedCustomer.getZip());
            PhoneText.setText(selectedCustomer.getPhone());
            DivisionIdText.setText(selectedCustomer.getDivisionName());
        }
    }


    /**
     * Initialize the customer screen
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param rb
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    public void initialize(URL url, ResourceBundle rb) {

        ObservableList<Country> allCountries = null;
        try {
            allCountries = Country.getCountries();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<String> countryNames = FXCollections.observableArrayList();
        allCountries.stream().map(Country::getCountryName).forEach(countryNames::add);
        customerCountryComboBox.setItems(countryNames);

        ObservableList<firstLevelDivision> allFirstLevelDivisions = null;
        try {
            allFirstLevelDivisions = firstLevelDivision.getFirstLevelDivisions();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<String> firstLevelDivisionAllNames = FXCollections.observableArrayList();
        allFirstLevelDivisions.stream().map(firstLevelDivision::getDivisionName).forEach(firstLevelDivisionAllNames::add);
        customerStateComboBox.setItems(firstLevelDivisionAllNames);

        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("Address"));
        customerPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Zip"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("Phone"));

        try {
            custTable.getItems().setAll(getAllCustomers());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        custTable.getSelectionModel().selectedItemProperty().addListener
                ((observable, oldValue, newValue)-> showCustomerDetails( newValue));

    }

    /**
     * Opens the Add customer Screen
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void AddCustomerButtonClicked(ActionEvent actionEvent) throws IOException, SQLException {

        Parent root = FXMLLoader.load(getClass().getResource("/view/AddCustomer.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Updates the Customer Database
      * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void updateButtonClicked(ActionEvent actionEvent) throws IOException, SQLException {


        //makes sure a selection is picked
        if (custTable.getSelectionModel().getSelectedItem() == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("ERROR: Choose a Customer to update");
            alert.showAndWait();
        } else
        {

            if (customerCountryComboBox.getValue() == null || customerStateComboBox.getValue() == null)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setContentText("ERROR: Choose Country and first level division");
                alert.showAndWait();
            }
            else {
                //This takes the selected Id to make as a parameter to send to update query
                Customer selectedCustomer = custTable.getSelectionModel().getSelectedItem();
                int selectedId = Integer.parseInt(selectedCustomer.getId());
                System.out.println("the selected ID is " + selectedId);

                //this takes the first level data
                int firstLevelDivisionName = 0;
                for (model.firstLevelDivision firstLevelDivision : firstLevelDivision.getFirstLevelDivisions()) {
                    if (customerStateComboBox.getSelectionModel().getSelectedItem().equals(firstLevelDivision.getDivisionName())) {
                        firstLevelDivisionName = firstLevelDivision.getDivisionID();
                    }
                }

                //--------Takes the input values in the form
                String name = NameText.getText();
                String address = AddressText.getText();
                String postcode = PostalText.getText();
                String phone = PhoneText.getText();
                //int divId = Integer.parseInt(DivisionIdText.getText());

                if (Customer.updateCustomer(selectedId, name, address, postcode, phone, firstLevelDivisionName) == 1) {
                    //lets the user know the query was successful and puts a refreshed list out
                    System.out.println("Update Customer query executed");
                    //Refresh the TableView
                    try {
                        custTable.getItems().setAll(getAllCustomers());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }
            }

        }
    }

    /**
     * Deletes a customer
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void deleteButtonClicked(ActionEvent actionEvent) throws SQLException, IOException {

            {
            //makes sure a selection is picked
            Customer selectedCustomer = custTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer == null)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setContentText("ERROR: Choose a Customer to delete");
                alert.showAndWait();

            } else
            {
                    int customer_Id = Integer.parseInt(selectedCustomer.getId());
                    System.out.println(" customer ID to be deleted is " + customer_Id);
                    Customer.deleteCustomer(customer_Id);
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning Dialog");
                    alert.setContentText("Customer was successfully Removed from database");
                    alert.showAndWait();

                    //Refresh the tableview
                    try
                    {
                        custTable.getItems().setAll(getAllCustomers());
                    } catch (SQLException e)
                    {
                        throw new RuntimeException(e);
                    }

            }
        }
    }


    /**
     * Changes scenes to the appointments
     * @param actionEvent
     * @throws IOException
     */
    public void viewAppointmentButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/AppointmentScreen.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    /**
     * THis method set info and filters the info in the country combo boxes
      * @param actionEvent
     */
    public void customerEditCountryDropDown(ActionEvent actionEvent) {

        try {
            JDBCDAO.getConn();

            String selectedCountry = (String) customerCountryComboBox.getSelectionModel().getSelectedItem();

            ObservableList<firstLevelDivision> getAllFirstLevelDivisions = firstLevelDivision.getFirstLevelDivisions();

            ObservableList<String> flDivisionUS = FXCollections.observableArrayList();
            ObservableList<String> flDivisionUK = FXCollections.observableArrayList();
            ObservableList<String> flDivisionCanada = FXCollections.observableArrayList();

            getAllFirstLevelDivisions.forEach(firstLevelDivision -> {
                if (firstLevelDivision.getCountry_ID() == 1) {
                    flDivisionUS.add(firstLevelDivision.getDivisionName());
                } else if (firstLevelDivision.getCountry_ID() == 2) {
                    flDivisionUK.add(firstLevelDivision.getDivisionName());
                } else if (firstLevelDivision.getCountry_ID() == 3) {
                    flDivisionCanada.add(firstLevelDivision.getDivisionName());
                }
            });

            //needs a little revision
            if (selectedCountry.equals("U.S")) {
                customerStateComboBox.setItems(flDivisionUS);
            }
            else if (selectedCountry.equals("UK")) {
                customerStateComboBox.setItems(flDivisionUK);
            }
            else if (selectedCountry.equals("Canada")) {
                customerStateComboBox.setItems(flDivisionCanada);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Closes the Program
     * @param actionEvent
     */
    public void ExitButtonClick(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * takes the user to the forms page
     * @param actionEvent
     */
    public void formButtonClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/ReportScreen.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void SearchButtonClicked(ActionEvent actionEvent) {

        String name = SearchText.getText();
        ObservableList<Customer> allCustomersWithName;
        try {
            allCustomersWithName = getAllCustomersWithName(name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        custTable.getItems().clear();
        custTable.setItems(allCustomersWithName);
        custTable.refresh();


    }
}
