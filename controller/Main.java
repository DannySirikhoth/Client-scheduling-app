package controller;

import helper.JDBCDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.time.*;

/**
 * WGU C195 Software II
 * Danny Sirikhoth
 * Client Scheduling App
 * @main extends Application
 */

/**
 * Main method extends application
 * this method starts the program
 */
public class Main extends Application {


    /**
     * start the javafx application
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    public void start(Stage stage) throws Exception {
        //tells which fxml file to load
        Parent root = FXMLLoader.load((getClass().getResource("/view/login.fxml")));
        stage.setScene(new Scene( root,550,290));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    /**
     * @main
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {

        //Opens the jdbc connection
        JDBCDAO.openConnection();

        LocalDate myLD = LocalDate.of(2022,07,18 );
        LocalTime myLT = LocalTime.of(16,46);
        LocalDateTime myLDT = LocalDateTime.of(myLD,myLT);
        ZoneId myZoneId = ZoneId.systemDefault();
        ZonedDateTime myZDT = ZonedDateTime.of(myLDT,myZoneId);
        //System.out.println("changing localdatetime with ozone to new york " + myLDT.atZone(ZoneId.of("America/New_York")));
        System.out.println(myZoneId);
        System.out.println(myZDT);
        System.out.println(myZDT.toLocalDate());
        System.out.println(myZDT.toLocalTime());
        System.out.println(myZDT.toLocalDateTime());

        System.out.println(" istnat.now = " + OffsetTime.now().toString().substring(16,21));


        //use this to update into the db
        System.out.println(myZDT.toLocalDate().toString() + " " + myZDT.toLocalTime().toString());

        //convert from utc to local
        System.out.println("USer Time " + myZDT);
        ZoneId utcZoneId = ZoneId.of("UTC");

        ZonedDateTime utcZDT = ZonedDateTime.ofInstant(myZDT.toInstant(),utcZoneId);
        System.out.println("user time to UTC " + utcZDT);

        myZDT = ZonedDateTime.ofInstant(utcZDT.toInstant(), myZoneId);
        System.out.println("UTC to user time "+myZDT);


        //launches the javafx window
        launch(args);

        //close jdbc connection
        JDBCDAO.closeConnection();

    }

}
