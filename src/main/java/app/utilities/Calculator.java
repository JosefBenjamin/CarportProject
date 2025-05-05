package app.utilities;

import app.entities.CompleteUnitMaterial;
import app.entities.Order;
import app.persistence.ConnectionPool;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    private static final int POST = 1;
    private static final int RAFTER = 2;
    private static final int BEAMS = 2;

    private List<CompleteUnitMaterial> orderMaterials = new ArrayList<CompleteUnitMaterial>();
    private int width;
    private int length;
    private ConnectionPool connectionPool;

    public Calculator(int width, int length, ConnectionPool connectionPool) {
        this.width = width;
        this.length = length;
        this.connectionPool = connectionPool;
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
