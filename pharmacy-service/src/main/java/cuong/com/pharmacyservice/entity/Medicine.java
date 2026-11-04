package cuong.com.pharmacyservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "medicinse") // Khớp với tên bảng trong log Hibernate của bạn
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String medicineName;
    private String description;
    private double price;
    private int quantity;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}