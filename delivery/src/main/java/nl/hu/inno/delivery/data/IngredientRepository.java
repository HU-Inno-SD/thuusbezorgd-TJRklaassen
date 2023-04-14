package nl.hu.inno.stock.data;

import nl.hu.inno.thuusbezorgd.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}