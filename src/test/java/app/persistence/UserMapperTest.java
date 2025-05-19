package app.persistence;

import app.entities.User;
import app.entities.ZipCode;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "cupcake";

    private static ConnectionPool connectionPool;
    private static UserMapper userMapper;

    @BeforeAll
    public static void setUpClass() {
        try {
            connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
            userMapper = new UserMapper();
            try (Connection testConnection = connectionPool.getConnection()) {
                try (Statement stmt = testConnection.createStatement()) {
                    stmt.execute("CREATE SCHEMA IF NOT EXISTS test");
                    // The test schema is already created, so we only need to delete/create test tables
                    stmt.execute("DROP TABLE IF EXISTS test.users");
                    stmt.execute("DROP TABLE IF EXISTS test.complete_unit_material");
                    stmt.execute("DROP TABLE IF EXISTS test.material_length");
                    stmt.execute("DROP TABLE IF EXISTS test.material_setup_descriptions");
                    stmt.execute("DROP TABLE IF EXISTS test.materials");
                    stmt.execute("DROP TABLE IF EXISTS test.orders");
                    stmt.execute("DROP TABLE IF EXISTS test.zip_codes");

                    // Create tables as copy of original public schema structure
                    stmt.execute("CREATE TABLE IF NOT EXISTS test.users (LIKE public.users INCLUDING ALL)");
                    stmt.execute("CREATE TABLE IF NOT EXISTS test.complete_unit_material (LIKE public.complete_unit_material INCLUDING ALL)");
                    stmt.execute("CREATE TABLE IF NOT EXISTS test.material_length (LIKE public.material_length INCLUDING ALL)");
                    stmt.execute("CREATE TABLE IF NOT EXISTS test.material_setup_descriptions (LIKE public.material_setup_descriptions INCLUDING ALL)");
                    stmt.execute("CREATE TABLE IF NOT EXISTS test.materials (LIKE public.materials INCLUDING ALL)");
                    stmt.execute("CREATE TABLE IF NOT EXISTS test.orders (LIKE public.orders INCLUDING ALL)");
                    stmt.execute("CREATE TABLE IF NOT EXISTS test.zip_codes (LIKE public.zip_codes INCLUDING ALL)");
                    // Example for users table
                    stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.users_user_id_seq");
                    stmt.execute("ALTER TABLE test.users ALTER COLUMN user_id SET DEFAULT nextval('test.users_user_id_seq')");

// complete_unit_material
                    stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.complete_unit_material_cum_id_seq");
                    stmt.execute("ALTER TABLE test.complete_unit_material ALTER COLUMN cum_id SET DEFAULT nextval('test.complete_unit_material_cum_id_seq')");

// material_length
                    stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.material_length_ml_id_seq");
                    stmt.execute("ALTER TABLE test.material_length ALTER COLUMN ml_id SET DEFAULT nextval('test.material_length_ml_id_seq')");

// material_setup_descriptions
                    stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.material_setup_descriptions_msd_id_seq");
                    stmt.execute("ALTER TABLE test.material_setup_descriptions ALTER COLUMN msd_id SET DEFAULT nextval('test.material_setup_descriptions_msd_id_seq')");

// materials
                    stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.materials_material_id_seq");
                    stmt.execute("ALTER TABLE test.materials ALTER COLUMN material_id SET DEFAULT nextval('test.materials_material_id_seq')");

// orders
                    stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.orders_order_id_seq");
                    stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");



                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                fail("Database connection failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        try (Connection testConnection = connectionPool.getConnection()) {
            try (Statement stmt = testConnection.createStatement()) {
                // Clear test data (exclude materials and descriptions from DELETE)
                stmt.execute("DELETE FROM test.users");
                stmt.execute("DELETE FROM test.complete_unit_material");
                stmt.execute("DELETE FROM test.material_length");
                stmt.execute("DELETE FROM test.orders");
                stmt.execute("DELETE FROM test.zip_codes");

                // Refresh materials and descriptions from public
                stmt.execute("DELETE FROM test.materials");
                stmt.execute("INSERT INTO test.materials SELECT * FROM public.materials");

                stmt.execute("DELETE FROM test.material_length");
                stmt.execute("INSERT INTO test.material_length SELECT * FROM public.material_length");

                stmt.execute("DELETE FROM test.material_setup_descriptions");
                stmt.execute("INSERT INTO test.material_setup_descriptions SELECT * FROM public.material_setup_descriptions");

                // Reset sequences
                // These tables are cleared and you want predictable ID starts from 1
                stmt.execute("SELECT setval('test.users_user_id_seq', 1, false)");
                stmt.execute("SELECT setval('test.complete_unit_material_cum_id_seq', 1, false)");
                stmt.execute("SELECT setval('test.orders_order_id_seq', 1, false)");

                // These tables are repopulated from public schema, so continue after MAX(id)
                stmt.execute("SELECT setval('test.material_length_ml_id_seq', COALESCE((SELECT MAX(ml_id) FROM test.material_length), 1), true)");
                stmt.execute("SELECT setval('test.material_setup_descriptions_msd_id_seq', COALESCE((SELECT MAX(msd_id) FROM test.material_setup_descriptions), 1), true)");
                stmt.execute("SELECT setval('test.materials_material_id_seq', COALESCE((SELECT MAX(material_id) FROM test.materials), 1), true)");
            }
        } catch (SQLException e) {
            fail("Database setup failed: " + e.getMessage());
        }
    }



    //Connection is all good
    @Test
    void testConnection() throws SQLException { //Test is successful
        //assertFalse to make test fail
        assertNotNull(connectionPool.getConnection());
    }

    @Test
    void registerAndGetUser() {
        try {
            User newUser = new User("test@mail.dk", "12345678", 42756486, false, "Test vej");
            newUser.setZipCode(new ZipCode(2800, "Lyngby"));
            User expected = UserMapper.register(newUser, connectionPool);
            assertNotNull(expected);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void login() {
        try {
            User newUser = new User("test@mail.dk", "12345678", 42756486, false, "Test vej");
            newUser.setZipCode(new ZipCode(2800, "Lyngby"));
            UserMapper.register(newUser, connectionPool);
            assertTrue(UserMapper.login("test@mail.dk", "12345678" , connectionPool));
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void emailExist() {
        try {
            User newUser = new User("test@mail.dk", "12345678", 42756486, false, "Test vej");
            newUser.setZipCode(new ZipCode(2800, "Lyngby"));
            UserMapper.register(newUser, connectionPool);
            assertTrue(UserMapper.emailExist(newUser.getEmail(), connectionPool));
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void getUserById() {
        try {
            User newUser = new User("test@mail.dk", "12345678", 42756486, false, "Test vej");
            newUser.setZipCode(new ZipCode(2800, "Lyngby"));
            User expected = UserMapper.register(newUser, connectionPool);
            UserMapper.getUserById(expected.getUserID(), connectionPool);
            assertNotNull(expected);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateMail() {
        try {
            User newUser = new User("test@mail.dk", "12345678", 42756486, false, "Test vej");
            newUser.setZipCode(new ZipCode(2800, "Lyngby"));
            User registeredUser = UserMapper.register(newUser, connectionPool);
            UserMapper.updateMail("newmail@mail.dk", registeredUser.getUserID(), connectionPool);


            User expected = UserMapper.getUserById(registeredUser.getUserID(), connectionPool);
            assertEquals(expected.getEmail(), "newmail@mail.dk");
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updatePassword() {
    }

    @Test
    void updateTlf() {
    }

    @Test
    void updateAddress() {
    }

    @Test
    void updateCityAndZipCode() {
    }
}