package nl.hu.inno.delivery.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hu.inno.delivery.application.DeliveryService;
import nl.hu.inno.delivery.data.DeliveryRepository;
import nl.hu.inno.delivery.domain.Delivery;
import nl.hu.inno.delivery.domain.DeliveryOrder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Receiver {
    private final DeliveryRepository deliveries;
    private final DeliveryService deliveryService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public Receiver(DeliveryRepository deliveries, RabbitTemplate rabbitTemplate, DeliveryService deliveryService) {
        this.deliveries = deliveries;
        this.deliveryService = deliveryService;
        this.rabbitTemplate = rabbitTemplate;
        objectMapper = new ObjectMapper();
    }

    public void receiveMessage(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        routingKey = routingKey.replaceFirst("^delivery\\.", "");
        String content = new String(message.getBody());
        System.out.println("Received message with key: " + routingKey);

        switch (routingKey) {
            case "start":
                startDelivery(content);
                break;
            default:
                System.out.println("Unknown routing key: " + routingKey);
        }
    }

    private void startDelivery(String content) {
        long orderId = Long.parseLong(content);
        DeliveryOrder deliveryOrder = new DeliveryOrder(orderId);
        deliveryService.scheduleDelivery(deliveryOrder);
    }

    private void returnMessage(Message message, Object object) {
        String json = "";
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting DishDTO to JSON: " + e.getMessage());
            return;
        }
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(message.getMessageProperties().getCorrelationId());
        Message responseMessage = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.send(message.getMessageProperties().getReplyTo(), responseMessage);
    }
}