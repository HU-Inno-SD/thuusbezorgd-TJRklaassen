package nl.hu.inno.thuusbezorgd.presentation;

import nl.hu.inno.thuusbezorgd.application.DeliveryService;
import nl.hu.inno.thuusbezorgd.data.DeliveryRepository;
import nl.hu.inno.thuusbezorgd.data.ReviewRepository;
import nl.hu.inno.thuusbezorgd.domain.*;

import nl.hu.inno.thuusbezorgd.security.User;
import nl.hu.inno.thuusbezorgd.security.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryRepository deliveries;
    private final ReviewRepository reviews;

    public DeliveryController(DeliveryService deliveryService, DeliveryRepository deliveries, ReviewRepository reviews) {
        this.deliveryService = deliveryService;
        this.deliveries = deliveries;
        this.reviews = reviews;
    }

    public record DeliveryResponseDTO(long id, String riderName, int minutesRemaining, String orderLink) {
        public static DeliveryResponseDTO fromDelivery(Delivery d, int minutes) {
            return new DeliveryResponseDTO(d.getId(), d.getRider().getName(), minutes, "/orders/" + d.getOrder().getId());
        }
    }

    @GetMapping
    public List<DeliveryResponseDTO> deliveries(User user) {
        List<Delivery> found = deliveries.findByOrder_User(user);

        return found.stream().map(d -> DeliveryResponseDTO.fromDelivery(d, this.deliveryService.getMinutesRemaining(d))).toList();
    }

    @GetMapping("{id}")
    public ResponseEntity<DeliveryResponseDTO> getDelivery(User user, @PathVariable long id) {
        Optional<Delivery> delivery = this.deliveries.findById(id);

        if (delivery.isEmpty() || delivery.get().getOrder().getUser() != user) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                DeliveryResponseDTO.fromDelivery(delivery.get(), this.deliveryService.getMinutesRemaining(delivery.get())));
    }


    public record ReviewDTO(String delivery, String reviewerName, int rating) {
        public static ReviewDTO fromReview(DeliveryReview review) {
            return new ReviewDTO(review.getDelivery().getId().toString(), review.getUser().getName(), review.getRating().toInt());
        }
    }

    public record PostedReviewDTO(int rating) {

    }


    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getDishReviews(@PathVariable("id") long id) {
        Optional<Delivery> d = this.deliveries.findById(id);
        if (d.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<DeliveryReview> reviews = this.reviews.findDeliveryReviews(d.get());
        return ResponseEntity.ok(reviews.stream().map(ReviewDTO::fromReview).toList());
    }

    @PostMapping("/{id}/reviews")
    @Transactional
    public ResponseEntity<ReviewDTO> postReview(User user, @PathVariable("id") long id, @RequestBody PostedReviewDTO reviewDTO) {
        Optional<Delivery> found = this.deliveries.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DeliveryReview review = new DeliveryReview(found.get(), ReviewRating.fromInt(reviewDTO.rating()), user);
        reviews.save(review);

        return ResponseEntity.ok(ReviewDTO.fromReview(review));
    }
}
