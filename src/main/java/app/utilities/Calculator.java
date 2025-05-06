package app.utilities;

import app.entities.CompleteUnitMaterial;
import app.entities.Material;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Remme
    private final List<Material> beams;
    /*


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

    private final double totalPrice;
    private final List<CompleteUnitMaterial> orderMaterials = new ArrayList<CompleteUnitMaterial>();


    public Calculator(int width, int length, int height, ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;

        this.width = width;
        this.length = length;
        this.height = height;

        this.post = getMaterialByID(POSTS).get(0);
        calculatePost();

        this.beams = getMaterialByID(BEAMS);
        calculateBeams();

        this.totalPrice = calculateTotalPrice();


    }


    /**
     *
     * @param materialID uses materialID (which is hardcoded in Calculators attributes)
     *                   to find the specific material.
     * @return returns list of that Material, which size is varied because the Materials
     * can have different sizes.
     */
    private List<Material> getMaterialByID(int materialID) {
        try {
             return materialMapper.getMaterialsByID(materialID, connectionPool);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Calculates the quantity fo posts needed.
     * Then adds the Complete unit Material to a list.
     * That list will represent our Bill of Materials
     */
    // Stolper
    private void calculatePost() {
        // Antal stolper
        int quantity = calculatePostAmount();

        orderMaterials.add(new CompleteUnitMaterial(quantity, "Stolper nedgraves 90 cm. i jord", post));

    }

    /**
     *
     * @return Returns the quantity of posts. The quantity is decided by this equation.
     */
    public int calculatePostAmount() {
        return 2 * (2 + (length - 130) / 340);
    }



    // Remme
    private void calculateBeams() {
        int remainingLength = this.length;
        List<Material> sortedBeams = new ArrayList<>(beams);
        sortedBeams.sort((a,b) -> Integer.compare(b.getLength(), a.getLength()));

        List<Material> selectedBeams = new ArrayList<>();

        while (remainingLength > 0) {
            boolean found = false;
            for (Material beam : sortedBeams) {
                if (beam.getLength() <= remainingLength) {
                    selectedBeams.add(beam);
                    remainingLength -= beam.getLength();
                    found = true;
                    break;
                }
            }

            if (!found) {
                Material smallest = sortedBeams.get(sortedBeams.size() - 1);
                selectedBeams.add(smallest);
                remainingLength -= smallest.getLength();
            }
        }

        Map<Integer, CompleteUnitMaterial> grouped = new HashMap<>();
        for (Material m : selectedBeams) {
            grouped.compute(m.getLength(), (len, cum) -> {
                if (cum == null) {
                    return new CompleteUnitMaterial(2, "Remme i sider, sadles ned i stopler", m);
                } else {
                    cum.setQuantity(cum.getQuantity() + 2);
                    return cum;
                }
            });
        }

        orderMaterials.addAll(grouped.values());

    }

    // Spær
    private void calculateRafters() {

    }

    public List<CompleteUnitMaterial> getOrderMaterials() {
        return orderMaterials;
    }

    /**
     * totalPrice equation:
     * Material length / 100 <- divides by 100 to get the length(cm) in meters
     *
     * @return returns the total price accumulated from the orderMaterials (BillOfMaterials) list.
     *
     * Return statement used to get the totalPrice in only two decimals:
     * totalPrice * 100 shifts the decimal point two places to the right.
     * Math.round rounds totalPrice * 100 to the nearest integer.
     * This divided by 100 shifts the decimal to its original place, but now rounded to two decimal digits.
     */
    private double calculateTotalPrice() {
        double totalPrice = 0;
        for (CompleteUnitMaterial material : orderMaterials) {
            totalPrice += (material.getMaterial().getMeterPrice() * ((double) material.getMaterial().getLength() / 100)) * material.getQuantity();

        }
        return Math.round(totalPrice * 100) / 100.0;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
