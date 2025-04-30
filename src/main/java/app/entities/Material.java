package app.entities;

public class Material {

    private int materialID;
    private String name;
    private String unitName;
    private double price;
    private int length;

    public Material(int materialID, String name, String unitName, double price, int length) {
        this.materialID = materialID;
        this.name = name;
        this.unitName = unitName;
        this.price = price;
        this.length = length;
    }

    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Material{" +
                "materialID=" + materialID +
                ", name='" + name + '\'' +
                ", unitName='" + unitName + '\'' +
                ", price=" + price +
                ", length=" + length +
                '}';
    }
}
