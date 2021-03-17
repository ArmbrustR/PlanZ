package de.armbrust.planz.service;

import de.armbrust.planz.db.ProductMongoDb;
import de.armbrust.planz.model.Product;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private final ProductMongoDb productMongoDb = mock(ProductMongoDb.class);
    private final ProductService productService = new ProductService(productMongoDb);

    @Test
    @DisplayName("List Products should return list from DB")
    public void listProducts() {
        //GIVEN
        when(productMongoDb.findAll()).thenReturn(List.of(
                Product.builder()
                        .sku("123")
                        .title("Product1")
                        .asin("ABC")
                        .build(),
                Product.builder()
                        .sku("456")
                        .title("Product2")
                        .asin("EFG")
                        .build())
        );
        //WHEN
        List<Product> products = productService.listProducts();

        //THEN
        assertThat(products, containsInAnyOrder(
                Product.builder()
                        .sku("123")
                        .title("Product1")
                        .asin("ABC")
                        .build(),
                Product.builder()
                        .sku("456")
                        .title("Product2")
                        .asin("EFG")
                        .build()));
    }


    @Test
    @DisplayName("A not existing product should be added to db")
    public void testAddNotExistingProduct() {
        //GIVEN
        Optional<Product> OptionalOfProductToAdd = Optional.of(Product.builder()
                .sku("456")
                .title("Product2")
                .asin("EFG")
                .build());

        when(productMongoDb.existsById(OptionalOfProductToAdd.get().getSku())).thenReturn(false);

        //WHEN
        Optional<Product> actual = productService.addProduct(OptionalOfProductToAdd.get());

        //THEN
        verify(productMongoDb).save(OptionalOfProductToAdd.get());
        assertTrue(actual.isPresent());
        assertThat(actual, Matchers.is(OptionalOfProductToAdd));
    }

    @Test
    @DisplayName("A existing product should be NOT added to db")
    public void testAddExistingProduct() {
        //GIVEN
        Optional<Product> OptionalOfProductToAdd = Optional.of(Product.builder()
                .sku("456")
                .title("Product2")
                .asin("EFG")
                .build());

        when(productMongoDb.existsById(OptionalOfProductToAdd.get().getSku())).thenReturn(true);

        //WHEN
        Optional<Product> actual = productService.addProduct(OptionalOfProductToAdd.get());

        //THEN
        assertTrue(actual.isEmpty());
        verify(productMongoDb, never()).save(any());

    }
}