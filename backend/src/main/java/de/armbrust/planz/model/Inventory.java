package de.armbrust.planz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    private String dateOfReport;
    private String asin;
    private String sku;
    private String amount;
    private String warehouse;
    private String condition;
    private String country;

}
