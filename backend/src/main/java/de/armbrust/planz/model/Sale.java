package de.armbrust.planz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Sale {

    private String asin;
    private String date;
    private Integer quantity;

}
