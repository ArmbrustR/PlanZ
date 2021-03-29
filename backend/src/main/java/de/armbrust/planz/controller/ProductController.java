package de.armbrust.planz.controller;

import com.amazon.spapi.documents.exception.CryptoException;
import com.amazon.spapi.documents.exception.HttpResponseException;
import com.amazon.spapi.documents.exception.MissingCharsetException;
import de.armbrust.planz.amazonapi.AmazonApiHead;
import de.armbrust.planz.amazonapi.ReportsApiService;
import de.armbrust.planz.model.Product;
import de.armbrust.planz.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/product")
public class ProductController {

    private final ProductService productService;
    private final AmazonApiHead amazonApiHead;

    @Autowired
    public ProductController(ProductService productService, AmazonApiHead amazonApiHead) {
        this.productService = productService;
        this.amazonApiHead = amazonApiHead;
    }

    @GetMapping
    public List<Product> listProducts() {
        return productService.listProducts();
    }

    @GetMapping("update")  // This method should be scheduled later (once a day) in an "scheduling class"
    public void updateDatabaseFromReportsApi () {
        try {
            productService.UpdateProductDb();
        } catch (MissingCharsetException | IOException | HttpResponseException | CryptoException e) {
            throw new RuntimeException("Error in updateDatabaseFromReportsApi", e);
        }
    }

    @GetMapping("inventory")
    public List<Product> getInventoryFromReportsApi () {
        List<Product> reportResponse = amazonApiHead.getCurrentInventoryFromApiReport();
        return reportResponse;
    }

}
