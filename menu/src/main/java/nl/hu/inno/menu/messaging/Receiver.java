package nl.hu.inno.menu.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hu.inno.menu.application.DishService;
import nl.hu.inno.menu.data.DishRepository;
import nl.hu.inno.menu.domain.Dish;
import nl.hu.inno.menu.dto.DishDTO;
import nl.hu.inno.menu.dto.ReviewDTO;
import nl.hu.inno.menu.dto.PostedReviewDTO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class Receiver {
    private final DishRepository dishes;
    private final DishService dishService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public Receiver(DishRepository dishes, RabbitTemplate rabbitTemplate, DishService dishService) {
        this.dishes = dishes;
        this.dishService = dishService;
        this.rabbitTemplate = rabbitTemplate;
        objectMapper = new ObjectMapper();
    }

    public void receiveMessage(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        routingKey = routingKey.replaceFirst("^menu\\.", "");
        String content = new String(message.getBody());
        System.out.println("Received message with key: " + routingKey);

        switch (routingKey) {
            case "dish.find":
                findDish(message, content);
                break;
            case "dish.available":
                isDishAvailable(message, content);
                break;
            case "dish.prepare":
                prepareDish(content);
                break;
            default:
                System.out.println("Unknown routing key: " + routingKey);
        }
    }

    private void findDish(Message message, String content) {
        long id = Long.parseLong(content);
        Optional<Dish> dishResult = this.dishes.findById(id);

        dishResult.ifPresent(dish -> {
            DishDTO dishDto = DishDTO.fromDish(dish);
            returnMessage(message, dishDto);
        });
    }

    private void isDishAvailable(Message message, String content) {
        long id = Long.parseLong(content);
        Optional<Dish> dishResult = this.dishes.findById(id);

        dishResult.ifPresent(dish -> {
            if(dishService.getDishAvailability(dish) > 0) {
                returnMessage(message, true);
            } else {
                returnMessage(message, false);
            }
        });
    }

    private void prepareDish(String content) {
        long id = Long.parseLong(content);
        Optional<Dish> dishResult = this.dishes.findById(id);

        dishResult.ifPresent(dishService::prepareDish);
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