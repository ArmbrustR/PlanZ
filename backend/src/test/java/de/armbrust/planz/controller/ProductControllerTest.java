package de.armbrust.planz.controller;

import de.armbrust.planz.db.ProductMongoDb;
import de.armbrust.planz.model.Product;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ProductMongoDb productMongoDb;

    @BeforeEach
    public void setup() {
        productMongoDb.deleteAll();
    }

    @Test
    @DisplayName("Get api/product should return a list of products")
    public void testGetMappingReturnsListOfProducts() {

        //GIVEN
        productMongoDb.save(Product.builder()
                .sku("123")
                .title("Product1")
                .asin("ABC")
                .build());
        productMongoDb.save(Product.builder()
                .sku("456")
                .title("Product2")
                .asin("EFG")
                .build());

        //WHEN
        ResponseEntity<Product[]> response = testRestTemplate.getForEntity("/api/product", Product[].class);

        //THEN
        assertThat(response.getStatusCode(), Matchers.is(HttpStatus.OK));
        assertThat(response.getBody(), arrayContainingInAnyOrder(
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

}