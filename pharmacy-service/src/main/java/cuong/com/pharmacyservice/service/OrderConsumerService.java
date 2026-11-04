package cuong.com.pharmacyservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cuong.com.pharmacyservice.model.OrderEvent;
import cuong.com.pharmacyservice.entity.Order; // Entity đơn hàng của bạn
import cuong.com.pharmacyservice.entity.Medicine; // Entity thuốc của bạn
import cuong.com.pharmacyservice.repository.OrderRepository;
import cuong.com.pharmacyservice.repository.MedicineRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderRepository orderRepository;
    private final MedicineRepository medicineRepository;

    public OrderConsumerService(OrderRepository orderRepository, MedicineRepository medicineRepository) {
        this.orderRepository = orderRepository;
        this.medicineRepository = medicineRepository;
    }

    @KafkaListener(topics = "medicine-stock-events", groupId = "pharmacy-inventory-group")
    @Transactional
    public void consumeOrderEvent(String message) {
        try {
            // 1. Chuyển đổi chuỗi JSON từ Kafka thành OrderEvent
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            System.out.println("-> Consumer nhận được sự kiện: " + message);

            // 2. Thực hiện INSERT đơn hàng mới vào bảng orders (Tương ứng log Hibernate 1)
            Order order = new Order();
            order.setMedicineId(Long.valueOf(event.getMedicineId()));
            order.setQuantity(event.getQuantity());
            order.setTimestamp(event.getTimestamp());
            // Thiết lập thêm giá bán, tổng tiền tùy theo thiết kế database của bạn
            orderRepository.save(order);

            // 3. Thực hiện SELECT và UPDATE trừ tồn kho trong bảng medicines (Tương ứng log Hibernate 2 & 3)
            Medicine medicine = medicineRepository.findById(Long.valueOf(event.getMedicineId()))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc có ID: " + event.getMedicineId()));

            // Trừ số lượng tồn kho
            medicine.setQuantity(medicine.getQuantity() - event.getQuantity());
            medicineRepository.save(medicine);

            // 4. In thông báo thành công giống hệt kết quả mong muốn
            System.out.println("Thêm mới đơn hàng thành công !");

        } catch (Exception e) {
            System.err.println("Lỗi xử lý sự kiện Kafka: " + e.getMessage());
        }
    }
}