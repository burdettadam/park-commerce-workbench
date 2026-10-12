package dev.parkcommerce.pricing;
import dev.parkcommerce.core.*;
import dev.parkcommerce.support.RemoteCalls;
import java.security.Principal;
import java.time.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
@RestController
public class PricingApi {
    private final Pricing.Service pricing;private final Pricing.Store store;
    public PricingApi(Pricing.Service pricing,Pricing.Store store){this.pricing=pricing;this.store=store;}
    @PostMapping("/api/admin/price-books") @ResponseStatus(HttpStatus.CREATED)
    public Pricing.PriceBook publish(@RequestBody Pricing.PriceBook book){return pricing.publish(book);}
    @PostMapping("/api/admin/price-books/preview")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public Pricing.Preview preview(@RequestBody Pricing.PreviewRequest request){return pricing.preview(request);}
    @GetMapping("/api/admin/price-books/{id}") public Pricing.PriceBook book(@PathVariable String id){
        Failure.id(id,"Product ID");return store.latest(id).orElseThrow(()->new Failure(Failure.Code.NOT_FOUND,"No price book"));
    }
    @PostMapping("/api/quotes") @ResponseStatus(HttpStatus.CREATED)
    public Pricing.Quote create(@RequestBody QuoteContract.Input input,Principal principal){
        Failure.require(input!=null,"A quote request is required");
        return pricing.create(input.toRequest(),principal.getName());
    }
    @GetMapping("/api/quotes/{id}") public Pricing.Quote get(@PathVariable String id,Principal principal){return pricing.get(QuoteContract.parseId(id),principal.getName());}
    @GetMapping("/api/quotes/{id}/status") public Pricing.QuoteStatus status(@PathVariable String id,Principal principal){return pricing.status(QuoteContract.parseId(id),principal.getName());}
    @GetMapping("/api/internal/quotes/{id}") public Pricing.Quote internal(@PathVariable String id){return pricing.internal(QuoteContract.parseId(id));}
    @Configuration(proxyBeanMethods=false) static class Wiring {
        @Bean Catalog.Client catalogClient(RestClient.Builder builder,Environment env){
            RestClient client=RemoteCalls.client(builder,env.getRequiredProperty("park.catalog-url"),env.getRequiredProperty("DEMO_SERVICE_TOKEN"));
            return (id,revision)->RemoteCalls.get(client,"/api/products/{id}/revisions/{revision}",Catalog.Product.class,id,revision);
        }
        @Bean Pricing.Service pricingService(Pricing.Store store,Catalog.Client catalog,Clock clock){return new Pricing.Service(store,catalog,clock,ZoneId.of("America/New_York"));}
    }
}
