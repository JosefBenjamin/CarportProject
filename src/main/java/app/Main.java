package app;


import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.*;
import app.entities.User;
import app.persistence.ConnectionPool;
import app.utilities.MailSender;
import app.utilities.Role;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "cupcake";

    public static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);


    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        /*
            Gives each visitor a unique ID to track them across the site
            This happens before any http requests are made
         */
        app.before(ctx -> {
            if(ctx.sessionAttribute("currentVisitor") == null) {
                ctx.sessionAttribute("currentVisitor", "guest-" + UUID.randomUUID());
            }
            ctx.attribute("session", ctx.sessionAttributeMap());
        });

        // Access management for admin routes
        app.beforeMatched(ctx -> {
            if (ctx.path().startsWith("/admin")) {
                User user = ctx.sessionAttribute("currentUser");
                if (user == null) {
                    ctx.sessionAttribute("currentUser", null); // Clear any stale session
                    ctx.redirect("/");
                    return;
                }
                if (user.getRole() != Role.ADMIN) {
                    ctx.redirect("/");
                    return;
                }
            }
        });

        app.get("/", ctx -> {
            System.out.println("Visitor ID: " + ctx.sessionAttribute("currentVisitor"));
            ctx.render("index.html");
        });

        UserController.routes(app, connectionPool);
        AdminController.routes(app, connectionPool);
        CarportMakerController.routes(app, connectionPool);
        ProfileController.routes(app, connectionPool);
        OrderDetailsController.routes(app, connectionPool);


    }

}