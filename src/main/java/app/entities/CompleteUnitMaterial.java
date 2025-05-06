package app.entities;

public class CompleteUnitMaterial {

    private int cumId;
    private int quantity;
    private String description;
    private int ordersId;
    private int MaterialLengthId;
    private int msdId;


    public CompleteUnitMaterial(int cumId, int quantity, String description, int ordersId, int materialLengthId) {
        this.cumId = cumId;
        this.quantity = quantity;
        this.description = description;
        this.ordersId = ordersId;
        this.MaterialLengthId = materialLengthId;
    }

    public CompleteUnitMaterial(int cumId, int quantity, int ordersId, int mlId, int msdId) {
        this.cumId = cumId;
        this.quantity = quantity;
        this.ordersId = ordersId;
        this.MaterialLengthId = mlId;
        this.msdId = msdId;
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
