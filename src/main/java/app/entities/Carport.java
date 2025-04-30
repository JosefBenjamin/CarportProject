package app.entities;

public class Carport {

    private int carportWidth;
    private int carportLength;
    private int carportHeight;

    public Carport(int carportWidth, int carportLength, int carportHeight) {
        this.carportWidth = carportWidth;
        this.carportLength = carportLength;
        this.carportHeight = carportHeight;
    }

    public int getCarportHeight() {
        return carportHeight;
    }

    public void setCarportHeight(int carportHeight) {
        this.carportHeight = carportHeight;
    }

    public int getCarportLength() {
        return carportLength;
    }

    public void setCarportLength(int carportLength) {
        this.carportLength = carportLength;
    }

    public int getCarportWidth() {
        return carportWidth;
    }

    public void setCarportWidth(int carportWidth) {
        this.carportWidth = carportWidth;
    }

    @Override
    public String toString() {
        return "Carport{" +
                "carportWidth=" + carportWidth +
                ", carportLength=" + carportLength +
                ", carportHeight=" + carportHeight +
                '}';
    }
}
