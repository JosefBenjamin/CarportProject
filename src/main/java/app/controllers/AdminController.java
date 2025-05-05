package app.controllers;

import app.entities.User;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminController {

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(
            "postgres", "postgres", "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "fog_carport_2025"
    );

    private static final UserMapper userMapper = new UserMapper(connectionPool);

    public static void Routes(Javalin app){
        app.get("/admin", AdminController::showAdminPage);

    }

    public static void showAdminPage(Context ctx){
        User adminUser = ctx.sessionAttribute("user");
        if(adminUser == null || !adminUser.isAdmin()){
            ctx.redirect("index.html");
            return;
        }

        ctx.render("admin.html");
    }

}
