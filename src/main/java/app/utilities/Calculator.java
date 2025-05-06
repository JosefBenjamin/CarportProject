package app.utilities;

import app.entities.CompleteUnitMaterial;
import app.entities.Material;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    private ConnectionPool connectionPool;
    private MaterialMapper materialMapper;

    private static final int POSTS = 6;
    private static final int BEAMS = 5;
    private static final int RAFTERS = 5;

    // Carport dimensions
    private final int width;
    private final int length;
    private final int height;

    // Stolper
    private final Material post;
    //private final int amountOfPosts;

    /*
    // Remme
    private final Material beams;
    private final int amountOfBeams;

    // Spær
    private final Material rafter;
    private final int amountOfRafters;

    // Stern
    private final Material stern
    private final int amount of sterns

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
        this.post = getMaterialByID(POSTS);


        calculatePost();



    }

    private Material getMaterialByID(int materialID) {
        try {
             return materialMapper.getMaterialByID(materialID, connectionPool);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Stolper
    private void calculatePost() {
        // Antal stolper
        int quantity = calculatePostAmount();

        // Længde på stolper
    }

    public int calculatePostAmount() {
        return 2 * (2 + (length - 130) / 340);
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
