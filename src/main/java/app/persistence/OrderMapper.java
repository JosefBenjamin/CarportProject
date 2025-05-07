package app.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper {

    public static void registerOrder(int userID, int width, int length, int height, double totalPrice, int status, ConnectionPool connectionPool) {
        String sql = "INSERT INTO orders (user_id, carport_width, carport_length, carport_height, date, total_price, status)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            stmt.setInt(2, width);
            stmt.setInt(3, length);
            stmt.setInt(4, height);
            stmt.setDate(5, new java.sql.Date(new java.util.Date().getTime()));
            stmt.setDouble(6, totalPrice);
            stmt.setInt(7, status);

            stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public static int getOrderID(int userID, ConnectionPool connectionPool) {
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_id DESC LIMIT 1";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, (userID));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("order_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
