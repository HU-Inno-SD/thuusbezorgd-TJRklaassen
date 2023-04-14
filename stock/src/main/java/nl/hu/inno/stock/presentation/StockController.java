package nl.hu.inno.thuusbezorgd.presentation;

import nl.hu.inno.thuusbezorgd.data.IngredientRepository;
import nl.hu.inno.thuusbezorgd.domain.Dish;

import nl.hu.inno.thuusbezorgd.data.DishRepository;
import nl.hu.inno.thuusbezorgd.domain.Ingredient;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stock")
public class StockController {


    public record DishDto(Long id, String name, int nrAvailable) {
        public DishDto(Dish d) {
            this(d.getId(), d.getName(), d.getAvailable());
        }
    }


    private final DishRepository dishes;
    private final IngredientRepository ingredients;

    public StockController(DishRepository dishes, IngredientRepository ingredients) {
        this.dishes = dishes;
        this.ingredients = ingredients;
    }

    @GetMapping("/dishes")
    public ResponseEntity<List<DishDto>> getAllDishes() {
        List<DishDto> allDishes = this.dishes.findAll().stream()
                .map(DishDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(allDishes);
    }

    @GetMapping("/dishes/{id}")
    public ResponseEntity<DishDto> getDish(@PathVariable("id") long id) {
        Optional<Dish> d = this.dishes.findById(id);
        if (d.isPresent()) {
            return ResponseEntity.ok(new DishDto(d.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public record IngredientDTO(long id, String name, int available) {
        public static IngredientDTO fromIngredient(Ingredient ingredient) {
            return new IngredientDTO(ingredient.getId(), ingredient.getName(), ingredient.getNrInStock());
        }
    }

    @GetMapping("/ingredients")
    public ResponseEntity<List<IngredientDTO>> getIngredients() {
        List<IngredientDTO> allIngredients = this.ingredients.findAll().stream()
                .map(i -> IngredientDTO.fromIngredient(i))
                .collect(Collectors.toList());
        return ResponseEntity.ok(allIngredients);
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<IngredientDTO> getIngredient(@PathVariable("id") long id) {
        Optional<Ingredient> i = this.ingredients.findById(id);
        if (i.isPresent()) {
            return ResponseEntity.ok(IngredientDTO.fromIngredient(i.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public record DeliveryDTO(int nrDelivered) {
    }

    @GetMapping("/ingredients/{id}/deliveries")
    public ResponseEntity<List<DeliveryDTO>> getDeliveries(@PathVariable("id") long id) {
        Optional<Ingredient> i = this.ingredients.findById(id);
        if (i.isPresent()) {
            return ResponseEntity.ok(new ArrayList<>()); //Daadwerkelijk bijhouden van bezorgingen is nog niet interessant
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/ingredients/{id}/deliveries")
    @Transactional
    public ResponseEntity<IngredientDTO> addDelivery(@PathVariable("id") long id, @RequestBody DeliveryDTO deliveryDTO) {
        Optional<Ingredient> i = this.ingredients.findById(id);
        if (i.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        i.get().deliver(deliveryDTO.nrDelivered);

        return ResponseEntity.ok(IngredientDTO.fromIngredient(i.get()));
    }
}
