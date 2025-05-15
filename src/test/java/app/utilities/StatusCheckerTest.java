package app.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusCheckerTest {

    @Test
    void getStatusText() {
        StatusChecker checker = new StatusChecker();
        String actual = checker.getStatusText(1);

        assertEquals("Behandler", actual);
    }
}