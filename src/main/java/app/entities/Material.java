package app.entities;

import java.util.ArrayList;
import java.util.List;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;

public class Material {

    private int materialId;
    private String name;
    private String unitName;
    private double meterPrice;
    private int length;
    private List<Integer> lengths;

    public Material(int materialId, String name, String unitName, double price, int length) {
        this.materialId = materialId;
        this.name = name;
        this.unitName = unitName;
        this.meterPrice = price;
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

    public double getMeterPrice() {
        return meterPrice;
    }

    public void setMeterPrice(double meterPrice) {
        this.meterPrice = meterPrice;
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

    public int getLengthID(ConnectionPool connectionPool) {
        try {
            return MaterialMapper.getLengthID(this.materialID, this.length, connectionPool);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String toString() {
        return "Material{" +
                "materialID=" + materialId +
                ", name='" + name + '\'' +
                ", unitName='" + unitName + '\'' +
                ", meterPrice=" + meterPrice +
                ", length=" + length +
                '}';
    }


}
