package dev.parkcommerce.pricing;
import org.springframework.data.jpa.repository.JpaRepository;
public interface Quotes extends JpaRepository<QuoteEntity,String>{}
