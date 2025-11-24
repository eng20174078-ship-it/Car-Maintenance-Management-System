package models;

public class MaintenanceOrder {
    private String orderId;
    private Vehicle vehicle;
    private String serviceType;
    private String status;

    // Constructor
    public MaintenanceOrder(String orderId, Vehicle vehicle, String serviceType, String status) {
        this.orderId = orderId;
        this.vehicle = vehicle;
        this.serviceType = serviceType;
        this.status = status;
    }

    // Getter and Setter methods
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
