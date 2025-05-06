package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialMapper {

    public static List<Material> getMaterialsByID(int materialID, ConnectionPool connectionPool) throws DatabaseException {
        List<Material> materials = new ArrayList<>();

        String sql = "SELECT materials.material_id, materials.name, unit_name, meter_price, material_length.length " +
                "FROM materials " +
                "JOIN material_length ON materials.material_id = material_length.material_id " +
                "WHERE materials.material_id = ? ";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, materialID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("material_id");
                    String name = rs.getString("name");
                    String unitName = rs.getString("unit_name");
                    double price = rs.getDouble("meter_price");
                    int length = rs.getInt("length");

                    materials.add(new Material(id, name, unitName, price, length));
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error fetching materials by type", e);
        }

        return materials;
    }

}
