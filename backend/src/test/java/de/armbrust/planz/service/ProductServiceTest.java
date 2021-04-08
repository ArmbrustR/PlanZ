package de.armbrust.planz.service;

import com.amazon.spapi.documents.exception.CryptoException;
import com.amazon.spapi.documents.exception.HttpResponseException;
import com.amazon.spapi.documents.exception.MissingCharsetException;
import de.armbrust.planz.amazonapi.AmazonApiHead;
import de.armbrust.planz.db.ProductMongoDb;
import de.armbrust.planz.model.Inventory;
import de.armbrust.planz.model.Product;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private final ProductMongoDb productMongoDb = mock(ProductMongoDb.class);
    private final AmazonApiHead amazonApiHead = mock(AmazonApiHead.class);
    private final LocalFileReader localFileReader = mock(LocalFileReader.class);
    private final ProductService productService = new ProductService(productMongoDb, amazonApiHead, localFileReader);


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
        Optional<Product> optionalOfProductToAdd = Optional.of(Product.builder()
                .sku("456")
                .title("Product2")
                .asin("EFG")
                .build());

        when(productMongoDb.existsById(optionalOfProductToAdd.get().getSku())).thenReturn(false);

        //WHEN
        Optional<Product> actual = productService.addProduct(optionalOfProductToAdd.get());

        //THEN
        verify(productMongoDb).save(optionalOfProductToAdd.get());
        assertTrue(actual.isPresent());
        assertThat(actual, Matchers.is(optionalOfProductToAdd));
    }

    @Test
    @DisplayName("An existing product should NOT be added to db")
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

    @Test
    @DisplayName("UpdateProductDb should write multiple products to db, if products not available at db")
    public void testAddingListOfNotExistingProducts() throws CryptoException, MissingCharsetException, HttpResponseException, IOException {
        //GIVEN
        Product product1 = Product.builder()
                .sku("123")
                .title("Product1")
                .asin("ABC")
                .build();
        Product product2 = Product.builder()
                .sku("456")
                .title("Product2")
                .asin("EFG")
                .build();

        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);

        when(amazonApiHead.getProductsFromApiReport()).thenReturn(products);

        //WHEN
        productService.initializeProductsOnDb();

        //THEN
        verify(productMongoDb).save(product1);
        verify(productMongoDb).save(product2);
    }

    @Test
    @DisplayName("if product available the inventory should be add to inventory array")
    public void IfProductAvailibeInventoryShouldAdded() {
        //GIVEN
        Product product1 = Product.builder()
                .sku("123")
                .title("Product1")
                .asin("ABC")
                .inventory(new ArrayList<>())
                .build();

        Inventory inventory1 = Inventory.builder()
                .warehouse("AnyWarehouse")
                .condition("NEW")
                .country("DE")
                .amount("23")
                .date("2021-02-25")
                .sku("123")
                .build();

        when(productMongoDb.findById(inventory1.getSku())).thenReturn(Optional.of(product1));

        // WHEN
        productService.findProductAndAddInventory(inventory1);

        // THEN
        verify(productMongoDb).findById("123");
        verify(productMongoDb).save(product1);
        assertThat(product1.getInventory(), containsInAnyOrder(inventory1));
    }


    @Test
    @DisplayName("if product is not available nothing should happen")
    public void IfProductNotAvailibeNothingHappens() {
        //GIVEN
        Product product1 = Product.builder()
                .sku("123")
                .title("Product1")
                .asin("ABC")
                .inventory(new ArrayList<>())
                .build();

        Inventory inventory1 = Inventory.builder()
                .warehouse("AnyWarehouse")
                .condition("NEW")
                .country("DE")
                .amount("23")
                .date("2021-02-25")
                .sku("123")
                .build();

        when(productMongoDb.findById(inventory1.getSku())).thenReturn(Optional.empty());

        // WHEN
        productService.findProductAndAddInventory(inventory1);

        // THEN
        verify(productMongoDb, never()).save(any());
    }

    @Test
    @DisplayName("if inventory DataPoint is already available, it not be added")
    public void IfProductHasInventoryPointAlreadyItShouldNotBeAdded() {
        //GIVEN
        Inventory inventory1 = Inventory.builder()
                .warehouse("AnyWarehouse")
                .condition("NEW")
                .country("DE")
                .amount("23")
                .date("2021-02-25")
                .sku("123")
                .build();

        ArrayList<Inventory> inventoryArray = new ArrayList<>();
        inventoryArray.add(inventory1);

        Product product1 = Product.builder()
                .sku("123")
                .title("Product1")
                .asin("ABC")
                .inventory(inventoryArray)
                .build();

        when(productMongoDb.findById(inventory1.getSku())).thenReturn(Optional.of(product1));

        // WHEN
        productService.findProductAndAddInventory(inventory1);

        // THEN
        assertThat(product1.getInventory().toString().length(), is(inventoryArray.toString().length()));
    }


}