package nl.hu.inno.menu.presentation;

import nl.hu.inno.menu.data.DishRepository;
import nl.hu.inno.menu.data.ReviewRepository;
import nl.hu.inno.menu.domain.Dish;
import nl.hu.inno.menu.domain.DishReview;
import nl.hu.inno.menu.domain.ReviewRating;
import nl.hu.inno.menu.security.User;
import nl.hu.inno.menu.dto.DishDTO;
import nl.hu.inno.menu.dto.ReviewDTO;
import nl.hu.inno.menu.dto.PostedReviewDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishRepository dishes;
    private final ReviewRepository reviews;

    public DishController(DishRepository dishes, ReviewRepository reviews) {
        this.dishes = dishes;
        this.reviews = reviews;
    }

    @GetMapping
    public List<DishDTO> getDishes() {
        return this.dishes.findAll().stream()
                .map(DishDTO::fromDish)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<DishDTO> getDish(@PathVariable long id) {
        Optional<Dish> dishResult = this.dishes.findById(id);
        if (dishResult.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(DishDTO.fromDish(dishResult.get()));
        }
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getDishReviews(@PathVariable("id") long id) {
        Optional<Dish> d = this.dishes.findById(id);
        if (d.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<DishReview> reviews = this.reviews.findDishReviews(d.get());
        return ResponseEntity.ok(reviews.stream().map(ReviewDTO::fromReview).toList());
    }

    @PostMapping("/{id}/reviews")
    @Transactional
    public ResponseEntity<ReviewDTO> postReview(User user, @PathVariable("id") long id, @RequestBody PostedReviewDTO reviewDTO) {
        Optional<Dish> found = this.dishes.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DishReview review = new DishReview(found.get(), ReviewRating.fromInt(reviewDTO.rating()), user);
        reviews.save(review);

        return ResponseEntity.ok(ReviewDTO.fromReview(review));
    }
}
