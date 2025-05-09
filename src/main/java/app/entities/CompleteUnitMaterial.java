package app.entities;

import app.exceptions.DatabaseException;
import app.persistence.CompleteUnitMaterialMapper;
import app.persistence.ConnectionPool;

public class CompleteUnitMaterial {

    private int cumId;
    private int quantity;
    private String description;
    private String materialName;
    private int ordersId;
    private int MaterialLengthId;
    private int msdId;
    private Material material;


    public CompleteUnitMaterial(int cumId, int quantity, String description, int ordersId, Material material) {
        this.cumId = cumId;
        this.quantity = quantity;
        this.description = description;
        this.ordersId = ordersId;
        this.MaterialLengthId = materialLengthId;
    }

    public CompleteUnitMaterial(int cumId, int quantity, String materialName, int ordersId, int mlId, int msdId) {
        this.cumId = cumId;
        this.quantity = quantity;
        this.materialName = materialName;
        this.ordersId = ordersId;
        this.MaterialLengthId = mlId;
        this.msdId = msdId;
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

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
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

    public int getDescriptionId(ConnectionPool connectionPool) {
        try {
            return CompleteUnitMaterialMapper.getDescriptionId(this.description, connectionPool);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public int getMsdId() {
        return msdId;
    }

    public void setMsdId(int msdId) {
        this.msdId = msdId;
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
