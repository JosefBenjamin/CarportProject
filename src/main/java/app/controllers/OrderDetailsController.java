package app.controllers;

import app.entities.CompleteUnitMaterial;
import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.CompleteUnitMaterialMapper;
import app.persistence.ConnectionPool;
import app.utilities.MailSender;
import app.utilities.Svg;
import io.javalin.Javalin;
import io.javalin.http.Context;


import java.util.List;

public class OrderDetailsController {
    public static void routes(Javalin app, ConnectionPool connectionPool) {
        app.get("/orderdetails", ctx -> {
            fetchBillOfMaterials(ctx, connectionPool);
            ctx.render("orderdetails.html");
        });

    }

    private static void fetchBillOfMaterials(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");

        int orderId = Integer.parseInt(ctx.queryParam("orderId"));

        try {
            List<CompleteUnitMaterial> billOfMaterial = CompleteUnitMaterialMapper.getCompleteUnitMaterialsByOrderId(orderId, connectionPool);
            ctx.attribute("billOfMaterial", billOfMaterial);
            Svg carportSvg = new Svg(0, 0, "0 0 855 690", "50%");
            carportSvg.addRectangle(0,0,600, 780, "stroke-width:1px; stroke:#000000; fill: #ffffff");
            ctx.attribute("svg", carportSvg.toString());

        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }

    }


}
