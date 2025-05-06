package app.controllers;

import app.entities.Order;
import app.entities.User;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class AdminController {


    public static void routes(Javalin app, ConnectionPool connectionPool){
        app.get("/admin", ctx -> showAdminPage(ctx, connectionPool));

    }

    public static void showAdminPage(Context ctx, ConnectionPool connectionPool){
        User adminUser = ctx.sessionAttribute("currentUser");
        if(adminUser == null || !adminUser.isAdmin()){
            ctx.redirect("index.html");
            return;
        }
        try {
            // Fetch all orders with their carport details and materials
            List<Order> orders = OrderMapper.getAllOrdersWithDetails(connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("admin.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", "Error fetching orders: " + e.getMessage());
            ctx.render("admin.html");
        }
    }

}
