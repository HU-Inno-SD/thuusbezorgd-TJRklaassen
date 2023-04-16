package nl.hu.inno.stock.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hu.inno.stock.data.IngredientRepository;
import nl.hu.inno.stock.domain.Ingredient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class Receiver {
    private final IngredientRepository ingredients;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public Receiver(IngredientRepository ingredients, RabbitTemplate rabbitTemplate) {
        this.ingredients = ingredients;
        this.rabbitTemplate = rabbitTemplate;
        objectMapper = new ObjectMapper();
    }

    public void receiveMessage(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        routingKey = routingKey.replaceFirst("^stock\\.", "");
        String content = new String(message.getBody());
        System.out.println("Received message with key: " + routingKey);

        switch (routingKey) {
            case "ingredient.availability":
                getIngredientAvailability(message, content);
                break;
            case "ingredient.subtract":
                subtractStock(message, content);
                break;
            default:
                System.out.println("Unknown routing key: " + routingKey);
        }
    }

    private void getIngredientAvailability(Message message, String content) {
        int availability = -1;

        try {
            List<Long> ingredientIds = objectMapper.readValue(content, new TypeReference<List<Long>>() {});

            if (!ingredientIds.isEmpty()) {
                availability = Integer.MAX_VALUE;
            }

            for(Long id : ingredientIds) {
                Optional<Ingredient> ingredient = ingredients.findById(id);

                if(ingredient.isPresent()) {
                    int nrInStock = ingredient.get().getNrInStock();
                    if (nrInStock < availability) {
                        availability = nrInStock;
                    }
                } else {
                    returnMessage(message, -1);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        returnMessage(message, availability);
    }

    private void subtractStock(Message message, String content) {
        try {
            List<Long> ingredientIds = objectMapper.readValue(content, new TypeReference<List<Long>>() {});

            for(Long id : ingredientIds) {
                Optional<Ingredient> ingredient = ingredients.findById(id);

                ingredient.ifPresent(i -> {
                    i.take(1);
                    ingredients.save(i);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void returnMessage(Message message, Object object) {
        byte[] payload;

        try {
            payload = objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting object to JSON: " + e.getMessage());
            return;
        }

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.setCorrelationId(message.getMessageProperties().getCorrelationId());
        Message responseMessage = new Message(payload, messageProperties);
        rabbitTemplate.send(message.getMessageProperties().getReplyTo(), responseMessage);
    }
}