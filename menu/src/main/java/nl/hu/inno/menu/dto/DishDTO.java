package nl.hu.inno.menu.dto;

import nl.hu.inno.menu.domain.Dish;

public record DishDTO(long id, String name) {
    public static DishDTO fromDish(Dish d) {
        return new DishDTO(d.getId(), d.getName());
    }
}
