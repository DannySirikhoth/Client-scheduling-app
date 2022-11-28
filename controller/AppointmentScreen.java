package controller;

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
import model.Contact;
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.LocalTime.*;
import static model.Appointment.getAllAppointments;


public class AppointmentScreen implements Initializable{

    @FXML
    public TableView AppointmentTable;
    @FXML
    public TableColumn<?,?> AppId;
    @FXML
    public TableColumn <Appointment, String> TitleColumn;
    public TableColumn <Appointment,String> descriptionColumn;
    @FXML
    public TableColumn <Appointment, String> locationColumn;
    @FXML
    public TableColumn  <Appointment,String>StartColumn;
    @FXML
    public TableColumn <Appointment, String> EndColumn;
    public TableColumn <Appointment, String> CustomerIDColumn;
    public TableColumn <Appointment, Integer> USERIDColumn;
    public TableColumn <Appointment, ?> ContactIDColumn;
    public TableColumn <Appointment, Integer> TypeColumn;


    public Label idLabel;
    public Label typeLabel;
    public Label locationLabel;
    public Label startLabel;


    public TextField AppidText;
    public TextField titleText;
    public TextField descriptionText;
    public TextField locationText;
    public TextField typeText;

    public Button AddButton;
    public Button UpdateButton;
    public Button DeleteButton;
    public Button viewCustomersButton;
    public DatePicker startDatePicker;
    public DatePicker EndDatePicker;
    public ComboBox startTimeComboBox;
    public ComboBox endTimeComboBox;
    public ComboBox<String> appointmentContactComboBox;

    public ComboBox CustomerIdComboBox;
    public ComboBox UserIdComboBox;
    public Button Exitbutton;
    public Button FormButton;
    public RadioButton WeekButton;
    public RadioButton MonthRadioButton;
    public RadioButton AllRadioButton;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");




