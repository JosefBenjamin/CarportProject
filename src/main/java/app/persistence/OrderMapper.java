package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper {

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

}
