package de.armbrust.planz.controller;

import com.amazon.sellingpartner.api.ReportsApi;
import com.amazon.sellingpartner.model.ReportDocument;
import de.armbrust.planz.amazonapi.SellersApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.crypto.SecretKey;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("report")
public class ReportController {

    private final SellersApiService sellersApiService;

    public ReportController(SellersApiService sellersApiService) {
        this.sellersApiService = sellersApiService;
    }


    @GetMapping
    public String getReportContent() {
        ReportsApi reportsApi = sellersApiService.getReportsApi();

        String reportsId = sellersApiService.createReportAndGetReportID(reportsApi);

        ReportDocument reportDownloadInformations = sellersApiService.getDownloadInformationsForReport(reportsId, reportsApi);

        File downloadedReport = sellersApiService.downloadReportDocument(reportDownloadInformations);

        String key = KeyFreportDownloadInformations.getEncryptionDetails().getKey();

        File outputFile = new File("outputFile.csv");

        String decryptedContent = sellersApiService.decryptFile(
                "AES",
                reportDownloadInformations.getEncryptionDetails().getKey(),
                reportDownloadInformations.getEncryptionDetails().getInitializationVector(),
                downloadedReport, outputFile
                );

        return decryptedContent;

    }


}
