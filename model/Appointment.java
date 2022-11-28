package model;

import helper.JDBCDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class Appointment {

    //first 5
    private int AppID;
    private String title;
    private String description;
    private String location;
    private String type;

    //second 5
    private LocalDateTime start;
    private LocalDateTime end;
    private int customer_id;
    private int user_id;
    private int contact;


    /**
     * Constructor
      * @param AppId
     * @param title
     * @param description
     * @param location
     * @param type
     * @param start
     * @param end
     * @param customer_id
     * @param user_id
     * @param contact_id
     */
    public Appointment(int AppId, String title,String description, String location, String type, LocalDateTime start, LocalDateTime end, int customer_id, int user_id, int contact_id) {
        this.AppID = AppId;
        this.title = title;
        this.description= description;
        this.location = location;
        this.type = type;

        this.start =(start);
        this.end = (end);
        this.customer_id = customer_id;
        this.user_id = user_id;
        this.contact = contact_id;

    }

    /**
     * creates a list of all appointments
     * @return
     * @throws SQLException
     */
    public static ObservableList<Appointment> getAllAppointments() throws SQLException {
        ObservableList<Appointment> appointmentsObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * from appointments";
        PreparedStatement ps = JDBCDAO.getConn().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int appointmentID = rs.getInt("Appointment_ID");
            String appointmentTitle = rs.getString("Title");
            String appointmentDescription = rs.getString("Description");
            String appointmentLocation = rs.getString("Location");
            String type = rs.getString("Type");

            LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
            LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");
            int contactID = rs.getInt("Contact_ID");

            Appointment appointment = new Appointment(appointmentID, appointmentTitle, appointmentDescription, appointmentLocation, type, start, end, customerID, userID, contactID);
            appointmentsObservableList.add(appointment);
        }

        return appointmentsObservableList;
    }

    /**
     * Adds a new appointment to the databse
      * @param title
     * @param description
     * @param location
     * @param type
     * @param start
     * @param end
     * @param customer_id
     * @param user_id
     * @param contact_id
     * @return
     * @throws SQLException
     */
    public static int addNewAppointment (String title, String description, String location, String type,  LocalDateTime start, LocalDateTime end, int customer_id, int user_id, int contact_id) throws SQLException {

        Timestamp ts = new Timestamp(System.currentTimeMillis());

        String sql = " INSERT into Appointments(Title, Description, Location, Type, Start, End,Create_Date,Created_By,Last_Update,Last_Updated_By, Customer_ID, User_ID, Contact_ID) Values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = JDBCDAO.connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setTimestamp(5, Timestamp.valueOf(start));
        ps.setTimestamp(6, Timestamp.valueOf(end));
        ps.setTimestamp(7,ts);
        ps.setString(8,"App User");
        ps.setTimestamp(9,ts);
        ps.setString(10,"Script");
        ps.setInt(11,customer_id);
        ps.setInt(12,user_id);
        ps.setInt(13,contact_id);


        int rowsAffected = ps.executeUpdate();
        if(rowsAffected > 0)
        {
            System.out.println("INSERT Appointment successful");
        }
        else {
            System.out.println("INSERT Appointment FAILED");
        }
        return rowsAffected;

    }

    /**
     * Deletes an appointment from the database
     * @param app_id
     * @return
     * @throws SQLException
     */
    public static int deleteAppointment(int app_id) throws SQLException {

        String sql = "DELETE FROM Appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBCDAO.connection.prepareStatement(sql);
        ps.setInt(1,app_id);

        int rowsAffected = ps.executeUpdate();
        return rowsAffected;

    }


    /**
     * Updates the appointment database
     * @param AppointmentId
     * @param title
     * @param description
     * @param location
     * @param appType
     * @param start
     * @param end
     * @param customerId
     * @param userId
     * @param contactId
     * @return
     * @throws SQLException
     */
    public static int updateAppointment(int AppointmentId, String title, String description, String location, String appType, LocalDateTime start, LocalDateTime end, int customerId, int userId, int contactId) throws SQLException {

        System.out.println("update method called");
        String sql = " Update Appointments Set  Title =?, Description =?, Location =?, Type =?, Start =?, End =?, Customer_ID =?, User_ID =?, Contact_ID =? Where Appointment_ID =?";
        PreparedStatement ps = JDBCDAO.connection.prepareStatement(sql);

        ps.setString(1,title);
        ps.setString(2,description);
        ps.setString(3,location);
        ps.setString(4,appType);
        ps.setTimestamp(5, Timestamp.valueOf(start));
        ps.setTimestamp(6, Timestamp.valueOf((end)));
        ps.setInt(7,customerId);
        ps.setInt(8,userId);
        ps.setInt(9,contactId);
        ps.setInt(10,AppointmentId);

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected ==1)
        {
            System.out.println("Update Appointment query successful");
        }
        else {
            System.out.println("Update Failed");
        }
        return rowsAffected;
    }

    /**
     * This Method Checks if there is an appointment in 15 minutes an alerts the user
     */
    public static void CheckSoonAppointments()
    {
        ObservableList<Appointment> allAppointmentsList;
        try {
            allAppointmentsList = getAllAppointments();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        LocalDateTime currentTimeMinus15Min = LocalDateTime.now().minusMinutes(15);
        LocalDateTime currentTimePlus15Min = LocalDateTime.now().plusMinutes(15);
        LocalDateTime startDateTime = null;
        int AppointmentId = 0;
        boolean UpcomingAppointment = false;
        LocalDateTime displayTime = null;


        for (Appointment a: allAppointmentsList) {
            startDateTime = a.getStart();
            if ((startDateTime.isAfter(currentTimeMinus15Min) || startDateTime.isEqual(currentTimeMinus15Min)) && (startDateTime.isBefore(currentTimePlus15Min) || (startDateTime.isEqual(currentTimePlus15Min)))) {
                AppointmentId = a.getAppID();
                displayTime = startDateTime;
                UpcomingAppointment = true;
            }
        }
        if (UpcomingAppointment ==true) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alert Dialog");
            alert.setContentText("Theres an appointment within 15 minutes! AppointmentId " + AppointmentId + " starts at " + displayTime.toLocalDate().toString() + " " + displayTime.toLocalTime().toString());
            alert.showAndWait();

        }

        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No upcoming appointments.");
            Optional<ButtonType> confirmation = alert.showAndWait();

        }

    }

    /**
     * checks if the time periods overlap with
     * @param customer_id
     * @param appID
     * @param localStart
     * @param localEnd
     * @throws SQLException
     * @return overlap
     */
    public static boolean checkOverLap(int customer_id,int appID ,LocalDateTime localStart, LocalDateTime localEnd
    ) throws SQLException {

        boolean overlap = false;

        ArrayList<Appointment> appointmentsObservableList = new ArrayList<>();
        String sql = "SELECT * from appointments where Customer_ID = ?";
        PreparedStatement ps = JDBCDAO.getConn().prepareStatement(sql);
        ps.setInt(1, customer_id);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            int appointmentID = rs.getInt("Appointment_ID");
            String appointmentTitle = rs.getString("Title");
            String appointmentDescription = rs.getString("Description");
            String appointmentLocation = rs.getString("Location");
            String type = rs.getString("Type");

            LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
            LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");
            int contactID = rs.getInt("Contact_ID");

            Appointment appointment = new Appointment(appointmentID, appointmentTitle, appointmentDescription, appointmentLocation, type, start, end, customerID, userID, contactID);
            appointmentsObservableList.add(appointment);

        }

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setContentText("ERROR: ");


        for(Appointment a : appointmentsObservableList)
        {

            if(appID == a.getAppID())
            {
                System.out.println(" same appid");
            }
                else if (localStart.isBefore(a.getEnd()) && localStart.isAfter(a.getStart()) ) //checks if start time is in between another appointment
                {
                    overlap = true;
                    alert.setContentText("ERROR: start time is within another appointment time frame");
                    alert.showAndWait();
                    break;

                }
                else if (localEnd.isAfter(a.getStart()) && localEnd.isBefore(a.getEnd()) ) //checks if end time is in between another appointment
                {
                    overlap = true;
                    alert.setContentText("ERROR: end time is within another appointment time frame");
                    alert.showAndWait();
                    break;

                }
                else if (localStart.isEqual(a.getEnd()) || localStart.isEqual(a.getStart()) ) //checks if user start times are the same as another start or end
                {
                    overlap = true;
                    alert.setContentText("ERROR: start time is the same as another start or end time");
                    alert.showAndWait();
                    break;

                }
                else if (localEnd.isEqual(a.getStart()) || localEnd.isEqual(a.getEnd()) ) //checks if the user end times are the same as another start or end
                {
                    overlap = true;
                    alert.setContentText("ERROR: End time is the same as another start or end time");
                    alert.showAndWait();
                    break;

                }
                else if (localStart.isBefore(a.getStart()) && localEnd.isAfter(a.getEnd()) ) //checks if there is an appointment between User start and User end
                {
                    overlap = true;
                    alert.setContentText("ERROR: There is another appointment between this time frame");
                    alert.showAndWait();
                    break;

                }
                else
                {
                    System.out.println(" all checks have been made for app id " + a.getAppID());
                }
            }

        System.out.println(" overlap is " + overlap);
        return overlap;

    }



    /**
     *gets the appointment Id
     * @return appid
     */
    public int getAppID() {
        return AppID;
    }

    /**
     *gets the title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     *gest the description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     *gets the location
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     *gets the type
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     *gets the start date
     * @return start
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     *gets the end date
     * @return end
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     *gest the customer id
      * @return customer id
     */
    public int getCustomer_id() {
        return customer_id;
    }

    /**
     *gets the user id
     * @return user id
     */
    public int getUser_id() {
        return user_id;
    }

    /**
     *returns the contact id
     * @return contact
     */
    public int getContact() {
        return contact;
    }



}
