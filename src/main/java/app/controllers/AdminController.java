package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.MaterialMapper;
import app.persistence.UserMapper;
import app.utilities.Calculator;
import app.utilities.MailSender;
import app.utilities.Role;
import app.utilities.StatusChecker;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AdminController {

    public static void routes(Javalin app, ConnectionPool connectionPool) {
        app.get("/admin", ctx -> showAdminPage(ctx, connectionPool), Role.ADMIN);
        app.get("/admin/show-materials", ctx -> showMaterials(ctx, connectionPool), Role.ADMIN);
        app.post("/admin/show-materials", ctx -> showMaterials(ctx, connectionPool), Role.ADMIN);
        app.post("/admin/show-orders", ctx -> showOrders(ctx, connectionPool), Role.ADMIN);
        app.post("/admin/add-material", ctx -> addMaterial(ctx, connectionPool), Role.ADMIN);
        app.post("/admin/delete-material", ctx -> deleteMaterial(ctx, connectionPool), Role.ADMIN);
        app.post("/admin/update-order", ctx -> updateOrder(ctx, connectionPool), Role.ADMIN);
    }

    public static void showAdminPage(Context ctx, ConnectionPool connectionPool) {
        User adminUser = ctx.sessionAttribute("currentUser");

        /*
        if (adminUser == null || !adminUser.isAdmin()) {
            ctx.redirect("index.html");
            return;
        }
        */

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
            ctx.attribute("StatusChecker", new StatusChecker());
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
            double meterPrice = Double.parseDouble(ctx.formParam("meter_price"));
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

    public static void updateOrder(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.formParam("orderId"));
            int carportWidth = Integer.parseInt(ctx.formParam("carportWidth"));
            int carportLength = Integer.parseInt(ctx.formParam("carportLength"));
            int carportHeight = Integer.parseInt(ctx.formParam("carportHeight"));
            int status = Integer.parseInt(ctx.formParam("status"));

            // Recalculate price and materials using Calculator
            Calculator calculator = new Calculator(carportWidth, carportLength, carportHeight, connectionPool);
            double newPrice = calculator.getTotalPrice();
            List<CompleteUnitMaterial> newMaterials = calculator.getOrderMaterials();

            // Update the order in the database
            OrderMapper.updateOrder(orderId, carportWidth, carportLength, carportHeight, status, newPrice, newMaterials, connectionPool);

            if(status == 3) {
                MailSender mailSender = new MailSender();
                Order order = OrderMapper.getOrderById(orderId, connectionPool);
                StringBuilder sb = new StringBuilder();
                for (CompleteUnitMaterial material : newMaterials) {
                    String matName = "Ukendt materiale";
                    if (material.getMaterial() != null && material.getMaterial().getName() != null) {
                        matName = material.getMaterial().getName();
                    }else {
                        matName = "Ukendt materiale";
                    }
                    String description;
                    if (material.getDescription() != null) {
                        description = material.getDescription();
                    } else {
                        description = "Ukendt beskrivelse";
                    }
                    sb.append("Materiale: ")
                            .append(matName)
                            .append(", Antal: ")
                            .append(material.getQuantity())
                            .append(", Beskrivelse: ")
                            .append(description);
                }
                String formattedMaterials = sb.toString();
                String currentDate = order.getDate().toString();
                User user = UserMapper.getUserById(order.getUserID(), connectionPool);
                String mailRecipient = user.getEmail();
                String heightLengthWidth = String.valueOf(carportHeight) + " X " + String.valueOf(carportLength) + " X " + String.valueOf(carportWidth);
                try {
                    System.out.println("About to send mail to: " + mailRecipient);
                    boolean success = mailSender.sendCUM(String.valueOf(orderId), heightLengthWidth, currentDate,
                            String.valueOf(newPrice), String.valueOf(formattedMaterials), mailRecipient);
                    if(success) {
                        System.out.println("Email succesfully sent to " + mailRecipient);
                    } else {
                        System.out.println("Failed to send mail");
                    }
                } catch (IOException e) {
                    throw new DatabaseException("Something went wrong sending an order confirmation mail to user");
                }
            }

            ctx.attribute("successMessage", "Order updated successfully");
            showOrders(ctx, connectionPool);
        } catch (NumberFormatException e) {
            ctx.attribute("error", "Invalid input format for dimensions, status, or order ID.");
            showOrders(ctx, connectionPool);
        } catch (DatabaseException e) {
            ctx.attribute("error", "Database error, order not updated: " + e.getMessage());
            showOrders(ctx, connectionPool);
        }
    }
}