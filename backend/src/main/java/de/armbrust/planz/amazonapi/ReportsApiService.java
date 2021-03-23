package de.armbrust.planz.amazonapi;

import com.amazon.SellingPartnerAPIAA.AWSAuthenticationCredentials;
import com.amazon.SellingPartnerAPIAA.LWAAuthorizationCredentials;
import com.amazon.sellingpartner.ApiException;
import com.amazon.sellingpartner.api.ReportsApi;
import com.amazon.spapi.documents.*;
import com.amazon.sellingpartner.model.*;
import com.amazon.spapi.documents.exception.CryptoException;
import com.amazon.spapi.documents.exception.HttpResponseException;
import com.amazon.spapi.documents.exception.MissingCharsetException;
import com.amazon.spapi.documents.impl.AESCryptoStreamFactory;
import com.amazon.spapi.documents.impl.OkHttpTransferClient;
import de.armbrust.planz.model.Product;
import de.armbrust.planz.security.AppUser;
import de.armbrust.planz.security.AppUserDb;
import de.armbrust.planz.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReportsApiService {

    public AppUserService appUserService;

    @Autowired
    public ReportsApiService(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    public ReportsApi BuildReportsApi(AWSAuthenticationCredentials awsAuthenticationCredentials, LWAAuthorizationCredentials lwaAuthorizationCredentials) {

        ReportsApi reportsApi = new ReportsApi.Builder()
                .awsAuthenticationCredentials(awsAuthenticationCredentials)
                .lwaAuthorizationCredentials(lwaAuthorizationCredentials)
                .endpoint("https://sellingpartnerapi-eu.amazon.com")
                .build();

        return reportsApi;
    }

    public ReportsApi getReportsApi() {
        String mainAppUserId = "Rafael";
        AppUser mainAppUserDetails = appUserService.findAppUserInAppUserDb(mainAppUserId);

        AWSAuthenticationCredentials awsAuthenticationCredentials = AWSAuthenticationCredentials.builder()
                .accessKeyId(mainAppUserDetails.getAccessKeyId())
                .secretKey(mainAppUserDetails.getSecretKey())
                .region(mainAppUserDetails.getRegion())
                .build();

        LWAAuthorizationCredentials lwaAuthorizationCredentials = LWAAuthorizationCredentials.builder()
                .clientId(mainAppUserDetails.getClientId())
                .clientSecret(mainAppUserDetails.getClientSecret())
                .refreshToken(mainAppUserDetails.getRefreshToken())
                .endpoint("https://api.amazon.com/auth/o2/token")
                .build();

        ReportsApi reportsApi = BuildReportsApi(awsAuthenticationCredentials, lwaAuthorizationCredentials);

        return reportsApi;
    }


    public String createReportAndGetReportID(ReportsApi reportsApi) {
        List<String> marketplaceList = new ArrayList<String>();
        marketplaceList.add("A1PA6795UKMFR9");

        CreateReportSpecification createReportSpecification = new CreateReportSpecification();
        createReportSpecification.setMarketplaceIds(marketplaceList);
        createReportSpecification.setReportType("GET_MERCHANT_LISTINGS_ALL_DATA");

        try {
            String reportId = reportsApi.createReport(createReportSpecification).getPayload().getReportId();
            return reportId;

        } catch (ApiException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    public ReportDocument getDownloadInformationsForReport(String reportID, ReportsApi reportsApi) {
        List<String> marketplaceList = new ArrayList<String>();
        marketplaceList.add("A1PA6795UKMFR9");

        CreateReportSpecification createReportSpecification = new CreateReportSpecification();
        createReportSpecification.setMarketplaceIds(marketplaceList);
        createReportSpecification.setReportType("GET_MERCHANT_LISTINGS_ALL_DATA");

        try {
            Report reportDocumentPayload = reportsApi.getReport(reportID).getPayload();

            while (reportDocumentPayload.getProcessingStatus() != Report.ProcessingStatusEnum.DONE) {
                Thread.sleep(1000);
                reportDocumentPayload = reportsApi.getReport(reportID).getPayload();
            }

            String reportDocumentId = reportDocumentPayload.getReportDocumentId();
            ReportDocument reportsDocumentDownloadInformations = reportsApi.getReportDocument(reportDocumentId).getPayload();

            return reportsDocumentDownloadInformations;

        } catch (ApiException | InterruptedException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    public List<Product> getListOfProductsFromDownloadedReport(ReportDocument reportDownloadInformations) throws IOException, HttpResponseException, CryptoException, MissingCharsetException {
        OkHttpTransferClient okHttpTransferClient = new OkHttpTransferClient.Builder().build();

        DownloadHelper downloadHelper = new DownloadHelper.Builder()
                .withHttpTransferClient(okHttpTransferClient)
                .withTmpFileDirectory(null)
                .build();

        String secretKey = reportDownloadInformations.getEncryptionDetails().getKey();
        String initializationVector = reportDownloadInformations.getEncryptionDetails().getInitializationVector();

        AESCryptoStreamFactory aesCryptoStreamFactory = new AESCryptoStreamFactory
                .Builder(secretKey,
                initializationVector)
                .build();

        DownloadSpecification downloadSpecification = new DownloadSpecification.Builder(aesCryptoStreamFactory, reportDownloadInformations.getUrl())
                .withCompressionAlgorithm(null)
                .build();

        DownloadBundle downloadedFileBundle = downloadHelper.download(downloadSpecification);
        BufferedReader bufferedReader = downloadedFileBundle.newBufferedReader();

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

        downloadedFileBundle.close();
        return products;
    }

    public List<Product> getProductListFromAmazonApiReport() throws CryptoException, MissingCharsetException, HttpResponseException, IOException {
        ReportsApi reportsApi = getReportsApi();
        String reportsId = createReportAndGetReportID(reportsApi);
        ReportDocument reportDownloadInformations = getDownloadInformationsForReport(reportsId, reportsApi);
        List<Product> products = getListOfProductsFromDownloadedReport(reportDownloadInformations);

        return products;
    }


}

