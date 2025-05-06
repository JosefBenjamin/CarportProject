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

    public static List<Order> getAllOrdersWithDetails(ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orders = new ArrayList<>();
        Map<Integer, List<CompleteUnitMaterial>> orderMaterialsMap = new HashMap<>();

        String orderSql = "SELECT order_id, user_id, carport_width, carport_length, carport_height, date, total_price, status FROM public.orders";

        String materialSql = """
                SELECT cum.cum_id, cum.quantity, cum.orders_id, cum.ml_id, cum.ms_description_id
                FROM public.complete_unit_material cum
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
                        int mlId = materialRs.getInt("ml_id");
                        int msdId = materialRs.getInt("ms_description_id");

                        CompleteUnitMaterial cum = new CompleteUnitMaterial(cumId, quantity, orderId, mlId, msdId);
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
