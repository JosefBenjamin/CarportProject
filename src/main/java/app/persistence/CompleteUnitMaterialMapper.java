package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompleteUnitMaterialMapper {
    public static int getDescriptionId(String description, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT msd_id FROM material_setup_descriptions WHERE description=?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, description);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("msd_id");
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error getting descriptionId", e);
        }

        return 0;
    }

    public static void registerCUMToOrder(int quantity, int newOrderID, int lengthID, int descriptionId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO complete_unit_material (quantity, orders_id, ml_id, ms_description_id) VALUES(?,?,?,?)";

        try (Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, newOrderID);
            ps.setInt(3, lengthID);
            ps.setInt(4, descriptionId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Error inserting complete_unit_material_order", e);
        }
    }
}
