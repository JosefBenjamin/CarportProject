package app.persistence;


import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderMapper {

    public static Map<Integer, String> getDescriptions(ConnectionPool connectionPool) throws DatabaseException {
        Map<Integer, String> descriptions = new HashMap<>();
        String descriptionSql = "SELECT msd_id, description FROM public.material_setup_descriptions";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(descriptionSql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int msdId = rs.getInt("msd_id");
                String description = rs.getString("description");
                descriptions.put(msdId, description);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Error fetching material setup descriptions", e);
        }

        return descriptions;
    }

    public static List<Order> getAllOrdersWithDetails(ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orders = new ArrayList<>();
        Map<Integer, List<CompleteUnitMaterial>> orderMaterialsMap = new HashMap<>();

        String orderSql = "SELECT order_id, user_id, carport_width, carport_length, carport_height, date, total_price, status FROM public.orders";

        String materialSql = """
                SELECT cum.cum_id, cum.quantity, cum.orders_id, cum.ml_id, cum.ms_description_id,
                       m.name AS material_name
                FROM public.complete_unit_material cum
                JOIN public.material_length ml ON cum.ml_id = ml.ml_id
                JOIN public.materials m ON ml.material_id = m.material_id
                WHERE cum.orders_id = ?
                """;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement orderPs = connection.prepareStatement(orderSql)) {

            ResultSet orderRs = orderPs.executeQuery();
            while (orderRs.next()) {
                int orderId = orderRs.getInt("order_id");
                int userId = orderRs.getInt("user_id");
                int carportWidth = orderRs.getInt("carport_width");
                int carportLength = orderRs.getInt("carport_length");
                int carportHeight = orderRs.getInt("carport_height");
                LocalDate date = orderRs.getDate("date").toLocalDate();
                double totalPrice = orderRs.getDouble("total_price");
                int status = orderRs.getInt("status");

                Carport carport = new Carport(carportWidth, carportLength, carportHeight);
                Order order = new Order(orderId, userId, carport, date, totalPrice, status);

                // Fetch the associated user
                User user = UserMapper.getUserById(userId, connectionPool);
                order.setUser(user);

                orders.add(order);

                // Fetch materials for this order
                try (PreparedStatement materialPs = connection.prepareStatement(materialSql)) {
                    materialPs.setInt(1, orderId);
                    ResultSet materialRs = materialPs.executeQuery();
                    List<CompleteUnitMaterial> materials = new ArrayList<>();

                    while (materialRs.next()) {
                        int cumId = materialRs.getInt("cum_id");
                        int quantity = materialRs.getInt("quantity");
                        int ordersId = materialRs.getInt("orders_id");
                        int mlId = materialRs.getInt("ml_id");
                        int msdId = materialRs.getInt("ms_description_id");
                        String materialName = materialRs.getString("material_name");

                        CompleteUnitMaterial cum = new CompleteUnitMaterial(cumId, quantity, materialName, ordersId, mlId, msdId);
                        materials.add(cum);
                    }
                    orderMaterialsMap.put(orderId, materials);
                }
            }

            for (Order order : orders) {
                List<CompleteUnitMaterial> materials = orderMaterialsMap.get(order.getOrderID());
                order.setBillOfMaterials(new BillOfMaterials(materials != null ? materials : new ArrayList<>()));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error fetching orders and materials", e);
        }

        return orders;
    }
  
   public static int registerOrder(int userID, int width, int length, int height, double totalPrice, int status, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (user_id, carport_width, carport_length, carport_height, date, total_price, status)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userID);
            stmt.setInt(2, width);
            stmt.setInt(3, length);
            stmt.setInt(4, height);
            stmt.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            stmt.setDouble(6, totalPrice);
            stmt.setInt(7, status);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            } catch (SQLException e) {
                throw new DatabaseException("Database error registering carport query", e);
            }
        return 0;

    }

    public static void updateOrder(int orderId, int width, int length, int height, int status, double totalPrice, List<CompleteUnitMaterial> materials, ConnectionPool connectionPool) throws DatabaseException {
        String updateOrderSql = "UPDATE orders " +
                "SET carport_width = ?, carport_length = ?, carport_height = ?, status = ?, total_price = ? " +
                "WHERE order_id = ?";

        String deleteMaterialsSql = "DELETE FROM complete_unit_material WHERE orders_id = ?";

        try (Connection connection = connectionPool.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // Update the order details
                try (PreparedStatement ps = connection.prepareStatement(updateOrderSql)) {
                    ps.setInt(1, width);
                    ps.setInt(2, length);
                    ps.setInt(3, height);
                    ps.setInt(4, status);
                    ps.setDouble(5, totalPrice);
                    ps.setInt(6, orderId);
                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected == 0) {
                        throw new DatabaseException("Order " + orderId + " not found.");
                    }
                }

                // Delete existing materials
                try (PreparedStatement psDelete = connection.prepareStatement(deleteMaterialsSql)) {
                    psDelete.setInt(1, orderId);
                    psDelete.executeUpdate();
                }

                // Insert new materials
                for (CompleteUnitMaterial material : materials) {
                    int descriptionId = CompleteUnitMaterialMapper.getDescriptionId(material.getDescription(), connectionPool);
                    int lengthId = MaterialMapper.getLengthID(material.getMaterial().getMaterialID(), material.getMaterial().getLength(), connectionPool);
                    CompleteUnitMaterialMapper.registerCUMToOrder(material.getQuantity(), orderId, lengthId, descriptionId, connectionPool);
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new DatabaseException("Error updating order: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database connection error: " + e.getMessage());
        }
    }

          public static List<Order> getAllOrdersByUserId(int userID, ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    int userId = rs.getInt("user_id");
                    int carportWidth = rs.getInt("carport_width");
                    int carportLength = rs.getInt("carport_length");
                    int carportHeight = rs.getInt("carport_height");
                    LocalDate date = rs.getDate("date").toLocalDate();
                    double totalPrice = rs.getDouble("total_price");
                    int status = rs.getInt("status");

                    orders.add(new Order(orderId, userId, new Carport(carportWidth, carportLength, carportHeight), date, totalPrice, status));
                }
                return orders;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Database error fetching orders", e);
        }
    }

    public static void updateStatus(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, 3);
            ps.setInt(2, orderId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Database error updating status", e);

        }
    }
}