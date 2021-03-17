package de.armbrust.planz.service;

import de.armbrust.planz.db.ProductMongoDb;
import de.armbrust.planz.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductMongoDb productMongoDb;

    public ProductService(ProductMongoDb productMongoDb) {
        this.productMongoDb = productMongoDb;
    }

    public List<Product> listProducts() {
        return productMongoDb.findAll();
    }

    public Optional<Product> addProduct(Product product) {
        if (!productMongoDb.existsById(product.getSku())) {
            productMongoDb.save(product);
            return Optional.of(product);
        }
        return Optional.empty();
    }

}
