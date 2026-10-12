package dev.parkcommerce.catalog;

import dev.parkcommerce.core.Catalog;
import dev.parkcommerce.core.CatalogEditorial;
import java.security.Principal;
import java.time.Clock;
import java.util.List;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class CatalogApi {
    private final Catalog.Service service;
    private final CatalogEditorial.Service editorial;
    public CatalogApi(Catalog.Service service, CatalogEditorial.Service editorial) {
        this.service = service; this.editorial = editorial;
    }
    @GetMapping("/api/products") public List<Catalog.Product> products() { return service.products(); }
    @GetMapping("/api/products/{id}") public Catalog.Product latest(@PathVariable String id) { return service.latest(id); }
    @GetMapping("/api/products/{id}/revisions/{revision}")
    public Catalog.Product revision(@PathVariable String id, @PathVariable long revision) { return service.revision(id, revision); }
    /** Legacy request/response shape retained; HTTP publication now always records the authenticated actor. */
    @PostMapping("/api/admin/products") @ResponseStatus(HttpStatus.CREATED)
    public Catalog.Product publish(@RequestBody Catalog.Product product, Principal principal) {
        return editorial.publishDirect(product, principal.getName());
    }
    @Configuration(proxyBeanMethods=false) static class Wiring {
        @Bean Catalog.Service catalogService(Catalog.Store store) { return new Catalog.Service(store); }
        @Bean CatalogEditorial.Service catalogEditorial(CatalogEditorial.Store store, Clock clock) {
            return new CatalogEditorial.Service(store, clock);
        }
    }
}
