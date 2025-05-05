package app.entities;

public class Material {

    private int materialID;
    private String name;
    private String unitName;
    private double meterPrice;
    private int length;

    public Material(int materialID, String name, String unitName, double price, int length) {
        this.materialID = materialID;
        this.name = name;
        this.unitName = unitName;
        this.meterPrice = price;
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

    public double getMeterPrice() {
        return meterPrice;
    }

    public void setMeterPrice(double meterPrice) {
        this.meterPrice = meterPrice;
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
                ", meterPrice=" + meterPrice +
                ", length=" + length +
                '}';
    }
}
