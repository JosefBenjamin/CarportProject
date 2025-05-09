package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.entities.Material;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.MaterialMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AdminController {

    public static void routes(Javalin app, ConnectionPool connectionPool) {
        app.get("/admin", ctx -> showAdminPage(ctx, connectionPool));
        app.post("/admin/show-materials", ctx -> showMaterials(ctx, connectionPool));
        app.post("/admin/show-orders", ctx -> showOrders(ctx, connectionPool));
        app.post("/admin/add-material", ctx -> addMaterial(ctx, connectionPool));
        app.post("/admin/delete-material", ctx -> deleteMaterial(ctx, connectionPool));
    }

    public static void showAdminPage(Context ctx, ConnectionPool connectionPool) {
        User adminUser = ctx.sessionAttribute("currentUser");
        if (adminUser == null || !adminUser.isAdmin()) {
            ctx.redirect("index.html");
            return;
        }

        try {
            List<Order> orders = OrderMapper.getAllOrdersWithDetails(connectionPool);
            System.out.println("Fetched orders: " + orders);
            Map<Integer, String> descriptions;
            try {
                descriptions = OrderMapper.getDescriptions(connectionPool);
                System.out.println("Fetched descriptions: " + descriptions);
            } catch (DatabaseException e) {
                descriptions = Collections.emptyMap();
                ctx.attribute("error", "Could not fetch material descriptions: " + e.getMessage());
            }

            ctx.attribute("orders", orders);
            ctx.attribute("descriptions", descriptions);
            ctx.attribute("view", "orders");
            ctx.render("admin.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", "Error fetching orders: " + e.getMessage());
            ctx.render("admin.html");
        }
    }

    public static void showMaterials(Context ctx, ConnectionPool connectionPool) {
        try {
            List<Material> materials = MaterialMapper.getAllMaterials(connectionPool);
            System.out.println("Fetched materials: " + materials);
            ctx.attribute("materials", materials);
            ctx.attribute("view", "materials");
            ctx.render("admin.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", "Error fetching materials: " + e.getMessage());
            ctx.render("admin.html");
        }
    }

    public static void showOrders(Context ctx, ConnectionPool connectionPool) {
        showAdminPage(ctx, connectionPool);
    }

    public static void addMaterial(Context ctx, ConnectionPool connectionPool) {
        try {
            String name = ctx.formParam("name");
            String unitName = ctx.formParam("unit_name");
            double meterPrice = Double.parseDouble(ctx.formParam("meter_price")); // Reverted to meter_price
            String lengths = ctx.formParam("lengths");
            MaterialMapper.addMaterial(name, unitName, meterPrice, lengths, connectionPool);
            ctx.redirect("/admin/show-materials");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("error", "Error adding material: " + e.getMessage());
            ctx.attribute("view", "materials");
            ctx.render("admin.html");
        }
    }

    public static void deleteMaterial(Context ctx, ConnectionPool connectionPool) {
        try {
            int materialId = Integer.parseInt(ctx.formParam("material_id"));
            MaterialMapper.deleteMaterial(materialId, connectionPool);
            ctx.redirect("/admin/show-materials");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("error", "Error deleting material: " + e.getMessage());
            ctx.attribute("view", "materials");
            ctx.render("admin.html");
        }
    }
}