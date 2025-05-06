package app.persistence;

import app.entities.User;
import app.entities.ZipCode;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ZipCodeMapper {


    public static ZipCode registerZipCode(ZipCode zipCode, ConnectionPool connectionPool) throws DatabaseException {

        int zip = zipCode.getZipCode();
        String city = zipCode.getCity();

        String sql = "INSERT INTO public.zip_codes (zip_code, city) VALUES (?, ?) RETURNING zip_code, city";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, zip);
            ps.setString(2, city);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int resultZipcode = rs.getInt("zip_code");
                    String resultCity = rs.getString("city");
                    return new ZipCode(resultZipcode, resultCity);
                }

            }
            throw new DatabaseException("No zip_code data was returned from the database");


        } catch (SQLException e) {
                throw new DatabaseException("Database error during registration", e);
            }

        }


        public static boolean zipChecker(ZipCode zipCode, ConnectionPool connectionPool) throws DatabaseException {

            int zip = zipCode.getZipCode();
            String city = zipCode.getCity();

            String sql = "SELECT * FROM public.zip_codes WHERE zip_code = ? AND city = ?";

            try (Connection connection = connectionPool.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setInt(1, zip);
                ps.setString(2, city);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next(); // If a row exists return true, false if no row exists
                }

            } catch (SQLException e) {
                throw new DatabaseException("Database error during zip check", e);
            }
        }


      public static String getCityByZip(int zip, ConnectionPool connectionPool) throws DatabaseException {
          String sql = "SELECT city FROM public.zip_codes WHERE zip_code = ?";
          try (Connection connection = connectionPool.getConnection();
               PreparedStatement ps = connection.prepareStatement(sql)) {

              ps.setInt(1, zip);

              try (ResultSet rs = ps.executeQuery()) {
                  if (rs.next()) {
                      return rs.getString("city");
                  }
                  return null;   // zip not found
              }
          } catch (SQLException e) {
              throw new DatabaseException("Database error during city lookup by zip", e);
          }
      }

      public static Integer getZipByCity(String city, ConnectionPool connectionPool) throws DatabaseException {
          String sql = "SELECT zip_code FROM public.zip_codes WHERE city = ?";
          try (Connection connection = connectionPool.getConnection();
               PreparedStatement ps = connection.prepareStatement(sql)) {

              ps.setString(1, city);

              try (ResultSet rs = ps.executeQuery()) {
                  if (rs.next()) {
                      return rs.getInt("zip_code");
                  }
                  return null;   // city not found
              }
          } catch (SQLException e) {
              throw new DatabaseException("Database error during zip lookup by city", e);
          }
      }
}
