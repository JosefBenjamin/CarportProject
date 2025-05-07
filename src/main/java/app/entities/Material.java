package app.entities;

import java.util.ArrayList;
import java.util.List;

public class Material {

    private int materialId;
    private String name;
    private String unitName;
    private double price;
    private double meterPrice;
    private int length;
    private List<Integer> lengths;

    public Material(int materialId, String name, String unitName, double price, int length) {
        this.materialId = materialId;
        this.name = name;
        this.unitName = unitName;
        this.price = price;
        this.length = length;
    }

    public Material(int materialId, String name, String unitName, double meterPrice) {
        this.materialId = materialId;
        this.name = name;
        this.unitName = unitName;
        this.meterPrice = meterPrice;
        this.lengths = new ArrayList<>();
    }

    public int getMaterialID() {
        return materialId;
    }

    public void setMaterialID(int materialID) {
        this.materialId = materialID;
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

    public List<Integer> getLengths() {
        return lengths;
    }

    public void setLengths(List<Integer> lengths) {
        this.lengths = lengths;
    }

    public void addLength(int length) {
        this.lengths.add(length);
    }

    @Override
    public String toString() {
        return "Material{" +
                "materialID=" + materialId +
                ", name='" + name + '\'' +
                ", unitName='" + unitName + '\'' +
                ", price=" + price +
                ", length=" + length +
                '}';
    }


}
