package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static void addMaterial(String name, String unitName, double meterPrice, String lengthsInput, ConnectionPool connectionPool) throws DatabaseException {
        String insertMaterialSql = "INSERT INTO materials (name, unit_name, meter_price) VALUES (?, ?, ?) RETURNING material_id";
        String insertLengthSql = "INSERT INTO material_length (material_id, length) VALUES (?, ?)";

        try (Connection connection = connectionPool.getConnection()) {
            int materialId;
            try (PreparedStatement ps = connection.prepareStatement(insertMaterialSql)) {
                ps.setString(1, name);
                ps.setString(2, unitName);
                ps.setDouble(3, meterPrice);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    materialId = rs.getInt("material_id");
                } else {
                    throw new DatabaseException("Failed to retrieve material_id after insertion");
                }
            }

            if (lengthsInput != null && !lengthsInput.trim().isEmpty()) {
                String[] lengthArray = lengthsInput.split(",");
                try (PreparedStatement ps = connection.prepareStatement(insertLengthSql)) {
                    for (String lengthStr : lengthArray) {
                        int length = Integer.parseInt(lengthStr.trim());
                        ps.setInt(1, materialId);
                        ps.setInt(2, length);
                        ps.executeUpdate();
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            throw new DatabaseException("Error adding material or lengths: " + e.getMessage(), e);
        }
    }

    public static void deleteMaterial(int materialId, ConnectionPool connectionPool) throws DatabaseException {
        // SQL to delete from material_length first
        String deleteLengthsSql = "DELETE FROM material_length WHERE material_id = ?";
        // SQL to delete from materials
        String deleteMaterialSql = "DELETE FROM materials WHERE material_id = ?";

        try (Connection connection = connectionPool.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // Delete associated lengths from material_length
                try (PreparedStatement psLengths = connection.prepareStatement(deleteLengthsSql)) {
                    psLengths.setInt(1, materialId);
                    psLengths.executeUpdate();
                }

                // Delete the material from materials
                try (PreparedStatement psMaterial = connection.prepareStatement(deleteMaterialSql)) {
                    psMaterial.setInt(1, materialId);
                    int rowsAffected = psMaterial.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new DatabaseException("Material with ID " + materialId + " not found.");
                    }
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new DatabaseException("Error deleting material with ID " + materialId + ": " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database connection error: " + e.getMessage());
        }
    }

public static int getLengthID(int materialID, int length, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT ml_id FROM material_length" +
                " WHERE material_id = ? AND length = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

             ps.setInt(1, materialID);
             ps.setInt(2, length);

             try (ResultSet rs = ps.executeQuery()) {
                 if (rs.next()) {
                     return rs.getInt("ml_id");
                 }
             }

        } catch (SQLException e) {
            throw new DatabaseException("Error fetching materials by length", e);
        }
        return 0;
    }
        public static List<Material> getAllMaterials(ConnectionPool connectionPool) throws DatabaseException {
            List<Material> materials = new ArrayList<>();
            Map<Integer, Material> materialMap = new HashMap<>();

            String materialSql = "SELECT material_id, name, unit_name, meter_price FROM materials";
            String lengthSql = "SELECT material_id, length FROM material_length";

            try (Connection connection = connectionPool.getConnection()) {
                // Fetch materials
                try (PreparedStatement materialPs = connection.prepareStatement(materialSql)) {
                    ResultSet rs = materialPs.executeQuery();
                    while (rs.next()) {
                        int materialId = rs.getInt("material_id");
                        String name = rs.getString("name");
                        String unitName = rs.getString("unit_name");
                        double meterPrice = rs.getDouble("meter_price");
                        Material material = new Material(materialId, name, unitName, meterPrice);
                        materialMap.put(materialId, material);
                        materials.add(material);
                    }
                }

                // Fetch lengths and associate them with materials
                try (PreparedStatement lengthPs = connection.prepareStatement(lengthSql)) {
                    ResultSet rs = lengthPs.executeQuery();
                    while (rs.next()) {
                        int materialId = rs.getInt("material_id");
                        int length = rs.getInt("length");
                        Material material = materialMap.get(materialId);
                        if (material != null) {
                            material.addLength(length);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new DatabaseException("Error fetching materials and lengths: " + e.getMessage(), e);

            }
            return materials;
        }
}
