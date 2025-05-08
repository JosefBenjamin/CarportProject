package app.controllers;

import app.entities.User;
import app.entities.ZipCode;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import app.persistence.ZipCodeMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.logging.Logger;

public class UserController {

    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    public static void routes(Javalin app, ConnectionPool connectionPool) {
        app.get("/register", ctx -> ctx.render("register.html")); // show register page
        app.post("/register", ctx -> register(ctx, connectionPool)); // handle form submission
        app.get("/login", ctx -> ctx.render("login.html"));  // show login page
        app.post("/login", ctx -> login(ctx, connectionPool));   // handle login request
        app.get("/logout", ctx -> logout(ctx, connectionPool));
    }


    public static void saveAttributes(Context ctx, String email, String phone, String address, String zip, String city) {
        ctx.sessionAttribute("savedEmail", email);
        ctx.sessionAttribute("savedPhone", phone);
        ctx.sessionAttribute("savedAddress", address);
        ctx.sessionAttribute("savedZip", zip);
        ctx.sessionAttribute("savedCity", city);
    }


    public static void register(Context ctx, ConnectionPool connectionPool) {

        String email = ctx.formParam("email");
        String password1 = ctx.formParam("regpassword1");
        String password2 = ctx.formParam("regpassword2");
        String tlf = ctx.formParam("phonenumber");
        String address = ctx.formParam("address");
        String zip = ctx.formParam("zip");
        String city = ctx.formParam("city");


        if (email == null || email.isEmpty() || !email.contains("@")) {
            saveAttributes(ctx, email, tlf, address, zip, city);
            ctx.attribute("message", "Ugyldig email addresse");
            ctx.render("register.html"); // Re-render form with error
            return;
        }

        if (password1 == null || password2 == null || password1.length() < 8 || password2.length() < 8) {
            saveAttributes(ctx, email, tlf, address, zip, city);
            ctx.attribute("message", "Kodeord skal være mindst 8 tegn" );
            ctx.render("register.html");
            return;
        }

        if ((!password1.equals(password2))) {
            // Set message attribute for Thymeleaf
            saveAttributes(ctx, email, tlf, address, zip, city);
            ctx.attribute("message", "Kodeordene matcher ikke");
            ctx.render("register.html"); // Re-render the same page with the error message
            return; // Stop processing
        }

        try {
            if (!UserMapper.emailExist(email, connectionPool)) {
                saveAttributes(ctx, email, tlf, address, zip, city);
                ctx.attribute("message", "Emailen findes allerede");
                ctx.render("register.html");
                return;
            }
        } catch (DatabaseException e) {
            saveAttributes(ctx, email, tlf, address, zip, city);
            ctx.attribute("message", "Fejl ved kontrol af email: " + e.getMessage());
            ctx.render("register.html");
            return;
        }

        if (tlf == null || tlf.isBlank()) {
            saveAttributes(ctx, email, tlf, address, zip, city);
            ctx.attribute("message", "Telefonnummer skal udfyldes");
            ctx.render("register.html");
            return;
        }

        int phone;
        try {
            phone = Integer.parseInt(tlf);
        } catch (NumberFormatException e) {
            saveAttributes(ctx, email, tlf, address, zip, city);
            ctx.attribute("message", "Ugyldigt telefonnummer");
            ctx.render("register.html"); // Re-render form with error
            return;
        }

        if (zip == null || zip.isBlank()) {
            saveAttributes(ctx, email, tlf, address, zip, city);
            ctx.attribute("message", "Postnummer skal udfyldes");
            ctx.render("register.html");
            return;
        }

        int zipInt;
        try {
            zipInt = Integer.parseInt(zip);
        } catch (NumberFormatException e) {
            saveAttributes(ctx, email, tlf, address, zip, city);
            ctx.attribute("message", "Postnummer skal være et tal");
            ctx.render("register.html"); // Re-render form with error
            return;
        }

        ZipCode zipCode = new ZipCode(zipInt, city);
        try {
            Integer matchedZip = ZipCodeMapper.getZipByCity(city, connectionPool);
            String matchedCity = ZipCodeMapper.getCityByZip(zipInt, connectionPool);

            // zip exists but with different city
            if ((matchedCity != null) && (!matchedCity.equalsIgnoreCase(city))) {
                saveAttributes(ctx, email, tlf, address, zip, city);
                ctx.attribute("message", "Postnummeret findes allerede med en anden by.");
                ctx.render("register.html");
                return;
            }

            // city exists but with different zip
            if ((matchedZip != null) && (matchedZip != zipInt)) {
                saveAttributes(ctx, email, tlf, address, zip, city);
                ctx.attribute("message", "Byen findes allerede med et andet postnummer.");
                ctx.render("register.html");
                return;
            }

            // zip + city combo is new and consistent → insert
            if (!ZipCodeMapper.zipChecker(zipCode, connectionPool)) {
                ZipCodeMapper.registerZipCode(zipCode, connectionPool);
            }

        } catch (DatabaseException e) {
            saveAttributes(ctx, email, tlf, address, zip, city);
            ctx.attribute("message", "Fejl ved behandling af postnummer: " + e.getMessage());
            ctx.render("register.html");
            return;
        }

        User user = new User(email, password1, phone, false, address);
        user.setZipCode(zipCode);
        try {
            UserMapper.register(user, connectionPool);
            ctx.sessionAttribute("savedEmail", null);
            ctx.sessionAttribute("savedPhone", null);
            ctx.sessionAttribute("savedAddress", null);
            ctx.sessionAttribute("savedZip", null);
            ctx.sessionAttribute("savedCity", null);
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
            ctx.sessionAttribute("savedEmail", email);
            ctx.render("login.html");
            return;
        }

        try {
            boolean authenticated = UserMapper.login(email, password, connectionPool);
            if (!authenticated) {
                ctx.attribute("message", "Forkert email eller kodeord");
                ctx.sessionAttribute("savedEmail", email);
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

    public static void logout(Context ctx, ConnectionPool connectionPool) {
        ctx.sessionAttribute("currentUser", null);
        ctx.redirect("/");
    }

}
