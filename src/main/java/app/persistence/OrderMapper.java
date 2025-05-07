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
}