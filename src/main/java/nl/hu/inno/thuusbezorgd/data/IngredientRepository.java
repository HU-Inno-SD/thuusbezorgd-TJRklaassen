package nl.hu.inno.thuusbezorgd.data;

import nl.hu.inno.thuusbezorgd.domain.Dish;
import nl.hu.inno.thuusbezorgd.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
