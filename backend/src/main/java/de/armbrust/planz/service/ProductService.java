package de.armbrust.planz.service;

import com.amazon.spapi.documents.exception.CryptoException;
import com.amazon.spapi.documents.exception.HttpResponseException;
import com.amazon.spapi.documents.exception.MissingCharsetException;
import de.armbrust.planz.amazonapi.AmazonApiLogic;
import de.armbrust.planz.amazonapi.ReportsApiService;
import de.armbrust.planz.db.ProductMongoDb;
import de.armbrust.planz.model.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductMongoDb productMongoDb;
    private final AmazonApiLogic amazonApiLogic;

    public ProductService(ProductMongoDb productMongoDb, AmazonApiLogic amazonApiLogic) {
        this.productMongoDb = productMongoDb;
        this.amazonApiLogic = amazonApiLogic;
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

    public void UpdateProductDb() throws CryptoException, MissingCharsetException, HttpResponseException, IOException {
        List<Product> productsFromApi = amazonApiLogic.getProductsFromApiReport();
        productsFromApi.forEach(product -> productMongoDb.save(product));
    }

}