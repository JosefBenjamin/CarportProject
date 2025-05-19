package app.controllers;

import app.entities.CompleteUnitMaterial;
import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.CompleteUnitMaterialMapper;
import app.persistence.ConnectionPool;
import app.utilities.CarportSvgSide;
import app.utilities.CarportSvgTop;
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


            CarportSvgTop carportSvgTop = new CarportSvgTop(600, 780);
            carportSvgTop.addBeams();
            carportSvgTop.addRafters();
            ctx.attribute("svgTop", carportSvgTop.toString());

            CarportSvgSide carportSvgSide = new CarportSvgSide(600, 780);
            carportSvgSide.addRafters();
            carportSvgSide.addPosts();
            ctx.attribute("svgSide", carportSvgSide.toString());


        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }

    }


}
