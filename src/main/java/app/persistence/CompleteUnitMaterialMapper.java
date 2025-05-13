package app.persistence;

import app.entities.CompleteUnitMaterial;
import app.entities.Material;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public static List<CompleteUnitMaterial> getCompleteUnitMaterialsByOrderId(int orderID, ConnectionPool connectionPool) throws DatabaseException {
        List<CompleteUnitMaterial> billOfMaterials = new ArrayList<>();
        String sql = "SELECT *" +
                " FROM complete_unit_material" +
                " JOIN material_length ON complete_unit_material.ml_id = material_length.ml_id" +
                " JOIN materials ON material_length.material_id = materials.material_id" +
                " JOIN material_setup_descriptions ON complete_unit_material.ms_description_id = material_setup_descriptions.msd_id" +
                " WHERE orders_id = ?;";

        try (Connection connection = connectionPool.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CompleteUnitMaterial completeUnitMaterial = new CompleteUnitMaterial(rs.getInt("quantity"),
                            rs.getString("description"), new Material(rs.getInt("material_id"),
                            rs.getString("name"), rs.getString("unit_name"),
                            rs.getDouble("meter_price"), rs.getInt("length")));

                    billOfMaterials.add(completeUnitMaterial);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error getting Bill of Materials", e);
        }
        return billOfMaterials;
    }
}
