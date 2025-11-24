package models;

public class Invoice {
    private String invoiceId;
    private MaintenanceOrder maintenanceOrder;
    private double totalAmount;

    // Constructor
    public Invoice(String invoiceId, MaintenanceOrder maintenanceOrder, double totalAmount) {
        this.invoiceId = invoiceId;
        this.maintenanceOrder = maintenanceOrder;
        this.totalAmount = totalAmount;
    }

    // Getter and Setter methods
    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public MaintenanceOrder getMaintenanceOrder() {
        return maintenanceOrder;
    }

    public void setMaintenanceOrder(MaintenanceOrder maintenanceOrder) {
        this.maintenanceOrder = maintenanceOrder;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
