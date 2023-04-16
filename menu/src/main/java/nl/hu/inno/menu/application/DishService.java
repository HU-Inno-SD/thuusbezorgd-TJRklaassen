package nl.hu.inno.menu.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hu.inno.menu.domain.Dish;
import nl.hu.inno.menu.domain.DishIngredient;
import nl.hu.inno.menu.messaging.Messenger;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishService {
    private Messenger messenger;
    private ObjectMapper objectMapper;

    public DishService(Messenger messenger, ObjectMapper objectMapper) {
        this.messenger = messenger;
        this.objectMapper = objectMapper;
    }

    public int getDishAvailability(Dish dish) {
        List<DishIngredient> dishIngredients = dish.getDishIngredients();
        List<Long> ingredientIds = dishIngredients.stream()
                .map(DishIngredient::getIngredientId)
                .collect(Collectors.toList());

        int availability = -1;

        try {
            String json = objectMapper.writeValueAsString(ingredientIds);
            Object response = messenger.sendAndReceive("stock-exchange", "stock.ingredient.availability", json);

            if (response instanceof byte[]) {
                availability = objectMapper.readValue((byte[]) response, Integer.class);
                System.out.println(availability);
            } else {
                System.out.println("Unexpected response type: " + response.getClass());
            }
        } catch (JsonProcessingException e) {
            System.err.println("Error converting object to JSON: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error deserializing the response: " + e.getMessage());
        }

        return availability;
    }

    public void prepareDish(Dish dish) {
        List<DishIngredient> dishIngredients = dish.getDishIngredients();
        List<Long> ingredientIds = dishIngredients.stream()
                .map(DishIngredient::getIngredientId)
                .collect(Collectors.toList());

        try {
            String json = objectMapper.writeValueAsString(ingredientIds);
            messenger.send("stock-exchange", "stock.ingredient.subtract", json);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting object to JSON: " + e.getMessage());
        }
    }
}
