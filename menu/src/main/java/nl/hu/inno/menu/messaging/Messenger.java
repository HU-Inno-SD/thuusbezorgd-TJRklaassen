package nl.hu.inno.menu.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class Messenger {
    private final RabbitTemplate rabbitTemplate;

    public Messenger(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String exchange, String routingKey, Object object) {
        System.out.println("Sending message with key: " + routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, object);
    }

    public Object sendAndReceive(String exchange, String routingKey, Object object) {
        System.out.println("Sending message with key: " + routingKey);
        return rabbitTemplate.convertSendAndReceive(exchange, routingKey, object);
    }
}
