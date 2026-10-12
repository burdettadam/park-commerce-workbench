package dev.parkcommerce.pricing;
import jakarta.persistence.*;
@Entity @Table(name="quotes")
public class QuoteEntity {
    @Id @Column(length=36) String id;
    @Column(nullable=false,length=100) String owner;
    @Column(name="expires_epoch_ms",nullable=false) long expiresEpochMs;
    @Lob @Column(nullable=false,columnDefinition="LONGTEXT") String payload;
    protected QuoteEntity(){}
    QuoteEntity(String id,String owner,long expiresEpochMs,String payload){this.id=id;this.owner=owner;this.expiresEpochMs=expiresEpochMs;this.payload=payload;}
}
