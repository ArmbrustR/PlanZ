package de.armbrust.planz.service;

import de.armbrust.planz.model.Inventory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FileReaderInventory {

    public Path getLocalFilePath() {
        Path path = Paths.get("/Users/rafael.armbrust/Java-Bootcamp/Abschlussprojekt/PlanZ/InventoryTextTabstop.txt");
        return path;
    }

    public List<Inventory> getInventoryFromLocalReport() {
        Path path = getLocalFilePath();

        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String line;
            String[] content = {};
            List<Inventory> inventoryList = new ArrayList<>();

            line = bufferedReader.readLine();
            line = null; // ignore Headline

            while ((line = bufferedReader.readLine()) != null) {
                content = line.split("\t");

                if (content.length == 7) {

                    Inventory inventory = Inventory.builder()
                            .dateOfReport(StringUtils.substring(content[0], 0, 10))
                            .asin(content[1])
                            .sku(content[2])
                            .amount(content[3])
                            .warehouse(content[4])
                            .condition(content[5])
                            .country(content[6])
                            .build();

                    inventoryList.add(inventory);

                }

            }
            return inventoryList;


        } catch (IOException e) {
            throw new RuntimeException("Error in ReadLocalFile", e);
        }
    }
}
