package model;

import helper.JDBCDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Country {
    private int countryID;
    private String countryName;
    private int count;

    /**
     * @param countryID
     * @param countryName
     */
    public Country(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     *
     * @param countryCount
     * @param countryName
     * @param countryID
     */
    public Country(int countryCount, String countryName, int countryID){
        this.count = countryCount;
        this.countryID = countryID;
        this.countryName = countryName;

    }

    /**
     * creates a list of countries
     * @return
     * @throws SQLException
     */
    public static ObservableList<Country> getCountries() throws SQLException {
        ObservableList<Country> countriesObservableList = FXCollections.observableArrayList();
        String sql = "SELECT Country_ID, Country from countries";
        PreparedStatement ps = JDBCDAO.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int countryID = rs.getInt("Country_ID");
            String countryName = rs.getString("Country");
            Country country = new Country(countryID, countryName);
            countriesObservableList.add(country);
        }
        return countriesObservableList;
    }

    /**
     * Returns a list of country names and counts of the instances
     * @return
     * @throws SQLException
     */
    public static ObservableList<Country> getCountryCounts() throws SQLException {
        ObservableList<Country> countriesObservableList = FXCollections.observableArrayList();
        String sql = "select countries.Country, countries.Country_ID, countries.Country,count(*) as countryCount from customers inner join first_level_divisions on customers.Division_ID = first_level_divisions.Division_ID inner join countries on countries.Country_ID = first_level_divisions.Country_ID where  customers.Division_ID = first_level_divisions.Division_ID group by first_level_divisions.Country_ID order by count(*) desc";
        PreparedStatement ps = JDBCDAO.getConn().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {


            int count = rs.getInt("countryCount");
            String countryName = rs.getString("Country");
            int countryId = rs.getInt("Country_ID");
            Country report = new Country(count, countryName,countryId);
            countriesObservableList.add(report);
            //System.out.println("country count i = " + count);

        }

        return countriesObservableList;
    }

    /**
     *gest the country ID
     * @return countryID
     */
    public int getCountryID() {

        return countryID;
    }


    /**
     * This returns the count of a country
     * @return
     */
    public int getCount()
    {
        return count;
    }

    /**
     *gets the country name
     * @return countryName
     */
    public String getCountryName() {

        return countryName;
    }
}