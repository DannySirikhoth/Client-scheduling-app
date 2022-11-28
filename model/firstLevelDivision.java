package model;

import helper.JDBCDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class firstLevelDivision {
    private int divisionID;
    private String divisionName;
    public int country_ID;

    /**
     *
     * @param divisionID
     * @param country_ID
     * @param divisionName
     */
    public firstLevelDivision(int divisionID, String divisionName, int country_ID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.country_ID = country_ID;
    }


    /**
     * creates a list of first level divisions
     * @return
     * @throws SQLException
     */
    public static ObservableList<firstLevelDivision> getFirstLevelDivisions() throws SQLException {
        ObservableList<firstLevelDivision> firstLevelDivisionsObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * from first_level_divisions";
        PreparedStatement ps = JDBCDAO.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            int country_ID = rs.getInt("COUNTRY_ID");
            firstLevelDivision firstLevelDivision = new firstLevelDivision(divisionID, divisionName, country_ID);
            firstLevelDivisionsObservableList.add(firstLevelDivision);
        }
        return firstLevelDivisionsObservableList;
    }



    /**
     *returns the division id
     * @return divisionID
     */
    public int getDivisionID() {

        return divisionID;
    }

    /**
     *returns the first level division name
     * @return divisionName
     */
    public String getDivisionName() {

        return divisionName;
    }

    /**
     *returns the country name
     * @return country_ID
     */
    public int getCountry_ID() {

        return country_ID;
    }

}