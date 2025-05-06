package app.entities;

public class CompleteUnitMaterial {

    private int cumId;
    private int quantity;
    private String description;
    private int ordersId;
    private Material material;


    public CompleteUnitMaterial(int cumId, int quantity, String description, int ordersId, Material material) {
        this.cumId = cumId;
        this.quantity = quantity;
        this.description = description;
        this.ordersId = ordersId;
        this.material = material;
    }

    public CompleteUnitMaterial(int quantity, String description, Material material) {
        this.quantity = quantity;
        this.description = description;
        this.material = material;
    }

    public int getCumId() {
        return cumId;
    }

    public void setCumId(int cumId) {
        this.cumId = cumId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(int ordersId) {
        this.ordersId = ordersId;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return "CompleteUnitMaterial{" +
                "cumId=" + cumId +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                ", ordersId=" + ordersId +
                ", material=" + material +
                '}';
    }
}
