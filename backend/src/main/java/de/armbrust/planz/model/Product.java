package de.armbrust.planz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "products")
public class Product {

    @Id
    private String sku;
    private String title;
    private String asin;
    private String itemDescription;
    private String imageUrl;
    private String pendingQuantity;
    private String price;
    private String status;
    private ArrayList<Inventory> inventory;

}
