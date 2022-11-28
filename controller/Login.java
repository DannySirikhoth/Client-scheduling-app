package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;
import model.User;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class Login implements Initializable {

    public Button LoginButton;
    public Button cancelButton;
    public Label TimeLabel;
    public Label titleLabel;
    public Label GraphicLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField userNameField;

    ResourceBundle rb;

    Alert a = new Alert(Alert.AlertType.ERROR);
    ResourceBundle resources = ResourceBundle.getBundle("helper/nat_fr");

    /**
     * Login button functions
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void loginButtonClick(ActionEvent actionEvent) throws IOException, SQLException
    {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("login_activity.txt", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PrintWriter outputFile = new PrintWriter(fileWriter);

        if(userNameField.getText().isBlank())
        {
            if(Locale.getDefault().getLanguage().equals("fr")) {
                a.setContentText("Merci d'entrer un nom d'utlisateur");
            }
            else{
                a.setContentText("Please Enter a Username");
            }
            a.show();
        }

        else if(passwordField.getText().isBlank())
        {
            if(Locale.getDefault().getLanguage().equals("fr")) {
                a.setContentText("veuillez entrer un mot de passe");
            }
            else{
                a.setContentText("Please Enter a Password!");
            }
            a.show();
        }

        else
        {
            if (User.checkCredentials(userNameField.getText(), passwordField.getText()) == true)
            {
                outputFile.print("user: " + userNameField.getText() + " logged in at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");
                outputFile.close();
                Appointment.CheckSoonAppointments(); //Checks it there is an appointment within 15 minutes
                System.out.println("Login verified");
                //Opens new scene
                Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerScreen.fxml"));
                Stage stage = (Stage) LoginButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } else {
                //log the failed login attempt
                outputFile.print("user: " + userNameField.getText() + " failed to login at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");
                outputFile.close();
                //Alert that tells the user they set invalid credentials
                System.out.println("Please Enter Valid Credentials");
                if(Locale.getDefault().getLanguage().equals("fr")) {
                    a.setContentText("Incorrecte  Identifiants");
                }
                else{
                    a.setContentText("Invalid Credentials");
                }

                a.show();

            }
        }
    }

    /**
     *Closes the app
     * @param actionEvent
     */
    public void CancelButtonClick(ActionEvent actionEvent) {

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }


    /**
     * initialize the login screen
      * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    public void initialize(URL location, ResourceBundle resources) {

        TimeLabel.setText(String.valueOf(ZoneId.systemDefault()));

        Locale locale = Locale.getDefault();
        Locale.setDefault(locale);


       

        rb = ResourceBundle.getBundle("helper/nat", Locale.getDefault());
        System.out.println(Locale.getDefault());

            LoginButton.setText(rb.getString("Login"));
            userNameField.setPromptText(rb.getString("username"));
            passwordField.setPromptText(rb.getString("Password"));
            cancelButton.setText(rb.getString("Exit"));
            titleLabel.setText(rb.getString("LoginForm"));
            GraphicLabel.setText(rb.getString("Danny"));
            a.setContentText(rb.getString("Alert"));



    }


}
