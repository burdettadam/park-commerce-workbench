package dev.parkcommerce.pricing;
import jakarta.persistence.*;
@Entity @Table(name="price_books",uniqueConstraints=@UniqueConstraint(name="uk_price_book_version",columnNames={"product_id","pricing_version"}))
public class PriceBookEntity {
    @Id @Column(length=36) String id;
    @Column(name="product_id",nullable=false,length=64) String productId;
    @Column(name="pricing_version",nullable=false) long version;
    @Lob @Column(nullable=false,columnDefinition="LONGTEXT") String payload;
    protected PriceBookEntity(){}
    PriceBookEntity(String id,String productId,long version,String payload){this.id=id;this.productId=productId;this.version=version;this.payload=payload;}
}
