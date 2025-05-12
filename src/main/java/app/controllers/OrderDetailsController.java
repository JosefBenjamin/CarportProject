package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public class OrderDetailsController {
    public static void routes(Javalin app, ConnectionPool connectionPool) {
        app.get("/orderdetails", ctx -> {
            fetchBillOfMaterials(ctx, connectionPool);
            ctx.render("orderdetails.html");
        });

    }

    private static void fetchBillOfMaterials(Context ctx, ConnectionPool connectionPool) {

    }


}
