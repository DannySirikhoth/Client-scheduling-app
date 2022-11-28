package model;

import helper.JDBCDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Report {

    private int countryCount;
    private String countryName;
   

    /**
     * @param countryName
     * @param countryCount
     */
    public Report(String countryName, int countryCount) {
        this.countryCount = countryCount;
        this.countryName = countryName;

    }


    /**
     * Returns country name for custom report.
     * @return countryName
     */
    public String getCountryName() {

        return countryName;
    }

    /**
     * Total for each country.
     * @return countryCount
     */
    public int getCountryCount() {

        return countryCount;
    }

    /**
     * Embedded class for the month Report
      */
    public static class ReportMonth {
        public String appMonth;
        public int appMonthTotal;

        /**
         * @param appointmentMonth
         * @param appointmentTotal
         */
        public ReportMonth(String appointmentMonth, int appointmentTotal) {
            this.appMonth = appointmentMonth;
            this.appMonthTotal = appointmentTotal;
        }

        /**
         * returns the app month
         * @return appointmentMonth
         */
        public String getAppMonth() {

            return appMonth;
        }

        /**
         * returns the app total
         * @return appointmentTotal
         */
        public int getAppMonthTotal() {

            return appMonthTotal;
        }
    }

    /**
     * Embedded class for the report type
      */
    public static class ReportType {
        public String appType;
        public int appTypeTotal;

        /**
         *constructor for report type
         * @param appointmentTotal
         * @param appointmentType
         */
        public ReportType(String appointmentType, int appointmentTotal) {
            this.appType = appointmentType;
            this.appTypeTotal = appointmentTotal;
        }

        /**
         * returns the appointment type
         */
        public String getAppType() {

            return appType;
        }

        /**
         *returns the appointment total
          * @return appTypeTotal
         */
        public int getAppTypeTotal() {


            return appTypeTotal;
        }

    }



    /**
     * SQL Query that pulls the exact data needed: Countries and total appointment per country.
     * @throws SQLException
     * @return countriesObservableList
     */
    public static ObservableList<Report> getCountries() throws SQLException {
        ObservableList<Report> countriesObservableList = FXCollections.observableArrayList();
        String sql = "select countries.Country, count(*) as countryCount from customers inner join first_level_divisions on customers.Division_ID = first_level_divisions.Division_ID inner join countries on countries.Country_ID = first_level_divisions.Country_ID where  customers.Division_ID = first_level_divisions.Division_ID group by first_level_divisions.Country_ID order by count(*) desc";
        PreparedStatement ps = JDBCDAO.getConn().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {

            String countryName = rs.getString("Country");
            int countryCount = rs.getInt("countryCount");
            Report report = new Report(countryName, countryCount);
            countriesObservableList.add(report);

        }

        return countriesObservableList;
    }

}