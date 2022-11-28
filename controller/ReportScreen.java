package controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import model.Country;
import model.Report;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Month;
import java.util.Collections;
import java.util.ResourceBundle;

import static model.Appointment.getAllAppointments;

public class ReportScreen implements Initializable {

    public TableView ContactReportTableView;
    public TableView CountryReportTableView;
    public ComboBox ContactNameComboBox;
    public Button ViewCustomerButton;
    public Button ViewAppointmentButton;
    public Button ExitButton;



    //--------------------First TableView on First tab

    public TableColumn CustomerIDColumnContact;
    public TableColumn EndColumnContact;
    public TableColumn StartColumnContact;
    public TableColumn TypeColumnContact;
    public TableColumn descriptionColumnContact;
    public TableColumn TitleColumnContact;
    public TableColumn AppIdContact;

    //----------------------Second Tab ---------
    public TableView reportMonthTableView;
    public TableView ReportTypeTableView;
    public TableColumn ReportTypeTotalColumn;
    public TableColumn TypeColumn;
    public TableColumn reportMonthCOlumn;
    public TableColumn reportMonthTotalcolumn;
    //----------------------third tab country tablelview-------------

    public TableColumn CountryNameColumn;
    public TableColumn TotalCountColumn;


    /**
     * initialize the report screen
      * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    public void initialize(URL location, ResourceBundle resources) {

        //--------------------First Tab------------------------------
        CustomerIDColumnContact.setCellValueFactory(new PropertyValueFactory<>("Customer_id"));
        EndColumnContact.setCellValueFactory(new PropertyValueFactory<>("End"));
        StartColumnContact.setCellValueFactory(new PropertyValueFactory<>("Start"));
        TypeColumnContact.setCellValueFactory(new PropertyValueFactory<>("Type"));
        descriptionColumnContact.setCellValueFactory(new PropertyValueFactory<>("Description"));
        TitleColumnContact.setCellValueFactory(new PropertyValueFactory<>("Title"));
        AppIdContact.setCellValueFactory(new PropertyValueFactory<>("AppID"));


        //Puts a list of contacts into the contact ComboBox
        ObservableList<Contact> contactsObservableList = null;
        try {
            contactsObservableList = Contact.getAllContacts();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<String> allContactsNames = FXCollections.observableArrayList();

        contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
        ContactNameComboBox.setItems(allContactsNames);

        try {
            ContactReportTableView.getItems().setAll(getAllAppointments());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        //--------------------Second Tab ----------------------------------
        //Type TableView
        TypeColumn.setCellValueFactory(new PropertyValueFactory<>("AppType"));
        ReportTypeTotalColumn.setCellValueFactory(new PropertyValueFactory<>("AppTypeTotal"));

        //Month TableView---------------
        reportMonthCOlumn.setCellValueFactory(new PropertyValueFactory<>("AppMonth"));
        reportMonthTotalcolumn.setCellValueFactory(new PropertyValueFactory<>("AppMonthTotal"));
        ObservableList<Appointment> AllAppointmentsList;
        try {
            AllAppointmentsList = getAllAppointments();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        ObservableList<Month> individualMonth = FXCollections.observableArrayList();
        ObservableList<Month> monthOfAppointmentsList = FXCollections.observableArrayList();

        ObservableList<String> appointmentType = FXCollections.observableArrayList();
        ObservableList<String> uniqueAppointment = FXCollections.observableArrayList();

        ObservableList<Report.ReportType> reportType = FXCollections.observableArrayList();
        ObservableList<Report.ReportMonth> reportMonths = FXCollections.observableArrayList();

        AllAppointmentsList.forEach(appointments -> {
            appointmentType.add(appointments.getType());
        });



        AllAppointmentsList.stream().map(appointment -> {
            return appointment.getStart().getMonth();
        }).forEach(monthOfAppointmentsList::add);

        monthOfAppointmentsList.stream().filter(month -> {
            return !monthOfAppointmentsList.contains(month);
        }).forEach(monthOfAppointmentsList::add);

        //Separating the individual types of appointments
        for (Appointment appointments : AllAppointmentsList) {
            String appointmentsAppointmentType = appointments.getType();
            if (!uniqueAppointment.contains(appointmentsAppointmentType)) {
                uniqueAppointment.add(appointmentsAppointmentType);
            }
        }

        //Separating each month value
        for(Month months :monthOfAppointmentsList){
            Month m = months;
            if(!individualMonth.contains(m)){
                 individualMonth.add(m);
            }
        }

        //set month TableView
        for (Month month : individualMonth) {
            String monthName = String.valueOf(month);
            int monthTotal = Collections.frequency(monthOfAppointmentsList, month);
           Report.ReportMonth appointmentMonth = new Report.ReportMonth(monthName, monthTotal);
            reportMonths.add(appointmentMonth);
        }
        reportMonthTableView.setItems(reportMonths);

        //set type TableView
        for (String type : uniqueAppointment) {
            String typeToSet = type;
            int typeTotal = Collections.frequency(appointmentType, type);
            Report.ReportType appointmentTypes = new Report.ReportType(typeToSet, typeTotal);
            reportType.add(appointmentTypes);
        }
        ReportTypeTableView.setItems(reportType);


        //--------------------Third Tab --------------------------------------

        CountryNameColumn.setCellValueFactory(new PropertyValueFactory<>("CountryName"));
        TotalCountColumn.setCellValueFactory(new PropertyValueFactory<>("Count"));

        try {

            ObservableList<Country> aggregatedCountries = Country.getCountryCounts();
            ObservableList<Country> countriestoadd = FXCollections.observableArrayList();

            aggregatedCountries.forEach(countriestoadd::add);
            CountryReportTableView.getItems().setAll(Country.getCountryCounts());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Takes user to the Customer Screen
     * @param actionEvent
     * @throws IOException
     */
    public void ViewCustomerButtonClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerScreen.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Takes User to the Appointment Screen
      * @param actionEvent
     * @throws IOException
     */
    public void ViewAppointmentBUttonClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/AppointmentScreen.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Closes the program
      * @param actionEvent
     */
    public void ExitButtonClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Changes the Contact to be viewed by the user
     * @param actionEvent
     * @throws SQLException
     * @lambda This lambda is justified because it lets me add values to the appointment list without having to implement a method
     */
    public void ChangeContact(ActionEvent actionEvent) throws SQLException {

        //creates a default appointment list with all appointments
        ObservableList<Appointment> allAppointmentsList;
        try {
            allAppointmentsList = getAllAppointments();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int custID = Contact.getContactIDfromName((String) ContactNameComboBox.getValue());

        /**
         * @Lambda adds values to an appointment list
         */
        ObservableList<Appointment> newAppointmentList = FXCollections.observableArrayList();
        allAppointmentsList.forEach(appointment -> {

                if (appointment.getCustomer_id() == custID)
                {
                    newAppointmentList.add(appointment);
                }

        });

            ContactReportTableView.getItems().clear();
            ContactReportTableView.setItems(newAppointmentList);
            ContactReportTableView.refresh();

}
}
