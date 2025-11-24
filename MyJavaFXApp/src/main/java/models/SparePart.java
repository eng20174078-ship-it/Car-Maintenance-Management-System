package models;

public class SparePart {
    private String partId;
    private String name;
    private int quantity;

    // Constructor
    public SparePart(String partId, String name, int quantity) {
        this.partId = partId;
        this.name = name;
        this.quantity = quantity;
    }

    // Getter and Setter methods
    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
