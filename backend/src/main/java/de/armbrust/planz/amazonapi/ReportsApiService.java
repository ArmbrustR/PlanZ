package de.armbrust.planz.amazonapi;

import com.amazon.SellingPartnerAPIAA.AWSAuthenticationCredentials;
import com.amazon.SellingPartnerAPIAA.LWAAuthorizationCredentials;
import com.amazon.sellingpartner.ApiException;
import com.amazon.sellingpartner.api.ReportsApi;
import com.amazon.sellingpartner.model.*;
import de.armbrust.planz.security.AppUser;
import de.armbrust.planz.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReportsApiService {

    public AppUserService appUserService;
    public DownloadAndDecryptService downloadAndDecryptService;
    public ReportParsingService reportParsingService;
    public AuthBuilderService authBuilderService;

    @Autowired
    public ReportsApiService(AppUserService appUserService, DownloadAndDecryptService downloadAndDecryptService, ReportParsingService reportParsingService, AuthBuilderService authBuilderService) {
        this.appUserService = appUserService;
        this.downloadAndDecryptService = downloadAndDecryptService;
        this.reportParsingService = reportParsingService;
        this.authBuilderService = authBuilderService;
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
        AWSAuthenticationCredentials awsAuthenticationCredentials = authBuilderService.getAwsAuthenticationCredentials(mainAppUserDetails);
        LWAAuthorizationCredentials lwaAuthorizationCredentials = authBuilderService.getLwaAuthorizationCredentials(mainAppUserDetails);

        ReportsApi reportsApi = BuildReportsApi(awsAuthenticationCredentials, lwaAuthorizationCredentials);

        return reportsApi;
    }

    public CreateReportSpecification getReportSpecifications() {
        List<String> marketplaceList = new ArrayList<String>();
        marketplaceList.add("A1PA6795UKMFR9");

        CreateReportSpecification createdReportSpecification = new CreateReportSpecification();
        createdReportSpecification.setMarketplaceIds(marketplaceList);
        createdReportSpecification.setReportType("GET_MERCHANT_LISTINGS_ALL_DATA");

        return createdReportSpecification;
    }

    public String createReportAndGetReportID(ReportsApi reportsApi) {
        CreateReportSpecification reportSpecification = getReportSpecifications();

        try {
            String reportId = reportsApi.createReport(reportSpecification).getPayload().getReportId();
            return reportId;

        } catch (ApiException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    public ReportDocument getDownloadInformationsForReport(String reportID, ReportsApi reportsApi) {
        try {
            Report reportDocumentPayload = reportsApi.getReport(reportID).getPayload();

            while (reportDocumentPayload.getProcessingStatus() != Report.ProcessingStatusEnum.DONE) {
                Thread.sleep(2000);
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
}

