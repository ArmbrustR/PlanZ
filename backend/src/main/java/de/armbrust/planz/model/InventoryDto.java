package de.armbrust.planz.model;

import com.amazon.sellingpartner.model.Amount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryDto {

private String date;
private Integer amount;

}
