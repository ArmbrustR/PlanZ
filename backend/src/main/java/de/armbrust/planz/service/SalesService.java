package de.armbrust.planz.service;

import de.armbrust.planz.db.SaleMongoDb;
import de.armbrust.planz.model.Sale;
import org.javatuples.Pair;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalesService {

    private final FileReaderSales fileReaderSales;
    private final SaleMongoDb saleMongoDb;

    public SalesService(FileReaderSales fileReaderSales, SaleMongoDb saleMongoDb) {
        this.fileReaderSales = fileReaderSales;
        this.saleMongoDb = saleMongoDb;
    }

    public List<Sale> getSalesFromOneAsinSortedByDate(String asin) {
        List<Sale> salesFromAsinList = saleMongoDb.findAllByAsin(asin);

        salesFromAsinList.sort((Sale o1, Sale o2) -> {
            try {
                Date date1 = DateFormat.getDateInstance().parse(o1.getDate());
                Date date2 = DateFormat.getDateInstance().parse(o2.getDate());
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });
        return salesFromAsinList;
    }

    public List<Sale> getSalesFromOneAsinSortedByDateFrontendDateFormat(String asin) {
        List<Sale> salesFromAsinList = getSalesFromOneAsinSortedByDate(asin);
        DateFormatHelper dateFormatHelper = new DateFormatHelper();

        List<Sale> salesWithParsedDate = salesFromAsinList.stream().map(sale -> {
            String parsedDate = null;
            try {
                parsedDate = dateFormatHelper.parseDateFromDDMMYYYYtoUTC(sale.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sale.setDate(parsedDate);
            return sale;
        }).collect(Collectors.toList());

        return salesWithParsedDate;
    }


    public void saveSalesFromLocalFileToDb() {
        List<Sale> salesToSave = getSalesGroupedByAsinAndDate();

        salesToSave.stream().forEach(sale -> {
            if (!saleMongoDb.findAll().contains(sale)) {
                saleMongoDb.save(sale);
            }
        });
    }

    public List<Sale> getSalesGroupedByAsinAndDate() {
        List<Sale> saleList = fileReaderSales.getListWithSalesAsinBased();
        List<Sale> saleListGroupedByAsinAndDate = sumSalesPerAsinAndDate(saleList);

        return saleListGroupedByAsinAndDate;
    }

    public List<Sale> sumSalesPerAsinAndDate(List<Sale> salesList) {

        Map<Pair<String, String>, Integer> salesPerDayAndAsin = salesList.stream()
                .collect(Collectors.groupingBy(sale -> new Pair<>(sale.getDate(), sale.getAsin()), Collectors.summingInt(sale -> sale.getQuantity())));

        List<Sale> salesGroupedByAsinAndDay = new ArrayList<>();

        salesPerDayAndAsin.entrySet().stream().forEach(entry -> {
            Sale sale = Sale.builder()
                    .asin(entry.getKey().getValue1())
                    .quantity(entry.getValue())
                    .date(entry.getKey().getValue0())
                    .build();

            salesGroupedByAsinAndDay.add(sale);
        });

        return salesGroupedByAsinAndDay;
    }
}
