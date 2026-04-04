<<<<<<< HEAD
package vn.hdbank.intern.orderservice.listener;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrderEvent(String message) {
        kafkaTemplate.send("notificationTopic", message);
    }
}
=======
package vn.hdbank.intern.orderservice.listener;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrderEvent(String message) {
        kafkaTemplate.send("notificationTopic", message);
    }
}
>>>>>>> 5097b7c3 (Tuan07/Init commit for Tuan07)
