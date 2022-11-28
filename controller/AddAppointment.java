package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.TimeZone;

import static java.time.LocalTime.MAX;
import static java.time.LocalTime.MIN;

public class AddAppointment implements Initializable {

    public Button saveButton;
    public Button CancelButton;
    public TextField TitleText;
    public TextField DescriptionText;
    public TextField LocationText;
    public TextField TypeText;
    public DatePicker StartDatePicker;
    public ComboBox<Integer> UserIdComboBox;
    public ComboBox<String> CustomerIdComboBox;
    public ComboBox<String> ContactComboBox;
    public ComboBox<String> startTimeComboBox;
    public ComboBox <String>endTimeComboBox;

    LocalDateTime endTime = null;
    LocalDateTime startTime = null;
    int ContactID = 0;

    String errorMessage = "";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * When the save button is clicked the software will attempt to add an appointment
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void saveButtonClick(ActionEvent actionEvent) throws SQLException, IOException {



        // Makes sure all fields have data entered
        if (TitleText.getText().isEmpty() || DescriptionText.getText().isEmpty() || LocationText.getText().isEmpty() || ContactComboBox == null || TypeText.getText().isEmpty() ||
                CustomerIdComboBox.getValue() == null || UserIdComboBox.getValue() == null || StartDatePicker.getValue() == null || endTimeComboBox.getValue() == null ||
                startTimeComboBox.getValue() == null)
        {

            errorMessage += "Enter a value for all fields\n";
            // Throw error
            ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            Alert invalidInput = new Alert(Alert.AlertType.WARNING, errorMessage, clickOkay);
            invalidInput.showAndWait();
            return;

        }

        String title = TitleText.getText();
        String description = DescriptionText.getText();
        String location = LocationText.getText();
        String type = TypeText.getText();
        LocalDate appDate = StartDatePicker.getValue();

        startTime = LocalDateTime.of(StartDatePicker.getValue(), LocalTime.parse(startTimeComboBox.getValue(),formatter));
        endTime = LocalDateTime.of(StartDatePicker.getValue(), LocalTime.parse(endTimeComboBox.getValue(),formatter));
        int customerID = Integer.parseInt(CustomerIdComboBox.getValue());
        Integer userID = UserIdComboBox.getValue();
        String contactName = ContactComboBox.getValue();
        int appid = 0;




        //Validates if the end is after the start
       //diff will be 0 if the values are the same, positive if endTime is sooner than startTime, negative if entime is later than starttime
        //The value should be -1
       int diff = startTime.compareTo(endTime);
        //System.out.println("diff value is " + diff);
        if(diff == 1|| diff ==0)
        {Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("The end time must be after the start time!!");
            alert.showAndWait();
            return;
        }
        else
        {

        }


      //checks for overlaps, if theres no overlap then it will call the add method
      if(!Appointment.checkOverLap(customerID,appid,startTime,endTime))
      {
          ContactID=Contact.getContactIDfromName(contactName);
          Appointment.addNewAppointment(title, description, location, type, startTime, endTime, customerID, userID, ContactID);

          //Returns us back to the Appointment Screen
          Parent root = FXMLLoader.load(getClass().getResource("/view/AppointmentScreen.fxml"));
          Stage stage = (Stage) CancelButton.getScene().getWindow();
          Scene scene = new Scene(root);
          stage.setScene(scene);
          stage.show();
      }

    }
    /**
     * Returns the user to the appointment screen
     * @param actionEvent
     * @throws IOException
     */
    public void CancelButtonClicked(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/view/AppointmentScreen.fxml"));
        Stage stage = (Stage) CancelButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    /**
     *  Lambda #1,2, The justification for these Lambdas is they allow me to write code blocks to fill the observable lists without implementing a method.Lambda #3 allows me to fill a combobox with less code.Lambdas allow the programmer to reference a code block that can be passed around offering code-use flexibility
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println(" the Locale is " + TimeZone.getDefault());

        //Puts a list of CustomerIds into the CustomerId ComboBox
        ObservableList<Customer> CustomerIdObservableList = null;
        try {
            CustomerIdObservableList = Customer.getAllCustomers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ObservableList<String>allCustomerIds = FXCollections.observableArrayList();
        //Lambda #1
        /**
         * @Lambda #1
          */
        CustomerIdObservableList.forEach(customers -> allCustomerIds.add(customers.getId()) );
        CustomerIdComboBox.setItems(allCustomerIds);


        ObservableList<Integer>allUserIds = FXCollections.observableArrayList();

        try {
            allUserIds = User.getAllUserIds();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        UserIdComboBox.setItems(allUserIds);


        //Puts a list of contacts into the contact ComboBox
        ObservableList<Contact> contactsObservableList = null;
        try {
            contactsObservableList = Contact.getAllContacts();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<String> allContactsNames = FXCollections.observableArrayList();
        //lambda #3
        /**
         * @Lambda #3
         */
        contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
        ContactComboBox.setItems(allContactsNames);


        //This sets the time available for meetings
        ObservableList<String> appointmentTimes = FXCollections.observableArrayList();
        String offSet = OffsetTime.now().toString().substring(16,21);
        LocalTime firstAppointment = MIN.plusHours(8);
        LocalTime lastAppointment = MAX.minusHours(1).minusMinutes(45);

        if(offSet.equals("07:00"))
        {
            firstAppointment = MIN.plusHours(5);
            lastAppointment = MAX.minusHours(4).minusMinutes(45);
        } else if (offSet.equals("06:00")) {
            firstAppointment = MIN.plusHours(6);
            lastAppointment = MAX.minusHours(3).minusMinutes(45);
        }
        else if (offSet.equals("05:00")) {
            firstAppointment = MIN.plusHours(7);
            lastAppointment = MAX.minusHours(2).minusMinutes(45);
        }
        else if (offSet.equals("08:00")) {
            firstAppointment = MIN.plusHours(4);
            lastAppointment = MAX.minusHours(5).minusMinutes(45);
        }
        else if(offSet.equals("04:00"))
        {
            firstAppointment = MIN.plusHours(8);
            lastAppointment = MAX.minusHours(2).minusMinutes(45);
        }
        else {
            firstAppointment = MIN.plusHours(8);
            lastAppointment = MAX.minusHours(2).minusMinutes(45);

        }
        if (!firstAppointment.equals(0) || !lastAppointment.equals(0)) {
            while (firstAppointment.isBefore(lastAppointment)) {
                appointmentTimes.add(String.valueOf(firstAppointment));
                firstAppointment = firstAppointment.plusMinutes(15);
            }
        }
        startTimeComboBox.setItems(appointmentTimes);
        endTimeComboBox.setItems(appointmentTimes);
    }
}
