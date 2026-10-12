package dev.parkcommerce.pricing;
import dev.parkcommerce.core.*;
import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;
@Repository
public class JpaPricingStore implements Pricing.Store {
    private final PriceBooks books; private final Quotes quotes; private final ObjectMapper json;
    public JpaPricingStore(PriceBooks books,Quotes quotes,ObjectMapper json){this.books=books;this.quotes=quotes;this.json=json;}
    @Override public Optional<Pricing.PriceBook> latest(String id){return books.findTopByProductIdOrderByVersionDesc(id).map(r->read(r.payload,Pricing.PriceBook.class));}
    @Override public void insert(Pricing.PriceBook book){
        try{books.saveAndFlush(new PriceBookEntity(UUID.randomUUID().toString(),book.productId(),book.version(),write(book)));}
        catch(DataIntegrityViolationException e){throw new Failure(Failure.Code.CONFLICT,"This pricing version was already published");}
    }
    @Override public void saveQuote(Pricing.Quote quote){quotes.saveAndFlush(new QuoteEntity(quote.id().toString(),quote.owner(),quote.expiresAt().toEpochMilli(),write(quote)));}
    @Override public Optional<Pricing.Quote> quote(UUID id){return quotes.findById(id.toString()).map(r->read(r.payload,Pricing.Quote.class));}
    private String write(Object value){try{return json.writeValueAsString(value);}catch(Exception e){throw new IllegalStateException("Snapshot serialization failed",e);}}
    private <T>T read(String value,Class<T> type){try{return json.readValue(value,type);}catch(Exception e){throw new IllegalStateException("Stored snapshot is invalid",e);}}
}
