package app.utilities;

import app.entities.CompleteUnitMaterial;
import app.entities.Material;
import app.entities.Order;
import app.persistence.ConnectionPool;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    private ConnectionPool connectionPool;

    // Carport dimensions
    private final int width;
    private final int length;
    private final int height;

    // Stolper
    /*private final Material post;
    private final int amountOfPosts;

    // Remme
    private final Material beams;
    private final int amountOfBeams;

    // Spær
    private final Material rafter;
    private final int amountOfRafters;

    // Tag
    private final Material roof;
    private final int amountOfRoofs;

    private final float totalPrice;
    */

    private List<CompleteUnitMaterial> orderMaterials = new ArrayList<CompleteUnitMaterial>();




    public Calculator(int width, int length, int height, ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;

        this.width = width;
        this.length = length;
        this.height = height;



    }

    public void calculateCarport(Order order) {
        calculatePost();
        calculateBeams();
        calculateRafters();
    }

    // Stolper
    private void calculatePost() {
        // Antal stolper
        int quantity = 6;

        // Længde på stolper

    }

    // Remme
    private void calculateBeams() {

    }

    // Spær
    private void calculateRafters() {

    }

    public List<CompleteUnitMaterial> getOrderMaterials() {
        return orderMaterials;
    }
}
