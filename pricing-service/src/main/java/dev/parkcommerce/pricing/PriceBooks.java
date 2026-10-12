package dev.parkcommerce.pricing;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PriceBooks extends JpaRepository<PriceBookEntity,String> {
    Optional<PriceBookEntity> findTopByProductIdOrderByVersionDesc(String productId);
}
