package nl.hu.inno.stock.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class Messenger {
    private final RabbitTemplate rabbitTemplate;

    public Messenger(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String exchange, String routingKey, Object object) {
        rabbitTemplate.convertAndSend(exchange, routingKey, object);
    }

    public Object sendAndReceive(String exchange, String routingKey, Object object) {
        return rabbitTemplate.convertSendAndReceive(exchange, routingKey, object);
    }
}
