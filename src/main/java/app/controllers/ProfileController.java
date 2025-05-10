package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public class ProfileController {
    public static void routes(Javalin app, ConnectionPool connectionPool) {
        app.get("/userprofile", ctx -> ctx.render("userprofile.html"));
        app.post("updateMail", ctx -> updateMail(ctx, connectionPool));
        app.post("updatePassword", ctx -> updatePassword(ctx, connectionPool));
        app.post("updateTlf", ctx -> updateTlf(ctx, connectionPool));
        app.post("updateAddress", ctx -> updateAddress(ctx, connectionPool));
        app.post("updateCityAndZip", ctx -> updateCityAndZipCode(ctx, connectionPool));
    }

    private static void updateCityAndZipCode(Context ctx, ConnectionPool connectionPool) {
        String city = ctx.formParam("city");
        int zipCode = Integer.parseInt(ctx.formParam("zipCode"));
        User user = ctx.sessionAttribute("currentUser");

        if(user != null) {
            try {
                UserMapper.updateCityAndZipCode(address, user.getUserID(), connectionPool);

                user.getZipCode().setZipCode(zipCode);
                user.getZipCode().setCity(city);

                ctx.sessionAttribute("currentUser", user);

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
        User user = ctx.sessionAttribute("currentUser");

        if(user != null) {
            try {
                UserMapper.updatePassword(password, user.getUserID(), connectionPool);

                user.setPassword(password);
                ctx.sessionAttribute("currentUser", user);

                ctx.attribute("message", "password er ændret");
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
