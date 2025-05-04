package app.entities;

public class ZipCode {

    private int zipCode;
    private String city;

    public ZipCode(int zipCode, String city) {
        this.zipCode = zipCode;
        this.city = city;

    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "ZipCode{" +
                "zipCode=" + zipCode +
                ", city='" + city + '\'' +
                '}';
    }
}