    /**
     * takes you to the add appointment screen
     * @param actionEvent
     * @throws IOException
     */
    public void AddAppointmentButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
        Stage stage = (Stage) AddButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Update appointment method
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void updateButtonClicked(ActionEvent actionEvent) throws IOException, SQLException {

        boolean valid = false;
        //makes sure a selection is picked
        if (AppointmentTable.getSelectionModel().getSelectedItem() == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("ERROR: Choose an Appointment to update");
            alert.showAndWait();

        } else
        {

            Appointment selectedAppointment = (Appointment) AppointmentTable.getSelectionModel().getSelectedItem();
            int selectedId = Integer.parseInt(String.valueOf(selectedAppointment.getAppID()));
            //System.out.println("the selected Appointment ID is " + selectedId);

            //Takes in all the values from the form
            String title = titleText.getText();
            String description = descriptionText.getText();
            String location = locationText.getText();
            String type = typeText.getText();

            LocalDateTime localStart = LocalDateTime.of(startDatePicker.getValue(), parse((CharSequence) startTimeComboBox.getValue(),formatter));
            LocalDateTime localEnd = LocalDateTime.of(startDatePicker.getValue(), parse((CharSequence) endTimeComboBox.getValue(),formatter));
            int customerId = Integer.parseInt((String) CustomerIdComboBox.getValue());
            int userId = Integer.parseInt((String) UserIdComboBox.getValue());
            int contactId = Contact.getContactIDfromName(appointmentContactComboBox.getValue());


            //Validates if localEnd is after the beginning
            //diff will be 0 if the values are the same, positive if endTime is sooner than startTime, negative if entime is later than starttime
            //The value should be -1
            int diff = localStart.compareTo(localEnd);
           // System.out.println("diff value is " + diff);
            if(diff != -1)
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setContentText("The End time must be after the Start time!!");
                alert.showAndWait();
                return;
                }
            else
                {

                }


            if(Appointment.checkOverLap(customerId,selectedId,localStart,localEnd) == false )
            {
                   Appointment.updateAppointment(selectedId, title, description, location, type, localStart, localEnd, customerId, userId, contactId);
                    //Refreshes the Tableview
                    try
                    {
                        AppointmentTable.getItems().setAll(getAllAppointments());
                    } catch (SQLException e)
                    {
                        throw new RuntimeException(e);
                    }
            }

        }

    }

    /**
     *Delete Appointment method
      * @param actionEvent
     * @throws SQLException
     */
    public void deleteButtonClicked(ActionEvent actionEvent) throws SQLException {
        //makes sure a selection is picked
        if (AppointmentTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("ERROR: Choose an appointment to delete");
            alert.showAndWait();
        } else {
            Appointment selectedAppointment = (Appointment) AppointmentTable.getSelectionModel().getSelectedItem();
            int selectedId = Integer.parseInt(String.valueOf(selectedAppointment.getAppID()));
            System.out.println("the selected Appointment ID is " + selectedId);

            if(Appointment.deleteAppointment(selectedId) ==1)
            {
                System.out.println(selectedId + " was successfully deleted from the database");
                //refresh the tableView
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setContentText("The Appointment with the ID of : " + selectedId + " and Type: " + typeText.getText() + " successfuly removed from the database");
                alert.showAndWait();
                try {
                    AppointmentTable.getItems().setAll(getAllAppointments());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }


    @FXML
    /**
     * Shows the appointment details on the form
     * @Lambda contacts This helps by adding contact names into the combo box with less code
      */
    private void showAppointmentDetails(Appointment selectedAppointment) throws SQLException {

        ObservableList<Contact> contactsObservableList = Contact.getAllContacts();
        ObservableList<String> allContactsNames = FXCollections.observableArrayList();
        String displayContactName = "";
        //lambda #1
        contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
        appointmentContactComboBox.setItems(allContactsNames);

        //Filter the contact combobox
        if (AppointmentTable.getSelectionModel().getSelectedItem() != null) {
            for (Contact contact : contactsObservableList) {
                if (selectedAppointment.getContact() == contact.getId()) {
                    displayContactName = contact.getContactName();
                }
            }
        }

        if (AppointmentTable.getSelectionModel().getSelectedItem() != null) {
            AppidText.setText(String.valueOf(selectedAppointment.getAppID()));
            titleText.setText(selectedAppointment.getTitle());
            descriptionText.setText(selectedAppointment.getDescription());
            locationText.setText(selectedAppointment.getLocation());
            typeText.setText(selectedAppointment.getType());
            startDatePicker.setValue(selectedAppointment.getStart().toLocalDate());
            startTimeComboBox.setValue(selectedAppointment.getStart().atZone(ZoneId.systemDefault()).toString().substring(11, 16));
            EndDatePicker.setValue(LocalDate.from(selectedAppointment.getEnd().toLocalDate()));
            endTimeComboBox.setValue(selectedAppointment.getEnd().toLocalTime().toString());
            CustomerIdComboBox.setValue(String.valueOf(selectedAppointment.getCustomer_id()));
            UserIdComboBox.setValue(String.valueOf(selectedAppointment.getUser_id()));
            appointmentContactComboBox.setValue(String.valueOf(displayContactName));

        }
    }


    /**
     * The lambda users add the userIds to the userIdComboBox more efficiently and with less code,@lambda #4 ports Appointment information into the form when an appointment is picked.The justification for this lambda is it ports info to the form with less code and is cleaner
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    public void initialize(URL location, ResourceBundle resources) {

        //adds value to the table view
        AppId.setCellValueFactory(new PropertyValueFactory<>("AppID"));
        TitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        TypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        //second 5 columns
        StartColumn.setCellValueFactory(new PropertyValueFactory<>("Start"));
        EndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        CustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customer_id"));
        USERIDColumn.setCellValueFactory(new PropertyValueFactory<>("User_id"));
        ContactIDColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));





        //sets the times
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
        else if(offSet.equals("04:00"))
        {
            firstAppointment = MIN.plusHours(8);
            lastAppointment = MAX.minusHours(2).minusMinutes(45);
        }
        else if (offSet.equals("08:00")) {
            firstAppointment = MIN.plusHours(4);
            lastAppointment = MAX.minusHours(5).minusMinutes(45);

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


        ObservableList<String>allCustomerIds = FXCollections.observableArrayList();
        //Lambda #1
        /**
         * @Lambda #1
         */

        //Puts a list of CustomerIds into the CustomerId ComboBox
        ObservableList<Customer> CustomerIdObservableList = null;
        try {
            CustomerIdObservableList = Customer.getAllCustomers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        CustomerIdObservableList.forEach(customers -> allCustomerIds.add(customers.getId()) );
        CustomerIdComboBox.setItems(allCustomerIds);



        ObservableList<Integer>allUserIds = FXCollections.observableArrayList();

        try {
            allUserIds = User.getAllUserIds();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        UserIdComboBox.setItems(allUserIds);


        //Loads up all appointments by default
        try {
            AppointmentTable.getItems().setAll(getAllAppointments());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //shows appointment details on the form when clicked

        /**
         * @lambda #3 ports information into the form when an appointment is picked
         */
        AppointmentTable.getSelectionModel().selectedItemProperty().addListener
                    ((observable, oldValue, newValue) -> {
                        try {
                            showAppointmentDetails((Appointment) newValue);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

    }

    /**
     * Returns user to the customer screen
     * @param actionEvent
     * @throws IOException
     */
    public void viewCustomersButtonClick(ActionEvent actionEvent) throws IOException {
        //Returns to the Home Screen
        Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerScreen.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Closes the program
     * @param actionEvent
     */
    public void ExitButtonClick(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Takes the user to the reports page
      * @param actionEvent
     * @throws IOException
     */
    public void formButtonClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/ReportScreen.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * filters the appointment list to only show appointments in the current week
      * @param actionEvent
     */
    public void WeekRadioButtonClicked(ActionEvent actionEvent) {

        //creates a default appointment list with all appointments
        ObservableList<Appointment> allAppointmentsList;
        try {
            allAppointmentsList = getAllAppointments();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        AllRadioButton.setSelected(false);
        MonthRadioButton.setSelected(false);

        //Create a list for weekly Appointments
        ObservableList<Appointment> WeeklyAppointmentsList = FXCollections.observableArrayList();
        LocalDateTime weekStart = LocalDateTime.now().minusWeeks(1);
        LocalDateTime weekEnd = LocalDateTime.now().plusWeeks(1);
        if (allAppointmentsList != null)
            //IDE converted forEach
            allAppointmentsList.forEach(appointment -> {
                if (appointment.getEnd().isAfter(weekStart) && appointment.getEnd().isBefore(weekEnd))
                {
                    WeeklyAppointmentsList.add(appointment);
                }
            });
        AppointmentTable.getItems().clear();
        AppointmentTable.setItems(WeeklyAppointmentsList);
        AppointmentTable.refresh();
    }

    /**
     * Filters the appointment list to only show appointments this month
     * @param actionEvent
     */
    public void MonthRadioButtonClick(ActionEvent actionEvent) {
        //creates a default appointment list with all appointments
        ObservableList<Appointment> allAppointmentsList;
        try {
            allAppointmentsList = getAllAppointments();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        AllRadioButton.setSelected(false);
        WeekButton.setSelected(false);


        //Creates a list for the monthly appointments
        LocalDateTime currentMonthStart = LocalDateTime.now().minusMonths(1);
        LocalDateTime currentMonthEnd = LocalDateTime.now().plusMonths(1);
        ObservableList<Appointment> monthlyAppointmentList = FXCollections.observableArrayList();
        allAppointmentsList.forEach(appointment -> {
            if (appointment.getEnd().isAfter(currentMonthStart) && appointment.getEnd().isBefore(currentMonthEnd))
            {
                monthlyAppointmentList.add(appointment);
            }
        });
        AppointmentTable.getItems().clear();
        AppointmentTable.setItems(monthlyAppointmentList);
        AppointmentTable.refresh();
    }

    /**
     * Shows all the appointments when clicked
     * @param actionEvent
     */
    public void AllRadioButtonClicked(ActionEvent actionEvent) {
        //creates a default appointment list with all appointments
        ObservableList<Appointment> allAppointmentsList;
        try {
            allAppointmentsList = getAllAppointments();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        WeekButton.setSelected(false);
        MonthRadioButton.setSelected(false);

        AppointmentTable.getItems().clear();
        AppointmentTable.setItems(allAppointmentsList);
        AppointmentTable.refresh();
    }
}
