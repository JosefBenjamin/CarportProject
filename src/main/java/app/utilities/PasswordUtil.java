package app.utilities;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    /**
     *
     * @param plainPassword plain text password go through param before being saved to the Database
     * @return a String of the hashed and salted password, meaning even though two users use '1234' as their password,
     * in the database both will be unique
     */

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     *
     * @param plainPassword users plaintext password
     * @param hashedPasswordFromDB the hashed password generated via the hashPassword method
     * @return returns true if the passwords are matching and false if they are not
     */

    public static boolean checkPassword(String plainPassword, String hashedPasswordFromDB) {
            return BCrypt.checkpw(plainPassword, hashedPasswordFromDB);
        }

}
