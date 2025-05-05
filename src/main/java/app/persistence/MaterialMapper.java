package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MaterialMapper {

    public static Material getMaterialByID(int materialID, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT materials.name, unit_name, meter_price, length FROM materials JOIN material_length ON materials.material_id = material_length.material_id WHERE materials.material_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, materialID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String unitName = rs.getString("unit_name");
                    double meterPrice = rs.getDouble("meter_price");
                    int materialLength = rs.getInt("length");
                    return new Material(materialID, name, unitName, meterPrice, materialLength);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding material", e);
        }
    }
}
