package app.entities;

import app.utilities.Role;

public class User {
    private int userID;
    private String email;
    private String password;
    private int tlf;
    private boolean isAdmin;
    private String address;
    private ZipCode zipCode;

    public User(int userID, String email, String password, int tlf, boolean isAdmin) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.tlf = tlf;
        this.isAdmin = isAdmin;
    }

    public User(int userID, String email, String password, int tlf, boolean isAdmin, String address) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.tlf = tlf;
        this.isAdmin = isAdmin;
        this.address = address;
    }

    public User(String email, String password, int tlf, boolean isAdmin, String address) {
        this.email = email;
        this.password = password;
        this.tlf = tlf;
        this.isAdmin = isAdmin;
        this.address = address;
    }



    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTlf() {
        return tlf;
    }

    public void setTlf(int tlf) {
        this.tlf = tlf;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ZipCode getZipCode() {
        return zipCode;
    }

    public void setZipCode(ZipCode zipCode) {
        this.zipCode = zipCode;
    }

    public Role getRole() {
        return isAdmin ? Role.ADMIN : Role.USER;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", tlf=" + tlf +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
