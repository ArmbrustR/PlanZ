package de.armbrust.planz.service;

import de.armbrust.planz.model.AsinDto;
import de.armbrust.planz.model.InventoryDto;
import de.armbrust.planz.model.Sale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AsinService {

    private final ProductService productService;
    private final SalesService salesService;

    @Autowired
    public AsinService(ProductService productService, SalesService salesService) {
        this.productService = productService;
        this.salesService = salesService;
    }

    public Integer getExpectedSalesForOneAsin(String asin) {
        List<Sale> salesToRemove = getListWithSalesToRemoveBecauseLowInventory(asin);
        List<Sale> salesFromAsinList = salesService.getAllSalesFromOneAsinGroupedByDate(asin);

        Integer countOfDays = salesFromAsinList.size();

        if (!salesToRemove.isEmpty()) {
            OptionalDouble averageSalesExpected = salesToRemove.stream().mapToInt(sale -> sale.getQuantity()).average();

            double expectedSales = averageSalesExpected.getAsDouble() * countOfDays;
            return (int) expectedSales;
        }
        return getActualSalesForOneAsin(asin);
    }

    public List<AsinDto> getProductsAsinBased() {
        List<String> allAsins = productService.getAsinListAfterCleaningDbFromProductsWithNoStock();

        List<AsinDto> asinDtoList = allAsins.stream().map(asin -> AsinDto.builder()
                .asin(asin)
                .inventory(productService.getInventoryDtoForOneAsinFrontendFormat(asin))
                .sales(salesService.getSalesFromOneAsinSortedByDateFrontendDateFormat(asin))
                .expectedSales(getExpectedSalesForOneAsin(asin))
                .differenceFromExpectedSalesToActualSales(getDifferenceFromExpectedSalesToActualSales(asin))
                .title(productService.getNameByAsin(asin))
                .build()).collect(Collectors.toList());

        List<AsinDto> updatedAsinDtoList = asinDtoList.stream().filter(asinDto -> asinDto.calcMaxInventoryAmount() >= 50).collect(Collectors.toList());
        List<AsinDto> asinDtoListSortedByMinInventory = productService.getAsinDtoListSortedMinInventory(updatedAsinDtoList);

        return asinDtoListSortedByMinInventory;
    }


    public List<Sale> getListWithSalesToRemoveBecauseLowInventory(String asin) {
        List<InventoryDto> inventoryDtoList = productService.getInventoryDtoForOneAsin(asin);
        List<InventoryDto> inventoryDtoWithLowInventory = inventoryDtoList.stream()
                .filter(inventory -> inventory.getAmount() < 10).collect(Collectors.toList());
        List<String> datesOfLowInventory = new ArrayList<>();

        inventoryDtoWithLowInventory.stream().forEach(inventoryDto -> datesOfLowInventory.add(inventoryDto.getDate()));

        List<Sale> allSalesByOneAsin = salesService.getAllSalesFromOneAsinGroupedByDate(asin);
        List<Sale> salesToRemoveList = new ArrayList<>();

        if (!datesOfLowInventory.isEmpty()) {
            datesOfLowInventory.stream().forEach(date -> {
                allSalesByOneAsin.stream().forEach(sale -> {
                    if (sale.getDate().equals(date)) {
                        salesToRemoveList.add(sale);
                    }
                });
            });
            if (!salesToRemoveList.isEmpty()) {
                salesToRemoveList.stream().forEach(saleToRemove -> allSalesByOneAsin.remove(saleToRemove));
                return allSalesByOneAsin;
            }

        }
        return new ArrayList<>();
    }

    public Integer getActualSalesForOneAsin(String asin) {
        List<Sale> salesFromAsinList = salesService.getAllSalesFromOneAsinGroupedByDate(asin);

        return salesFromAsinList.stream().mapToInt(sale -> sale.getQuantity()).sum();
    }

    public Integer getDifferenceFromExpectedSalesToActualSales(String asin) {
        Integer actualSales = getActualSalesForOneAsin(asin);
        Integer expectedSales = getExpectedSalesForOneAsin(asin);

        return expectedSales - actualSales;
    }

}
