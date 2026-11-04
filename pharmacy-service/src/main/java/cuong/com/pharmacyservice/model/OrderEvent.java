package cuong.com.pharmacyservice.model;

public class OrderEvent {
    private String orderId;
    private String medicineId;
    private int quantity;
    private Long timestamp; // Dùng Long thay vì long

    public OrderEvent() {}

    public OrderEvent(String orderId, String medicineId, int quantity, Long timestamp) {
        this.orderId = orderId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}