package nl.hu.inno.menu.dto;

import nl.hu.inno.menu.domain.DishReview;

public record ReviewDTO(String dish, String reviewerName, int rating) {
    public static ReviewDTO fromReview(DishReview review) {
        return new ReviewDTO(review.getDish().getName(), review.getUser().getName(), review.getRating().toInt());
    }
}
