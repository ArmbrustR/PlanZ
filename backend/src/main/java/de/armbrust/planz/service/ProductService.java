package de.armbrust.planz.service;

import com.amazon.spapi.documents.exception.CryptoException;
import com.amazon.spapi.documents.exception.HttpResponseException;
import com.amazon.spapi.documents.exception.MissingCharsetException;
import de.armbrust.planz.amazonapi.AmazonApiHead;
import de.armbrust.planz.db.ProductMongoDb;
import de.armbrust.planz.model.Inventory;
import de.armbrust.planz.model.InventoryDto;
import de.armbrust.planz.model.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@Service
public class ProductService {

    private final ProductMongoDb productMongoDb;
    private final AmazonApiHead amazonApiHead;
    private final FileReaderInventory fileReaderInventory;

    public ProductService(ProductMongoDb productMongoDb, AmazonApiHead amazonApiHead, FileReaderInventory fileReaderInventory) {
        this.productMongoDb = productMongoDb;
        this.amazonApiHead = amazonApiHead;
        this.fileReaderInventory = fileReaderInventory;
    }

    public List<Product> listProducts() {
        return productMongoDb.findAll();
    }

    public List<String> getAsinListAfterCleaningDbFromProductsWithNoStock() {
        List<Product> products = productMongoDb.findAll();
        products.stream().forEach(product -> {
            if (product.getInventory().isEmpty()) {
                productMongoDb.delete(product);
            }
        });

        List<Product> updatedProductsList = productMongoDb.findAll();
        List<String> allAsins = updatedProductsList.stream().map(product -> product.getAsin()).distinct().collect(Collectors.toList());

        return allAsins;
    }

    public Map<String, Integer> getInventoryGroupedByDateAndAsin(List<Inventory> allInventoryItems) {
        Map<String, Integer> inventoryMap = new HashMap<>();

        allInventoryItems.stream().forEach(inventory -> {
            Integer sum = inventoryMap.get(inventory.getDate());
            Integer updatedAmount = sum != null ? sum + parseInt(inventory.getAmount()) : parseInt(inventory.getAmount());
            inventoryMap.put(inventory.getDate(), updatedAmount);
        });

        return inventoryMap;
    }


    public List<InventoryDto> getInventoryDtoForOneAsin(String asin) {
        List<Product> AllProductsOfOneAsin = productMongoDb.findAllByAsin(asin);

        List<Inventory> allInventoryItems = AllProductsOfOneAsin.stream().map(product ->
                product.getInventory()).flatMap(inventories -> inventories.stream()).collect(Collectors.toList());

        Map<String, Integer> inventoryItemsGroupedByDate = getInventoryGroupedByDateAndAsin(allInventoryItems);

        List<InventoryDto> inventoryDtoList = inventoryItemsGroupedByDate.entrySet()
                .stream().map(entry -> InventoryDto.builder()
                        .date(entry.getKey())
                        .amount(entry.getValue())
                        .build()).collect(Collectors.toList());

        List<InventoryDto> sortedInventoryList = sortInventoryDtoListByDate(inventoryDtoList);

        return sortedInventoryList;
    }

    public List<InventoryDto> getInventoryDtoForOneAsinFrontendFormat(String asin) {
        List<InventoryDto> inventoryDtoList = getInventoryDtoForOneAsin(asin);
        DateFormatHelper dateFormatHelper = new DateFormatHelper();

        List<InventoryDto> InventoryDtoWithParsedDate = inventoryDtoList.stream().map(inventoryDto -> {
            String parsedDate = null;
            try {
                parsedDate = dateFormatHelper.parseDateFromDDMMYYYYtoUTC(inventoryDto.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            inventoryDto.setDate(parsedDate);
            return inventoryDto;
        }).collect(Collectors.toList());

        return InventoryDtoWithParsedDate;
    }

    public List<InventoryDto> sortInventoryDtoListByDate(List<InventoryDto> inventoryDtoList) {
        inventoryDtoList.sort((InventoryDto o1, InventoryDto o2) -> {
            try {
                Date date1 = DateFormat.getDateInstance().parse(o1.getDate());
                Date date2 = DateFormat.getDateInstance().parse(o2.getDate());
                return date1.compareTo(date2);
            } catch (ParseException e) {
                return 0;
            }
        });
        return inventoryDtoList;
    }


    public Optional<Product> addProduct(Product product) {
        if (!productMongoDb.existsById(product.getSku())) {
            productMongoDb.save(product);
            return Optional.of(product);
        }
        return Optional.empty();
    }

    public void saveProductOnlyIfActiveAndNotInDb(Product product) {
        if (productMongoDb.findById(product.getSku()).isEmpty()) {
            productMongoDb.save(product);
        }
    }

    public void initializeProductsOnDb() throws CryptoException, MissingCharsetException, HttpResponseException, IOException {
        List<Product> productsFromApi = amazonApiHead.getProductsFromApiReport();
        productsFromApi.forEach(product -> saveProductOnlyIfActiveAndNotInDb(product));
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
        List<Inventory> inventoryList = fileReaderInventory.getInventoryFromLocalReport();
        inventoryList.forEach(inventory -> findProductAndAddInventory(inventory));
    }

    public String getTitleByAsin(String asin) {
        return productMongoDb.findFirstByAsin(asin).getTitle();
    }

}
