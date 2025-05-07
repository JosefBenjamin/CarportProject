package app.utilities;

import app.entities.CompleteUnitMaterial;
import app.entities.Material;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;

import java.util.*;

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

    // Spær
    private final Material rafter;

    /*
    // Stern
    private final Material stern
    private final int amount of sterns


    // Tag
    private final List<Material> roof;
    private final int amountOfRoofs;

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
        selectsBeams();

        this.rafter = getMaterialByID(RAFTERS).get(0);
        calculateRafters();

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
    private void selectsBeams() {
        int remainingLength = this.length;
        List<Material> sortedBeams = new ArrayList<>(beams);
        sortedBeams.sort((a, b) -> Integer.compare(a.getLength(), b.getLength()));

        List<Material> selectedBeams = new ArrayList<>();

        while (remainingLength > 0) {
            Material best1 = null, best2 = null;
            int bestTotal = Integer.MAX_VALUE;

            for (Material m1 : sortedBeams) {
                int len1 = m1.getLength();
                // Try single beam
                if (len1 >= remainingLength && len1 < bestTotal) {
                    best1 = m1;
                    best2 = null;
                    bestTotal = len1;
                }

                for (Material m2 : sortedBeams) {
                    int sum = len1 + m2.getLength();
                    if (sum >= remainingLength && sum < bestTotal) {
                        best1 = m1;
                        best2 = m2;
                        bestTotal = sum;
                    }
                }
            }

            if (best1 != null) {
                selectedBeams.add(best1);
                if (best2 != null) selectedBeams.add(best2);
                remainingLength -= bestTotal;
            } else {
                // fallback: just pick the smallest
                Material smallest = sortedBeams.get(0);
                selectedBeams.add(smallest);
                remainingLength -= smallest.getLength();
            }
        }

        calculateBeamAmount(selectedBeams);
    }





    /**
     *
     * @param selectedBeams A list of selected beams with the right lengths.
     *
     *                      Final bit:
     *      the compute method of the hashmap gives us the opportunity to update a values for a specific key.
     *      The loop run through the selected beams, puts in the length of the material and checks the CompleteUnitMaterial.
     *      If it's null (there isn't a completeUnitMaterial) we put it in as a value.
     *      The compute is there if we have beams with the same length. So if the second beam is the same as the first one.
     *      cum is not null and will then jumnp to the else statement, which just adds the quantity of the already
     *      added CompleteUnitMaterial.
     */

    private void calculateBeamAmount(List<Material> selectedBeams) {
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

    /**
     * First equation:
     * Checks how many 600 cm long rafters that are needed for the width of the carport.
     * So how many are needed to get from one side of the carport to the other.
     * Math.ceil always rounds up and return and integer.
     *
     * Second equation:
     * Check how man rafters (if have two 600 cm long rafters, then how many of those) are needed for the length of the carport.
     * Divides the max amount of space between rafters (60) and it's width (4.5).
     * Then multiplies by the quantity needed for its width.
     */
    // Spær
    private void calculateRafters() {
        // Checking how many 600 cm long rafters are needed for the width of the carport
        int i = (int) Math.ceil(width / 600.0);

        //Checking how many rafters are needed for the length of the carport, by dividing it by the max amount of space between
        //rafters (60), and the width of a rafter (4.5). Then multiplying it with the amount needed for the width
        int quantity = (int) Math.ceil(length / 64.5) * i;

        orderMaterials.add(new CompleteUnitMaterial(quantity, "Spær, monteres på rem", rafter));

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
