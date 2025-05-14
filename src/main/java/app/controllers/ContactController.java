package app.controllers;

import app.persistence.ConnectionPool;
import app.utilities.security.UserRole;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class ContactController {


    private static String relevantMail = "customcarport@protonmail.com";
    private static String relevantPhone = "Coming soon (+45) ...";
    private static String relevantAddress = "Firskovvej 18";


    public static void routes(Javalin app, ConnectionPool connectionPool) {
        app.get("/contact", ctx -> contactInfo(ctx, connectionPool), UserRole.LOGGED_IN, UserRole.ADMIN);
    }


    public static void orgInfo(Context ctx, String email, String phone, String address) {

        ctx.attribute("contactEmail", email);
        ctx.attribute("contactPhone", phone);
        ctx.attribute("contactAddress", address);
        ctx.render("/contact");


    }

    public static void contactInfo(Context ctx, ConnectionPool connectionPool) {

        orgInfo(ctx, relevantMail, relevantPhone, relevantAddress);

    }

}
