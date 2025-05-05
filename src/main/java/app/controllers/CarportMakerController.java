package app.controllers;

import app.Main;
import app.entities.Carport;
import app.persistence.ConnectionPool;
import app.utilities.Calculator;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class CarportMakerController {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void routes(Javalin app, ConnectionPool connectionPool) {
        app.get("/carport-form", ctx -> {
            Boolean loginRequired = ctx.sessionAttribute("loginRequired");
            ctx.attribute("loginRequired", loginRequired);
            ctx.sessionAttribute("loginRequired", null); // clears the session attribute after reloading page

            ctx.render("carportmaker.html");
        });
        app.post("/sendOrder", ctx -> sendAndSaveCarportQuery(ctx, connectionPool));
        app.post("/calculatePrice", ctx -> calculateCarport(ctx, connectionPool));

    }

    private static void calculateCarport(Context ctx, ConnectionPool connectionPool) {
        int carportLength = Integer.parseInt(ctx.formParam("length"));
        int carportWidth = Integer.parseInt(ctx.formParam("width"));

        Carport carport = new Carport(carportLength, carportWidth);

        Calculator calculator = new Calculator(carport.getCarportWidth(), carport.getCarportLength(), carport.getCarportHeight(), connectionPool);

    }

    private static void sendAndSaveCarportQuery(Context ctx, ConnectionPool connectionPool) {
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.sessionAttribute("loginRequired", true);
            ctx.redirect("/carport-form");
            return;
        }

        ctx.attribute("message", "Din forespørgsel er sendt");
        ctx.render("carportmaker.html");

    }


}
