package dev.parkcommerce.catalog;

import dev.parkcommerce.core.CatalogEditorial;
import dev.parkcommerce.core.Failure;
import java.security.Principal;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/** Admin-only URLs are protected by the owning service's EMPLOYEE filter rule. */
@RestController
@RequestMapping("/api/admin/products/{id}")
public class CatalogEditorialApi {
    /** Boxed preconditions distinguish omitted/null fields from an explicit initial version 0. */
    public record SaveDraftInput(Long expectedDraftVersion, Long basePublishedRevision, CatalogEditorial.Content content) {
        CatalogEditorial.SaveDraft command() {
            Failure.require(expectedDraftVersion != null && basePublishedRevision != null, "Both expected versions are required");
            return new CatalogEditorial.SaveDraft(expectedDraftVersion, basePublishedRevision, content);
        }
    }
    public record PublishDraftInput(Long draftVersion, Long expectedPublishedRevision) {
        CatalogEditorial.PublishDraft command() {
            Failure.require(draftVersion != null && expectedPublishedRevision != null, "Draft and expected published versions are required");
            return new CatalogEditorial.PublishDraft(draftVersion, expectedPublishedRevision);
        }
    }
    private final CatalogEditorial.Service service;
    public CatalogEditorialApi(CatalogEditorial.Service service) { this.service = service; }
    @PostMapping("/drafts")
    public ResponseEntity<CatalogEditorial.Draft> save(@PathVariable String id,
            @RequestBody SaveDraftInput request, Principal actor) {
        return response(HttpStatus.CREATED, service.saveDraft(id, request.command(), actor.getName()));
    }
    @GetMapping("/drafts/latest")
    public ResponseEntity<CatalogEditorial.Draft> latest(@PathVariable String id) {
        return response(HttpStatus.OK, service.latestDraft(id));
    }
    @GetMapping("/drafts/{version}")
    public ResponseEntity<CatalogEditorial.Draft> draft(@PathVariable String id, @PathVariable long version) {
        return response(HttpStatus.OK, service.draft(id, version));
    }
    @PostMapping("/publications")
    public ResponseEntity<CatalogEditorial.Publication> publish(@PathVariable String id,
            @RequestBody PublishDraftInput request, Principal actor) {
        return response(HttpStatus.CREATED, service.publishDraft(id, request.command(), actor.getName()));
    }
    @GetMapping("/history")
    public ResponseEntity<CatalogEditorial.HistoryPage> history(@PathVariable String id,
            @RequestParam(defaultValue="10") int limit, @RequestParam(required=false) String cursor) {
        return response(HttpStatus.OK, service.history(id, limit, cursor));
    }
    private static <T> ResponseEntity<T> response(HttpStatus status, T body) {
        return ResponseEntity.status(status).cacheControl(CacheControl.noStore()).body(body);
    }
}
