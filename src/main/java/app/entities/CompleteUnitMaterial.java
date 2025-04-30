package app.entities;

public class CompleteUnitMaterial {

    private int cumId;
    private int quantity;
    private String description;
    private int ordersId;
    private int MaterialLengthId;


    public CompleteUnitMaterial(int cumId, int quantity, String description, int ordersId, int materialLengthId) {
        this.cumId = cumId;
        this.quantity = quantity;
        this.description = description;
        this.ordersId = ordersId;
        MaterialLengthId = materialLengthId;
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

    public int getMaterialLengthId() {
        return MaterialLengthId;
    }

    public void setMaterialLengthId(int materialLengthId) {
        MaterialLengthId = materialLengthId;
    }

    @Override
    public String toString() {
        return "CompleteUnitMaterial{" +
                "cumId=" + cumId +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                ", ordersId=" + ordersId +
                ", MaterialLengthId=" + MaterialLengthId +
                '}';
    }
}
