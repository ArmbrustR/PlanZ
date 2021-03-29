package de.armbrust.planz.amazonapi;

import com.amazon.sellingpartner.api.ReportsApi;
import com.amazon.sellingpartner.model.ReportDocument;
import com.amazon.spapi.documents.DownloadBundle;
import de.armbrust.planz.model.Product;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AmazonApiHead {

    public final ReportParsingService reportParsingService;
    public final DownloadAndDecryptService downloadAndDecryptService;
    public final ReportsApiService reportsApiService;

    public AmazonApiHead(ReportParsingService reportParsingService, DownloadAndDecryptService downloadAndDecryptService, ReportsApiService reportsApiService) {
        this.reportParsingService = reportParsingService;
        this.downloadAndDecryptService = downloadAndDecryptService;
        this.reportsApiService = reportsApiService;
    }

    public List<Product> getProductsFromApiReport() {
        ReportsApi reportsApi = reportsApiService.getReportsApi();
        String reportsId = reportsApiService.createReportAndGetReportID("GET_MERCHANT_LISTINGS_ALL_DATA");
        ReportDocument reportDownloadInformations = reportsApiService.getDownloadInformationsForReport(reportsId, reportsApi);
        DownloadBundle downloadBundle = downloadAndDecryptService.getDecryptedDownloadBundle(reportDownloadInformations);
        List<Product> products = reportParsingService.readFileAndExtractProducts(downloadBundle);

        return products;
    }

    public List<Product> getCurrentInventoryFromApiReport() {
        ReportsApi reportsApi = reportsApiService.getReportsApi();
        String reportsId = reportsApiService.createReportAndGetReportID("GET_FBA_FULFILLMENT_MONTHLY_INVENTORY_DATA");
        ReportDocument reportDownloadInformations = reportsApiService.getDownloadInformationsForReport(reportsId, reportsApi);
        DownloadBundle downloadBundle = downloadAndDecryptService.getDecryptedDownloadBundle(reportDownloadInformations);
        List<Product> products = reportParsingService.readFileAndExtractProducts(downloadBundle);

        return products;
    }



}
