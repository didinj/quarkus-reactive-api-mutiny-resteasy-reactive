package com.djamware.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;

import com.djamware.model.Product;
import com.djamware.repository.ProductRepository;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductRepository repository;

    @GET
    @Operation(summary = "Get all products", description = "Returns a list of all products")
    public Multi<Product> getAll() {
        return repository.listAll()
                .onItem().transformToMulti(products -> Multi.createFrom().iterable(products));
    }

    @GET
    @Path("/{id}")
    public Uni<Response> getById(@PathParam("id") Long id) {
        return repository.findById(id)
                .onItem().ifNotNull().transform(product -> Response.ok(product).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND)::build);
    }

    @POST
    public Uni<Response> create(Product product) {
        return repository.persist(product)
                .map(p -> Response.status(Response.Status.CREATED).entity(p).build());
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> update(@PathParam("id") Long id, Product updated) {
        return repository.findById(id)
                .onItem().ifNotNull().transformToUni(product -> {
                    product.name = updated.name;
                    product.description = updated.description;
                    product.price = updated.price;
                    return repository.persist(product)
                            .replaceWith(Response.ok(product).build());
                })
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND)::build);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return repository.deleteById(id)
                .map(deleted -> deleted
                        ? Response.noContent().build()
                        : Response.status(Response.Status.NOT_FOUND).build());
    }
}
