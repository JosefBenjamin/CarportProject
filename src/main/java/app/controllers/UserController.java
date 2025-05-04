package app.controllers;

import app.Main;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import app.utilities.PasswordUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.logging.Logger;

public class UserController {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void routes(Javalin app, ConnectionPool connectionPool) {
        app.get("/register", ctx -> ctx.render("register.html")); // show register page
        app.post("/register", ctx -> register(ctx, connectionPool)); // handle form submission
        app.get("/login", ctx -> ctx.render("login.html"));  // show login page
        app.post("/login", ctx -> login(ctx, connectionPool));   // handle login request

    }


    public static void register(Context ctx, ConnectionPool connectionPool) {

            String email = ctx.formParam("email");
            String password1 = ctx.formParam("regpassword1");
            String password2= ctx.formParam("regpassword2");
            String tlf = ctx.formParam("phonenumber");
            String address = ctx.formParam("address");



        if (email == null || email.isEmpty() || !email.contains("@")) {
            ctx.sessionAttribute("savedEmail", email);
            ctx.sessionAttribute("savedPhone", tlf);
            ctx.sessionAttribute("savedAddress", address);
            ctx.attribute("message", "Ugyldig email addresse");
            ctx.render("register.html"); // Re-render form with error
            return;
        }

        if (password1 == null || password2.length() < 8 || password2 == null || password2.length() < 8) {
            ctx.sessionAttribute("savedEmail", email);
            ctx.sessionAttribute("savedPhone", tlf);
            ctx.sessionAttribute("savedAddress", address);
            ctx.attribute("message", "Kodeord skal være mindst 8 tegn");
            ctx.render("register.html");
            return;
        }

        if ((!password1.equals(password2))) {
            // Set message attribute for Thymeleaf
            ctx.sessionAttribute("savedEmail", email);
            ctx.sessionAttribute("savedPhone", tlf);
            ctx.sessionAttribute("savedAddress", address);
            ctx.attribute("message", "Kodeordene matcher ikke");
            ctx.render("register.html"); // Re-render the same page with the error message
            return; // Stop processing
        }

        try {
            if (!UserMapper.emailExist(email, connectionPool)) {
                ctx.sessionAttribute("savedEmail", email);
                ctx.sessionAttribute("savedPhone", tlf);
                ctx.sessionAttribute("savedAddress", address);
                ctx.attribute("message", "Emailen findes allerede");
                ctx.render("register.html");
                return;
            }
        } catch (DatabaseException e) {
            ctx.sessionAttribute("savedEmail", email);
            ctx.sessionAttribute("savedPhone", tlf);
            ctx.sessionAttribute("savedAddress", address);
            ctx.attribute("message", "Fejl ved kontrol af email: " + e.getMessage());
            ctx.render("register.html");
            return;
        }


        int phone;
        try {
            phone = Integer.parseInt(tlf);
        } catch (NumberFormatException e) {
            ctx.sessionAttribute("savedEmail", email);
            ctx.sessionAttribute("savedPhone", tlf);
            ctx.sessionAttribute("savedAddress", address);
            ctx.attribute("message", "Ugyldigt telefonnummer");
            ctx.render("register.html"); // Re-render form with error
            return;
        }

        User user = new User(email, password1, phone, false, address);
        try {
            User registeredUser = UserMapper.register(user, connectionPool);
            ctx.sessionAttribute("savedEmail", null);
            ctx.sessionAttribute("savedPhone", null);
            ctx.sessionAttribute("savedAddress", null);
            ctx.redirect("/login");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Noget gik galt under oprettelsen: " + e.getCause());
            ctx.render("register.html");
        }


    }


    public static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        if (email == null || email.isEmpty() || !email.contains("@")) {
            ctx.attribute("message", "Ugyldig email addresse");
            ctx.render("login.html");
            return;
        }

        if (password == null || password.length() < 8) {
            ctx.attribute("message", "Kodeord skal være mindst 8 tegn");
            ctx.render("login.html");
            return;
        }

        try {
            String hashedPassword = UserMapper.getHashedPasswordByEmail(email, connectionPool);
            if (hashedPassword == null || !PasswordUtil.checkPassword(password, hashedPassword)) {
                ctx.attribute("message", "Forkert email eller kodeord");
                ctx.render("login.html");
                return;
            }

            User user = UserMapper.getUserByEmail(email, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.redirect("/");

        } catch (DatabaseException e) {
            ctx.attribute("message", "Fejl under login: " + e.getMessage());
            ctx.render("login.html");
        }
    }



}
