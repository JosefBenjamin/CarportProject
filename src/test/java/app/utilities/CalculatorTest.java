package app.utilities;

import app.entities.CompleteUnitMaterial;
import app.entities.Material;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class CalculatorTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "cupcake";

    public static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void setUp() {

    }

    @Test
    void calculatePostAmount() throws DatabaseException {
        Calculator calculator = new Calculator(600, 700, 230, connectionPool);
        assertEquals(6, calculator.calculatePostAmount());
    }

    @Test
    public void testSelectsBeams_MinimalWaste() {
        // Given: a set of beams
        Calculator calculator = new Calculator(600, 855, 230, connectionPool);
        List<Material> beams = calculator.getBeams();



        /// When
        List<CompleteUnitMaterial> result = calculator.getOrderMaterials();

        // Then
        int totalLength = result.stream()
                .filter(m -> m.getDescription().contains("Remme"))
                .mapToInt(m -> m.getMaterial().getLength() * (m.getQuantity() / 2))
                .sum();

        assertTrue(totalLength >= 855);
        assertTrue(totalLength - 855 < 60, "Waste should be minimal");
    }

}