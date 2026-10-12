package dev.parkcommerce.booking;

import dev.parkcommerce.core.*;
import java.security.Principal;
import java.time.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Separate route avoids changing the event-only response or idempotency contract. */
@RestController
@RequestMapping("/api/admission/orders")
@PreAuthorize("hasRole('GUEST')")
public class AdmissionApi {
    public record ConfirmRequest(String quoteId) {}
    private final Admission.Service service;
    public AdmissionApi(Admission.Service service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Admission.Order> confirm(@RequestBody ConfirmRequest request,
            @RequestHeader("Idempotency-Key") String key, Principal principal) {
        Failure.require(request != null, "Quote request is required");
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
            .body(service.confirm(QuoteContract.parseId(request.quoteId()), principal.getName(), key));
    }
    @PostMapping("/recovery")
    public ResponseEntity<Admission.Recovery> recover(@RequestBody ConfirmRequest request,
            @RequestHeader("Idempotency-Key") String key, Principal principal) {
        Failure.require(request != null, "Quote request is required");
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
            .body(service.recover(QuoteContract.parseId(request.quoteId()), principal.getName(), key));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Admission.Order> get(@PathVariable String id, Principal principal) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
            .body(service.get(QuoteContract.parseId(id), principal.getName()));
    }
    @Configuration(proxyBeanMethods = false)
    static class Wiring {
        @Bean Admission.Service admissionService(Admission.Store store, Pricing.Client pricing, Clock clock, Environment env) {
            return new Admission.Service(store, pricing, clock, ZoneId.of(env.getRequiredProperty("park.time-zone")));
        }
    }
}
