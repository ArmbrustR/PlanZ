package de.armbrust.planz.amazonapi;

import com.amazon.spapi.documents.DownloadBundle;
import com.amazon.spapi.documents.exception.CryptoException;
import com.amazon.spapi.documents.exception.MissingCharsetException;
import de.armbrust.planz.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReportParsingService {

    public List<Product> readFileAndExtractProducts(DownloadBundle downloadBundle) throws MissingCharsetException, IOException, CryptoException {
        BufferedReader bufferedReader = downloadBundle.newBufferedReader();

        String line;
        List<Product> products = new ArrayList<Product>();

        while ((line = bufferedReader.readLine()) != null) {
            String[] content = line.split("\t");

            Product tempProduct = new Product().builder()
                    .sku(content[3])
                    .title(content[0])
                    .asin(content[16])
                    .itemDescription(content[1])
                    .imageUrl(content[7])
                    .pendingQuantity(content[25])
                    .price(content[4])
                    .status(content[28])
                    .build();

            products.add(tempProduct);
        }

        downloadBundle.close();
        return products;
    }
}
