package cuong.com.pharmacyservice.controller;

import cuong.com.pharmacyservice.model.OrderEvent;
import cuong.com.pharmacyservice.service.OrderProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pharmacy")
public class OrderController {

    private final OrderProducerService producerService;

    public OrderController(OrderProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/sell")
    public ResponseEntity<String> sellMedicine(
            @RequestParam String medicineId,
            @RequestParam int quantity) {

        // Tạo đối tượng sự kiện đơn hàng
        OrderEvent orderEvent = new OrderEvent(
                "ORD-" + System.currentTimeMillis(),
                medicineId,
                quantity,
                System.currentTimeMillis()
        );

        // Đẩy sự kiện lên Kafka Producer
        producerService.sendOrderEvent(orderEvent);

        // Trả về đúng thông báo yêu cầu
        return ResponseEntity.ok("Thanh toán thành công! Sự kiện đã được gửi tới Kafka.");
    }
}