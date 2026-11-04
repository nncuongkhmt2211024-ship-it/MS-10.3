package cuong.com.pharmacyservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cuong.com.pharmacyservice.model.OrderEvent;
import cuong.com.pharmacyservice.entity.Order;
import cuong.com.pharmacyservice.entity.Medicine;
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
            // 1. Nhận sự kiện từ Kafka
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            System.out.println("-> Consumer nhận được sự kiện: " + message);

            // 2. Tìm thuốc và trừ tồn kho (UPDATE medicines)
            Medicine medicine = medicineRepository.findById(Long.valueOf(event.getMedicineId()))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc có ID: " + event.getMedicineId()));

            medicine.setQuantity(medicine.getQuantity() - event.getQuantity());
            medicineRepository.save(medicine);

            // 3. Thêm mới đơn hàng (INSERT orders)
            Order order = new Order();
            order.setMedicineId(Long.valueOf(event.getMedicineId()));
            order.setQuantity(event.getQuantity());
            order.setTimestamp(event.getTimestamp());
            orderRepository.save(order);

            // 4. In thông báo thành công khớp mẫu
            System.out.println("Thêm mới đơn hàng thành công !");

        } catch (Exception e) {
            System.err.println("Lỗi xử lý sự kiện Kafka: " + e.getMessage());
        }
    }
}