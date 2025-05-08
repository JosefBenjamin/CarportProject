package app.persistence;

import app.entities.User;
import app.entities.ZipCode;
import app.exceptions.DatabaseException;
import app.utilities.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {


    /**
     * ALERT! The user must type their password twice for confirmation,
     * that will be taken care of upstream before reaching this mapper
     *
     * @return a full user with address, but without the zipcode (and city) for now
     * @throws DatabaseException should be caught upstream in the UserController
     */

    public static User register(User user, ConnectionPool connectionPool) throws DatabaseException {

        String email = user.getEmail();
        String password = user.getPassword();
        int tlf = user.getTlf();
        boolean isAdmin = user.isAdmin();
        String address = user.getAddress();
        int zip = user.getZipCode().getZipCode();
        String city = user.getZipCode().getCity();

        String sql = "INSERT INTO public.users (email, password, tlf, is_admin, address, zip_code) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING user_id";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {


            String hashedPassword = PasswordUtil.hashPassword(password);
            ps.setString(1, email.trim());
            ps.setString(2, hashedPassword);
            ps.setInt(3, tlf);
            ps.setBoolean(4, isAdmin);
            ps.setString(5, address);
            ps.setInt(6, zip);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("user_id");
                    User newUser = new User(id, email, hashedPassword, tlf, isAdmin, address);
                    newUser.setZipCode(new ZipCode(zip, city));
                    return newUser;

                }
            }
            throw new DatabaseException("No user data was returned from the database");

        } catch (SQLException e) {
            throw new DatabaseException("Database error during registration", e);
        }

    }


    /**
     * Authenticate a user by e‑mail + plain‑text password.
     *
     * @param email         the e‑mail the user typed
     * @param plainPassword the plain‑text password the user typed
     * @return true if logged in, false if either email or (plain) password doesn't match respectively
     * @throws DatabaseException if the e‑mail is unknown, the password is wrong, or a DB error occurs
     */
    public static boolean login(String email, String plainPassword, ConnectionPool connectionPool) throws DatabaseException {


        String SQL = "SELECT password FROM  public.users WHERE  email = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    // Email not found
                    return false;
                }

                String hashedPassword = rs.getString("password");
                // Compare hashed password with the plainPassword provided via login page
                if (PasswordUtil.checkPassword(plainPassword, hashedPassword)) {
                    return true;   // credentials valid
                } else {
                    return false;  // wrong password
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error during login", e);
        }
    }


    /**
     *
     * @param connectionPool
     * @return true if email DOES NOT EXIST, and false if email DOES EXIST
     * @throws DatabaseException
     */


    public static boolean emailExist(String email, ConnectionPool connectionPool) throws DatabaseException {


        String sql = "SELECT email FROM public.users WHERE email = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
             ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next(); // returns true if email does NOT exist
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking email existence", e);
        }

    }

    public static User getUserByEmail(String email, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT u.user_id, u.email, u.password, u.tlf, u.is_admin, u.address, z.zip_code, z.city " +
                     "FROM public.users u " +
                     "JOIN public.zip_codes z ON u.zip_code = z.zip_code " +
                     "WHERE u.email = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("user_id");
                    String emailFromDb = rs.getString("email");
                    String passwordFromDb = rs.getString("password"); // Hashed password
                    int tlf = rs.getInt("tlf");
                    boolean isAdmin = rs.getBoolean("is_admin");
                    String address = rs.getString("address");
                    int zipCodeInt = rs.getInt("zip_code");
                    String cityName = rs.getString("city");

                    User user = new User(id, emailFromDb, passwordFromDb, tlf, isAdmin, address);
                    user.setZipCode(new ZipCode(zipCodeInt, cityName));
                    return user;
                } else {
                    return null; // User not found
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user by email: " + e.getMessage(), e);
        }
    }


}
