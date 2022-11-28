package controller;

import helper.JDBCDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Country;
import model.Customer;
import model.firstLevelDivision;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class AddCustomer implements Initializable {
    private static int id = 100;
    public Label addCustomerIDlabel;
    public TextField addCustomerNametxt;
    public TextField addCustomerAddresstxt;
    public TextField addCustomerZipcodext;
    public TextField addcustomerPhone;
    public ComboBox addCustomerCountry;
    public ComboBox addCustomerStateComboBox;
    Stage stage;
    Parent scene;






    @FXML

    /**
     * This tries to save a new customer if all requirements are met
     * @param Action event
     * @throws IOException,NumberFormatException
     */
    public void onActionSave(ActionEvent event) throws IOException,NumberFormatException {

        if(addCustomerNametxt.getText() == null||addCustomerAddresstxt.getText() == null|| addCustomerZipcodext.getText() == null|| addcustomerPhone.getText()==null ||
                addCustomerStateComboBox.getValue() == null || addCustomerStateComboBox.getValue() == null)

        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Please input valid values for all fields");
            alert.showAndWait();
            return;
        }
        else
        {

        try
        {
            int firstLevelDivisionName = 0;
            for (model.firstLevelDivision firstLevelDivision : firstLevelDivision.getFirstLevelDivisions())
            {
                if (addCustomerStateComboBox.getSelectionModel().getSelectedItem().equals(firstLevelDivision.getDivisionName()))
                {
                    firstLevelDivisionName = firstLevelDivision.getDivisionID();
                }
            }
            String name = addCustomerNametxt.getText();
            String address = addCustomerAddresstxt.getText();
            int zip = Integer.parseInt(addCustomerZipcodext.getText());
            String phone = addcustomerPhone.getText();


            Customer.addNewCustomer(name,address,zip,phone,firstLevelDivisionName);
            int rowsAffected = 1; //Customer.addNewCustomer(name,address,zip);
            if(rowsAffected > 0)
            {
                System.out.println("INSERT successful");
            }
            else {
                System.out.println("INSERT FAILED");
            }

                //Returns to the Home Screen
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("/view/CustomerScreen.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();

        } catch(NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText(String.valueOf(e));
            alert.showAndWait();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    }

    /**Returns to the main screen when the user clicks cancel
     *
    //@param event, when the cancel button is clicked
    //@throws IOException
     */
    @FXML
    public void onActionCancel(ActionEvent event) throws IOException
    {
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/CustomerScreen.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();

    }


    /**
     * Initialize the add customer screen
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        ObservableList<Country> allCountries = null;
        try {
            allCountries = Country.getCountries();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<String> countryNames = FXCollections.observableArrayList();
        allCountries.stream().map(Country::getCountryName).forEach(countryNames::add);
        addCustomerCountry.setItems(countryNames);



        ObservableList<firstLevelDivision> allFirstLevelDivisions = null;
        try {
            allFirstLevelDivisions = firstLevelDivision.getFirstLevelDivisions();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<String> firstLevelDivisionAllNames = FXCollections.observableArrayList();
        allFirstLevelDivisions.stream().map(firstLevelDivision::getDivisionName).forEach(firstLevelDivisionAllNames::add);
        addCustomerStateComboBox.setItems(firstLevelDivisionAllNames);
    }

    /**
     * This loads values into the customer Country ComboBOx
     * @param actionEvent
     */
    public void customerCountryComboBox(ActionEvent actionEvent) {

        try {
            JDBCDAO.getConn();

            String selectedCountry = (String) addCustomerCountry.getSelectionModel().getSelectedItem();

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


            if (selectedCountry.equals("U.S")) {
                addCustomerStateComboBox.setItems(flDivisionUS);
            }
            else if (selectedCountry.equals("UK")) {
                addCustomerStateComboBox.setItems(flDivisionUK);
            }
            else if (selectedCountry.equals("Canada")) {
                addCustomerStateComboBox.setItems(flDivisionCanada);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}