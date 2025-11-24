package models;
public class Vehicle {
    private String licensePlate;
    private String model;
    private String owner;

    // Constructor
    public Vehicle(String licensePlate, String model, String owner) {
        this.licensePlate = licensePlate;
        this.model = model;
        this.owner = owner;
    }

    // Getter and Setter methods
    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
