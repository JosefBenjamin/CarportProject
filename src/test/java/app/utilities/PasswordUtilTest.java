package app.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void hashPasswordAndCheck() {
        String hashedPassword = PasswordUtil.hashPassword("password");
        boolean expectedResult = PasswordUtil.checkPassword("password", hashedPassword);

        assertTrue(expectedResult);

    }


}