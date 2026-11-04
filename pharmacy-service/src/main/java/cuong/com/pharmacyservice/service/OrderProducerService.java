package cuong.com.pharmacyservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cuong.com.pharmacyservice.model.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    // Khởi tạo trực tiếp ObjectMapper để loại bỏ hoàn toàn lỗi thiếu Bean
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TOPIC = "medicine-stock-events";

    public OrderProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(OrderEvent orderEvent) {
        try {
            // Chuyển object thành chuỗi JSON
            String jsonMessage = objectMapper.writeValueAsString(orderEvent);

            // Gửi message lên Kafka với key là medicineId để chia Partition chính xác
            kafkaTemplate.send(TOPIC, orderEvent.getMedicineId(), jsonMessage);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gửi sự kiện lên Kafka", e);
        }
    }
}