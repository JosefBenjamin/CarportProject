package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.entities.ZipCode;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import app.persistence.ZipCodeMapper;
import app.utilities.StatusChecker;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.utilities.security.UserRole;
import java.util.List;

public class ProfileController {
    public static void routes(Javalin app, ConnectionPool connectionPool) {
        app.get("/userprofile", ctx -> {
            fetchOrders(ctx, connectionPool);
            ctx.render("userprofile.html");
        }, UserRole.LOGGED_IN);
        app.post("updateMail", ctx -> updateMail(ctx, connectionPool), UserRole.LOGGED_IN);
        app.post("updatePassword", ctx -> updatePassword(ctx, connectionPool), UserRole.LOGGED_IN);
        app.post("updateTlf", ctx -> updateTlf(ctx, connectionPool), UserRole.LOGGED_IN);
        app.post("updateAddress", ctx -> updateAddress(ctx, connectionPool), UserRole.LOGGED_IN);
        app.post("updateCityAndZip", ctx -> updateCityAndZipCode(ctx, connectionPool), UserRole.LOGGED_IN);
        app.post("/orderconfirmation", ctx -> handlePayment(ctx, connectionPool), UserRole.LOGGED_IN);
    }

    private static void handlePayment(Context ctx, ConnectionPool connectionPool) {
        int orderId = Integer.parseInt(ctx.formParam("orderId"));


        try {
            OrderMapper.updateStatus(orderId, connectionPool);
            ctx.render("orderconfirmation.html");

        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");

        }


    }

    private static void fetchOrders(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");


        try {
            List<Order> userOrders = OrderMapper.getAllOrdersByUserId(user.getUserID(), connectionPool);
            ctx.attribute("StatusChecker", new StatusChecker());
            ctx.attribute("userOrders", userOrders);
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }
    }

    private static void updateCityAndZipCode(Context ctx, ConnectionPool connectionPool) {
        String city = ctx.formParam("city");
        city = city.trim();

        int zipInt = Integer.parseInt(ctx.formParam("zipcode"));
        ZipCode zipCode = null;

        User user = ctx.sessionAttribute("currentUser");

        try {
            Integer matchedZip = ZipCodeMapper.getZipByCity(city, connectionPool);
            String matchedCity = ZipCodeMapper.getCityByZip(zipInt, connectionPool);

            // zip exists but with different city
            if (matchedCity != null && !matchedCity.trim().equalsIgnoreCase(city)) {
                ctx.attribute("message", "Postnummeret findes allerede med en anden by.");
                ctx.render("userprofile.html");
                return;
            }
            if (matchedZip != null && matchedZip != zipInt) {
                ctx.attribute("message", "Byen findes allerede med et andet postnummer.");
                ctx.render("userprofile.html");
                return;
            }
            zipCode = new ZipCode(zipInt, city);
            if (!ZipCodeMapper.zipChecker(zipCode, connectionPool)) {
                ZipCodeMapper.registerZipCode(zipCode, connectionPool);
            }
        } catch (DatabaseException e) {
            ctx.attribute("message", "Fejl ved behandling af postnummer: " + e.getMessage());
            ctx.render("userprofile.html");
        }

        if(user != null) {
            try {
                UserMapper.updateCityAndZipCode(zipInt, user.getUserID(), connectionPool);

                user.setZipCode(zipCode);


                ctx.attribute("message", "By og postnummer ændret");
                ctx.render("userprofile.html");


            } catch (DatabaseException e) {
                ctx.attribute("message", e.getMessage());
                ctx.render("index.html");
            }
        }
    }


    private static void updateAddress(Context ctx, ConnectionPool connectionPool) {
        String address = ctx.formParam("address");
        User user = ctx.sessionAttribute("currentUser");

        if(user != null) {
            try {
                UserMapper.updateAddress(address, user.getUserID(), connectionPool);

                user.setAddress(address);
                ctx.sessionAttribute("currentUser", user);

                ctx.attribute("message", "Adresse ændret");
                ctx.render("userprofile.html");


            } catch (DatabaseException e) {
                ctx.attribute("message", e.getMessage());
                ctx.render("index.html");
            }
        }
    }

    private static void updateTlf(Context ctx, ConnectionPool connectionPool) {
        int tlf = Integer.parseInt(ctx.formParam("tlf"));
        User user = ctx.sessionAttribute("currentUser");

        if(user != null) {
            try {
                UserMapper.updateTlf(tlf, user.getUserID(), connectionPool);

                user.setTlf(tlf);
                ctx.sessionAttribute("currentUser", user);

                ctx.attribute("message", "Tlf nummer er ændret");
                ctx.render("userprofile.html");


            } catch (DatabaseException e) {
                ctx.attribute("message", e.getMessage());
                ctx.render("index.html");
            }
        }
    }

    private static void updatePassword(Context ctx, ConnectionPool connectionPool)  {
        String password = ctx.formParam("password");
        String passwordConfirm = ctx.formParam("confirmPassword");
        User user = ctx.sessionAttribute("currentUser");

        if (!password.equals(passwordConfirm)) {
            ctx.attribute("message", "Password mismatch. Skriv det samme to gange");
            ctx.render("userprofile.html");
            return;
        }

        if(user != null) {
            try {
                UserMapper.updatePassword(password, user.getUserID(), connectionPool);

                user.setPassword(password);
                ctx.sessionAttribute("currentUser", user);

                ctx.attribute("message", "Password er ændret");
                ctx.render("userprofile.html");


            } catch (DatabaseException e) {
                ctx.attribute("message", e.getMessage());
                ctx.render("index.html");
            }
        }
    }

    private static void updateMail(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("mail");
        User user = ctx.sessionAttribute("currentUser");

        if (user != null) {
            try {
                if (!UserMapper.emailExist(email, connectionPool)) {
                    ctx.attribute("message", "Emailen findes allerede");
                    ctx.render("userprofile.html");
                } else {
                    UserMapper.updateMail(email, user.getUserID(), connectionPool);

                    user.setEmail(email);
                    ctx.sessionAttribute("currentUser", user);

                    ctx.attribute("message", "Mail er ændret");
                    ctx.render("userprofile.html");
                }
            } catch (DatabaseException e) {
                ctx.attribute("message", e.getMessage());
                ctx.render("index.html");
            }

        }
    }


}
