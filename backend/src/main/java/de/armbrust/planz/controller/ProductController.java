package de.armbrust.planz.controller;

import com.amazon.spapi.documents.exception.CryptoException;
import com.amazon.spapi.documents.exception.HttpResponseException;
import com.amazon.spapi.documents.exception.MissingCharsetException;
import de.armbrust.planz.model.AsinDto;
import de.armbrust.planz.service.AsinService;
import de.armbrust.planz.service.ProductService;
import de.armbrust.planz.service.SalesService;
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
    private final SalesService salesService;
    private final AsinService asinService;

    @Autowired
    public ProductController(ProductService productService, SalesService salesService, AsinService asinService) {
        this.productService = productService;
        this.salesService = salesService;
        this.asinService = asinService;
    }

    @GetMapping("update")
    public void updateDatabaseFromReportsApi() {
        try {
            productService.initializeProductsOnDb();
        } catch (MissingCharsetException | IOException | HttpResponseException | CryptoException e) {
            throw new RuntimeException("Error in updateDatabaseFromReportsApi", e);
        }
    }

    @GetMapping("updateSales")
    public void updateSalesDbFromLocalFile() {
        salesService.saveSalesFromLocalFileToDb();
    }

    @GetMapping("updateInventory")
    public void updateInventoryFromLocalFile() {
        productService.updateInventoryData();
    }

    @GetMapping("asins")
    public List<AsinDto> getAsinDtoList() {
        return asinService.getProductsAsinBased();
    }

}
