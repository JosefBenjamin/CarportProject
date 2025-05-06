package app.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Order {
    private int orderID;
    private int userID;
    private Carport carport;
    private LocalDate date;
    private double totalPrice;
    private int status;
    private BillOfMaterials billOfMaterials;

    public Order(int orderID, int userID, Carport carport, LocalDate date, double totalPrice, int status) {
        this.orderID = orderID;
        this.userID = userID;
        this.carport = carport;
        this.date = date;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Carport getCarport() {
        return carport;
    }

    public void setCarport(Carport carport) {
        this.carport = carport;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BillOfMaterials getBillOfMaterials() {
        return billOfMaterials;
    }

    public void setBillOfMaterials(BillOfMaterials billOfMaterials) {
        this.billOfMaterials = billOfMaterials;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", userID=" + userID +
                ", carport=" + carport +
                ", date=" + date +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                ", billOfMaterials=" + billOfMaterials +
                '}';
    }
}
