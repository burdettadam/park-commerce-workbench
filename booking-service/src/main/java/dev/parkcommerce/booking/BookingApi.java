package dev.parkcommerce.booking;
import dev.parkcommerce.core.*;
import dev.parkcommerce.support.RemoteCalls;
import java.security.Principal;
import java.time.Clock;
import java.util.UUID;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
@RestController
public class BookingApi {
    public record ReserveRequest(String eventId,UUID quoteId){}
    public record ConfirmRequest(UUID holdId){}
    public record SessionSubject(String subject){}
    private final Booking.Service service;
    public BookingApi(Booking.Service service){this.service=service;}
    @GetMapping("/api/session") @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<SessionSubject> session(Principal principal) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new SessionSubject(principal.getName()));
    }
    @PostMapping("/api/orders/recovery")
    public ResponseEntity<Booking.Recovery> recover(@RequestBody ConfirmRequest request,
            @RequestHeader("Idempotency-Key") String key, Principal principal) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
            .body(service.recover(request.holdId(), principal.getName(), key));
    }
    @PostMapping("/api/admin/events") @ResponseStatus(HttpStatus.CREATED)
    public Booking.Event event(@RequestBody Booking.Event event){return service.create(event);}
    @GetMapping("/api/events/{id}/availability") public Booking.Availability availability(@PathVariable String id){return service.availability(id);}
    @PostMapping("/api/holds") @ResponseStatus(HttpStatus.CREATED)
    public Booking.Hold hold(@RequestBody ReserveRequest request,Principal principal){return service.reserve(request.eventId(),request.quoteId(),principal.getName());}
    @PostMapping("/api/orders")
    public Booking.Order confirm(@RequestBody ConfirmRequest request,@RequestHeader("Idempotency-Key") String key,Principal principal){return service.confirm(request.holdId(),principal.getName(),key);}
    @GetMapping("/api/orders/{id}") public Booking.Order order(@PathVariable UUID id,Principal principal){return service.get(id,principal.getName());}
    @Configuration(proxyBeanMethods=false) static class Wiring {
        @Bean Pricing.Client pricingClient(RestClient.Builder builder,Environment env){
            RestClient client=RemoteCalls.client(builder,env.getRequiredProperty("park.pricing-url"),env.getRequiredProperty("DEMO_SERVICE_TOKEN"));
            return id->RemoteCalls.get(client,"/api/internal/quotes/{id}",Pricing.Quote.class,id);
        }
        @Bean Booking.Service bookingService(Booking.Store store,Pricing.Client pricing,Clock clock){return new Booking.Service(store,pricing,clock);}
    }
}
