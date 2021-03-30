package de.armbrust.planz.service;

import com.amazon.spapi.documents.exception.CryptoException;
import com.amazon.spapi.documents.exception.HttpResponseException;
import com.amazon.spapi.documents.exception.MissingCharsetException;
import de.armbrust.planz.amazonapi.AmazonApiHead;
import de.armbrust.planz.db.ProductMongoDb;
import de.armbrust.planz.model.Inventory;
import de.armbrust.planz.model.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductMongoDb productMongoDb;
    private final AmazonApiHead amazonApiHead;
    private final LocalFileReader localFileReader;

    public ProductService(ProductMongoDb productMongoDb, AmazonApiHead amazonApiHead, LocalFileReader localFileReader) {
        this.productMongoDb = productMongoDb;
        this.amazonApiHead = amazonApiHead;
        this.localFileReader = localFileReader;
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

    public void saveProductOnlyIfNotPresent (Product product) {
        if(!productMongoDb.findById(product.getSku()).isPresent()) {
            productMongoDb.save(product);
        }
    }

    public void initializeProductsOnDb() throws CryptoException, MissingCharsetException, HttpResponseException, IOException {
        List<Product> productsFromApi = amazonApiHead.getProductsFromApiReport();
        productsFromApi.forEach(product -> saveProductOnlyIfNotPresent(product));
    }

    public void findProductAndAddInventory(Inventory inventory) {
        Optional<Product> product = productMongoDb.findById(inventory.getSku());
        if (product.isPresent()) {
            ArrayList<Inventory> currentInventory = product.get().getInventory();
            if (!currentInventory.contains(inventory)) {
                currentInventory.add(inventory);
            }
            Product updatedProduct = product.get().toBuilder().inventory(currentInventory).build();
            productMongoDb.save(updatedProduct);
        }
    }

    public void updateInventoryData() {
        List<Inventory> inventoryList = localFileReader.getInventoryFromLocalReport();
        inventoryList.forEach(inventory -> findProductAndAddInventory(inventory));
    }

}