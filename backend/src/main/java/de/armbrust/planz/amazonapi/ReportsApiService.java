package de.armbrust.planz.amazonapi;

import com.amazon.SellingPartnerAPIAA.AWSAuthenticationCredentials;
import com.amazon.SellingPartnerAPIAA.AWSAuthenticationCredentialsProvider;
import com.amazon.SellingPartnerAPIAA.LWAAuthorizationCredentials;
import com.amazon.sellingpartner.ApiException;
import com.amazon.sellingpartner.api.ReportsApi;
import com.amazon.sellingpartner.model.*;
import de.armbrust.planz.security.AppUser;
import de.armbrust.planz.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
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

    public ReportsApi BuildReportsApi(AWSAuthenticationCredentials awsAuthenticationCredentials, LWAAuthorizationCredentials lwaAuthorizationCredentials, AWSAuthenticationCredentialsProvider awsAuthenticationCredentialsProvider) {

        ReportsApi reportsApi = new ReportsApi.Builder()
                .awsAuthenticationCredentials(awsAuthenticationCredentials)
                .lwaAuthorizationCredentials(lwaAuthorizationCredentials).awsAuthenticationCredentialsProvider(awsAuthenticationCredentialsProvider)
                .endpoint("https://sellingpartnerapi-eu.amazon.com")
                .build();

        return reportsApi;
    }

    public ReportsApi getReportsApi() {
        String mainAppUserId = "Rafael";
        AppUser mainAppUserDetails = appUserService.findAppUserInAppUserDb(mainAppUserId);
        AWSAuthenticationCredentials awsAuthenticationCredentials = authBuilderService.getAwsAuthenticationCredentials(mainAppUserDetails);
        LWAAuthorizationCredentials lwaAuthorizationCredentials = authBuilderService.getLwaAuthorizationCredentials(mainAppUserDetails);
        AWSAuthenticationCredentialsProvider awsAuthenticationCredentialsProvider = authBuilderService.getAwsAuthenticationCredentialsProvider(mainAppUserDetails);

        ReportsApi reportsApi = BuildReportsApi(awsAuthenticationCredentials, lwaAuthorizationCredentials, awsAuthenticationCredentialsProvider);
        return reportsApi;
    }

    public CreateReportSpecification getReportSpecifications(String reportsType) {
        List<String> marketplaceList = new ArrayList<String>();
        marketplaceList.add("A1PA6795UKMFR9");

        OffsetDateTime startTime = OffsetDateTime.parse("2021-01-01T12:00:00+03:30");
        OffsetDateTime endTime = OffsetDateTime.parse("2021-01-03T12:00:00+03:30");

        CreateReportSpecification createdReportSpecification = new CreateReportSpecification();
        createdReportSpecification.setMarketplaceIds(marketplaceList);
        createdReportSpecification.setReportType(reportsType);
        createdReportSpecification.setDataStartTime(startTime);
        createdReportSpecification.setDataEndTime(endTime);

        return createdReportSpecification;
    }

    public String createReportAndGetReportID(String reportsType) {
        ReportsApi reportsApi = getReportsApi();
        CreateReportSpecification reportSpecification = getReportSpecifications(reportsType);

        try {
            String reportId = reportsApi.createReport(reportSpecification).getPayload().getReportId();
            return reportId;

        } catch (ApiException e) {
            throw new RuntimeException("Error in createReportAndGetReportID", e);
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
            throw new RuntimeException("Error in getDownloadInformationsForReport", e);
        }
    }
}

