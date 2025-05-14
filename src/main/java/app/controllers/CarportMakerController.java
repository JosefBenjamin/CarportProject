package app.controllers;

import app.Main;
import app.entities.Carport;
import app.entities.CompleteUnitMaterial;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.CompleteUnitMaterialMapper;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.utilities.Calculator;
import app.utilities.security.UserRole;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;
import java.util.logging.Logger;

public class CarportMakerController {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * @param app line 1: Clears the two season attributes (loginRequired, orderSend)
     *            If user reloads the page the messages that are linked to those
     *            attributes won't show again.
     */

    public static void routes(Javalin app, ConnectionPool connectionPool) {
        app.get("/carport-form", ctx -> {
            Boolean loginRequired = ctx.sessionAttribute("loginRequired");
            Boolean orderSend = ctx.sessionAttribute("orderSend");

            ctx.attribute("loginRequired", loginRequired);
            ctx.attribute("orderSend", orderSend);

            ctx.sessionAttribute("loginRequired", null); // clear login message
            ctx.sessionAttribute("orderSend", null);     // clear order flag

            ctx.render("carportmaker.html");
        }, UserRole.VISITOR);

        app.post("/sendOrder", ctx -> sendAndSaveCarportQuery(ctx, connectionPool), UserRole.LOGGED_IN);
        app.post("/calculatePrice", ctx -> calculateCarport(ctx, connectionPool), UserRole.VISITOR);

    }

    private static void calculateCarport(Context ctx, ConnectionPool connectionPool)  {
        int carportLength = Integer.parseInt(ctx.formParam("length"));
        int carportWidth = Integer.parseInt(ctx.formParam("width"));

        Carport carport = new Carport(carportWidth, carportLength);

        Calculator calculator = new Calculator(carport.getCarportWidth(), carport.getCarportLength(), carport.getCarportHeight(), connectionPool);

        double totalPrice = calculator.getTotalPrice();
        ctx.attribute("message",  "Pris for selvlavet carport: " + totalPrice + " kr.");
        ctx.render("carportmaker.html");


    }


    private static void sendAndSaveCarportQuery(Context ctx, ConnectionPool connectionPool) {
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.sessionAttribute("loginRequired", true);
            ctx.redirect("/carport-form");
            return;
        }
        int carportLength = Integer.parseInt(ctx.formParam("length"));
        int carportWidth = Integer.parseInt(ctx.formParam("width"));

        Carport carport = new Carport(carportWidth, carportLength);

        User user = ctx.sessionAttribute("currentUser");

        Calculator calculator = new Calculator(carport.getCarportWidth(), carport.getCarportLength(), carport.getCarportHeight(), connectionPool);
        int newOrderID;
        try {
            newOrderID = OrderMapper.registerOrder(user.getUserID(), carport.getCarportWidth(), carport.getCarportLength(), carport.getCarportHeight(),
                    calculator.getTotalPrice(), 1, connectionPool);
        } catch (DatabaseException e) {
            ctx.attribute("message", "Noget gik galt i at gemme din forespørgsel");
            return;
        }

        List<CompleteUnitMaterial> billOfMaterials = calculator.getOrderMaterials();
        for(CompleteUnitMaterial material: billOfMaterials) {
            try {
                CompleteUnitMaterialMapper.registerCUMToOrder(material.getQuantity(), newOrderID, material.getMaterial().getLengthID(connectionPool),
                        material.getDescriptionId(connectionPool), connectionPool);
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }
        }

        ctx.attribute("orderSend", true);
        ctx.render("carportmaker.html");

    }


}
