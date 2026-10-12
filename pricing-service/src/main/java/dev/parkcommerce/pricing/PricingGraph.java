package dev.parkcommerce.pricing;
import dev.parkcommerce.core.*;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
@Controller
public class PricingGraph {
    private final Pricing.Service pricing;
    public PricingGraph(Pricing.Service pricing){this.pricing=pricing;}
    @MutationMapping @PreAuthorize("hasAnyRole('GUEST','EMPLOYEE')")
    public Pricing.Quote createQuote(@Argument QuoteContract.Input input,Principal principal){
        Failure.require(input!=null,"A quote request is required");
        return pricing.create(input.toRequest(),principal.getName());
    }
    @QueryMapping @PreAuthorize("hasAnyRole('GUEST','EMPLOYEE')")
    public Pricing.Quote quote(@Argument String id,Principal principal){return pricing.get(QuoteContract.parseId(id),principal.getName());}
    @QueryMapping @PreAuthorize("hasAnyRole('GUEST','EMPLOYEE')")
    public Pricing.QuoteStatus quoteStatus(@Argument String id,Principal principal){return pricing.status(QuoteContract.parseId(id),principal.getName());}
    @SchemaMapping(typeName="QuoteStatus",field="evaluatedAt") public String evaluatedAt(Pricing.QuoteStatus status){return status.evaluatedAt().toString();}
    @SchemaMapping(typeName="Quote",field="createdAt") public String createdAt(Pricing.Quote q){return q.createdAt().toString();}
    @SchemaMapping(typeName="Quote",field="pricingVersion") public String pricingVersion(Pricing.Quote q){return Long.toString(q.pricingVersion());}
    @SchemaMapping(typeName="Quote",field="catalogRevision") public String catalogRevision(Pricing.Quote q){return Long.toString(q.catalogRevision());}
    @SchemaMapping(typeName="Quote",field="visitDate") public String visitDate(Pricing.Quote q){return q.visitDate().toString();}
    @SchemaMapping(typeName="Quote",field="expiresAt") public String expiresAt(Pricing.Quote q){return q.expiresAt().toString();}
    @SchemaMapping(typeName="Money",field="cents") public String cents(Money money){return Long.toString(money.cents());}
    @SchemaMapping(typeName="Money",field="amount") public String amount(Money money){return money.amount();}
    @SchemaMapping(typeName="Money",field="currency") public String currency(Money money){return money.currency();}
}
