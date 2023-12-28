package de.armbrust.planz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "productsByAsin")
public class AsinDto {

    private String asin;
    private String title;
    private List<InventoryDto> inventory;
    private List<Sale> sales;
    private Integer expectedSales;
    private Integer differenceFromExpectedSalesToActualSales;

    public Integer calcMaxInventoryAmount() {
        return Collections.max(inventory, Comparator.comparing(i -> i.getAmount())).getAmount();
    }
}
