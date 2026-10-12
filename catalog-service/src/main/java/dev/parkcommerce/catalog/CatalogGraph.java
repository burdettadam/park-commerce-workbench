package dev.parkcommerce.catalog;
import dev.parkcommerce.core.Catalog;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.*;
@Controller
public class CatalogGraph {
    private final Catalog.Service service;
    public CatalogGraph(Catalog.Service service){this.service=service;}
    @QueryMapping public List<Catalog.Product> products(){return service.products();}
    @SchemaMapping(typeName="Product",field="revision") public String revision(Catalog.Product product){return Long.toString(product.revision());}
    @QueryMapping public Catalog.Product product(@Argument String id){return service.latest(id);}
}
