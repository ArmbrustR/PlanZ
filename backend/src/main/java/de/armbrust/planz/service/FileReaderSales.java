package de.armbrust.planz.service;


import de.armbrust.planz.model.Sale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

@Service
@Slf4j
public class FileReaderSales {

    public Path getLocalFilePath() {
        Path path = Paths.get("/Users/rafael.armbrust/Java-Bootcamp/Abschlussprojekt/PlanZ/667451018719.txt");
        return path;
    }

    public List<Sale> getListWithSalesAsinBased() {
        Path path = getLocalFilePath();

        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String line;
            String[] content = {};
            List<Sale> salesList = new ArrayList<>();

            line = bufferedReader.readLine();
            line = null; // ignore Headline

            while ((line = bufferedReader.readLine()) != null) {
                content = line.split("\t");

                DateFormatHelper dateFormatHelper = new DateFormatHelper();
                String simpleDate = dateFormatHelper.parseDateToSimpleStringDate(content[2]);

                Sale sale = Sale.builder()
                        .asin(content[12])
                        .date(simpleDate)
                        .quantity(parseInt(content[14]))
                        .build();
                salesList.add(sale);
            }
            return salesList;

        } catch (IOException e) {
            throw new RuntimeException("Error in ReadLocalFile", e);
        }

    }

}
