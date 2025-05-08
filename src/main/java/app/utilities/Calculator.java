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
    private static final int ROOFS = 8;

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
*/

    // Tag
    private final List<Material> roof;
    private final int roofPanelWidth = 109;



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

        this.roof = getMaterialByID(ROOFS);
        selectsRoof();

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


    /**
     * This method finds the best combination of beams while trying to have as little waste of material as possible.
     *
     */
    // Remme
    private void selectsBeams() {
        int remainingLength = this.length;
        List<Material> sortedBeams = new ArrayList<>(beams);
        sortedBeams.sort((a, b) -> Integer.compare(a.getLength(), b.getLength())); //Sorts list by length in ascending order

        List<Material> selectedBeams = new ArrayList<>(); // Will hold the chosen combination of beams

        while (remainingLength > 0) {
            Material best1 = null;
            Material best2 = null;
            int bestTotal = Integer.MAX_VALUE;

            for (Material m1 : sortedBeams) { // Goes through every beam as the first candidate
                int len1 = m1.getLength();
                // Try single beam
                if (len1 >= remainingLength && len1 < bestTotal) {
                    best1 = m1;
                    best2 = null;
                    bestTotal = len1;
                }

                for (Material m2 : sortedBeams) {
                    int sum = len1 + m2.getLength();
                    if (sum >= remainingLength && sum < bestTotal) { // Checks combination and waste to find the best one.
                        best1 = m1;
                        best2 = m2;
                        bestTotal = sum;
                    }
                }
            }

            if (best1 != null) {
                selectedBeams.add(best1);
                if (best2 != null) {
                    selectedBeams.add(best2);
                }
                remainingLength -= bestTotal;
            }
        }

        calculateBeamAmount(selectedBeams);
    }





    /**
     *
     * @param selectedBeams A list of selected beams with the right lengths.
     *
     *                      Final bit:
     *
     *      The loop run through the selected beams, puts in the length of the material and checks the CompleteUnitMaterial.
     *      If its length isn't there. The the material will be added as a CompleteUnitMaterial
     *      The if-statement is there so if we have beams with the same length. So if the second beam is the same as the first one.
     *      It just adds the quantity of the already added CompleteUnitMaterial.
     */

    private void calculateBeamAmount(List<Material> selectedBeams) {
        // Map to store beam length as key and the CompleteUnitMaterial as value
        Map<Integer, CompleteUnitMaterial> grouped = new HashMap<>();

        for (Material m : selectedBeams) {
            int length = m.getLength();

            // If this beam length hasn't been added yet
            if (!grouped.containsKey(length)) {
                CompleteUnitMaterial newItem = new CompleteUnitMaterial(2, "Remme i sider, sadles ned i stolper", m);
                grouped.put(length, newItem);
            } else {
                // If this length is already added, just increase its quantity
                CompleteUnitMaterial existingItem = grouped.get(length);
                int currentQuantity = existingItem.getQuantity();
                existingItem.setQuantity(currentQuantity + 2);
            }
        }
        // Add all the collected materials to the order
        orderMaterials.addAll(grouped.values());
    }


    /**
     * Decided only to have one length for the rafters (600 cm)
     *
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
        int i = (int) Math.ceil(width / 600.0);


        int quantity = (int) Math.ceil(length / 64.5) * i;

        orderMaterials.add(new CompleteUnitMaterial(quantity, "Spær, monteres på rem", rafter));

    }

    /**
     * Finds the roof panels needed across the carports length.
     * Out of our roof lengths: 600, 480, 360. Which are needed across a carport length
     * Will find the longest that can fit and then find others until the remainLength hits 0.
     * If we can find a perfect fit, it will pick the smallest roof panel (will have the least waste to it)
     */
    // Tag
    private void selectsRoof() {
        List<Material> panels = new ArrayList<>(roof);
        panels.sort((a, b) -> Integer.compare(b.getLength(), a.getLength())); // Sorts roof materials by length. Descending order.



        List<Material> selectedPanelsForLength = new ArrayList<>(); // Determine arrangement along the length
        int remainingLength = length;

        while (remainingLength > 0) {
            boolean panelAdded = false;

            // Try to fit the longest possible panel first
            for (Material panel : panels) {
                if (remainingLength >= panel.getLength()) {
                    selectedPanelsForLength.add(panel);
                    remainingLength -= panel.getLength();
                    panelAdded = true;
                    break;
                }
            }

            // If nothing fits, use the smallest panel (least waste)
            if (!panelAdded) {
                selectedPanelsForLength.add(panels.get(panels.size() - 1));
                break;
            }
        }
        calculateRoofAmount(selectedPanelsForLength);

    }

    /**
     *
     * @param selectedPanelsForLength List of roof panels picked for a carport length.
     *                                We now need to find how many of that combination we need.
     *
     * First equation:
     * Calculates how many panels we need across the carport width.
     *
     * First for loop:
     * Goes thought the picked list and puts them in a hashmap with the quantity ( how many we need across the carport width).
     * If there are duplicates (2 with 600 length for example) add to the quantity.
     */
    private void calculateRoofAmount(List<Material> selectedPanelsForLength) {
        // Calculate how many panels are needed across the width of the carport
        int panelsInWidth = (int) Math.ceil((double) width / roofPanelWidth);

        // Create a map to count how many of each panel type we need
        Map<Material, Integer> grouped = new HashMap<>();

        for (Material m : selectedPanelsForLength) {
            // If this panel type hasn't been added yet, put it in the map
            if (!grouped.containsKey(m)) {
                grouped.put(m, panelsInWidth);
            } else {
                // If it already exists, add more panels
                int currentCount = grouped.get(m);
                grouped.put(m, currentCount + panelsInWidth);
            }
        }

        // Add all the counted panels to the order materials list
        for (Map.Entry<Material, Integer> entry : grouped.entrySet()) {
            int quantity = entry.getValue();
            Material material = entry.getKey();
            orderMaterials.add(new CompleteUnitMaterial(quantity, "Tagplader monteres på spær", material));
        }
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

    public List<CompleteUnitMaterial> getOrderMaterials() {
        return orderMaterials;
    }
}
