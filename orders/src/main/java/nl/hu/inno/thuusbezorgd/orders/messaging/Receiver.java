package nl.hu.inno.thuusbezorgd.orders.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hu.inno.thuusbezorgd.orders.data.OrderRepository;
import nl.hu.inno.thuusbezorgd.orders.domain.Order;
import nl.hu.inno.thuusbezorgd.orders.domain.OrderStatus;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Receiver {
    private final OrderRepository orders;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public Receiver(OrderRepository orders, RabbitTemplate rabbitTemplate) {
        this.orders = orders;
        this.rabbitTemplate = rabbitTemplate;
        objectMapper = new ObjectMapper();
    }

    public void receiveMessage(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        routingKey = routingKey.replaceFirst("^orders\\.", "");
        String content = new String(message.getBody());
        System.out.println("Received message with key: " + routingKey);

        switch (routingKey) {
            case "status":
                getStatus(message, content);
                break;
            case "complete":
                markComplete(content);
                break;
            default:
                System.out.println("Unknown routing key: " + routingKey);
        }
    }

    private void getStatus(Message message, String content) {
        long id = Long.parseLong(content);
        Optional<Order> orderResult = this.orders.findById(id);
        orderResult.ifPresent(order -> {
            returnMessage(message, order.getStatus().name());
        });
    }

    private void markComplete(String content) {
        long id = Long.parseLong(content);
        Optional<Order> orderResult = this.orders.findById(id);
        orderResult.ifPresent(order -> {
            order.setStatus(OrderStatus.Delivered);
            orders.save(order);
        });
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