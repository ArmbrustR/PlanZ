package de.armbrust.planz.amazonapi;

import com.amazon.sellingpartner.api.ReportsApi;
import com.amazon.sellingpartner.model.ReportDocument;
import com.amazon.spapi.documents.DownloadBundle;
import com.amazon.spapi.documents.exception.CryptoException;
import com.amazon.spapi.documents.exception.HttpResponseException;
import com.amazon.spapi.documents.exception.MissingCharsetException;
import de.armbrust.planz.model.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AmazonApiLogic {

    public final ReportParsingService reportParsingService;
    public final DownloadAndDecryptService downloadAndDecryptService;
    public final ReportsApiService reportsApiService;

    public AmazonApiLogic(ReportParsingService reportParsingService, DownloadAndDecryptService downloadAndDecryptService, ReportsApiService reportsApiService) {
        this.reportParsingService = reportParsingService;
        this.downloadAndDecryptService = downloadAndDecryptService;
        this.reportsApiService = reportsApiService;
    }

    public List<Product> getProductsFromApiReport() throws CryptoException, MissingCharsetException, HttpResponseException, IOException {
        ReportsApi reportsApi = reportsApiService.getReportsApi();
        String reportsId = reportsApiService.createReportAndGetReportID(reportsApi);
        ReportDocument reportDownloadInformations = reportsApiService.getDownloadInformationsForReport(reportsId, reportsApi);
        DownloadBundle downloadBundle = downloadAndDecryptService.getDecryptedDownloadBundle(reportDownloadInformations);
        List<Product> products = reportParsingService.readFileAndExtractProducts(downloadBundle);

        return products;
    }
}
