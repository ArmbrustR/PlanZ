package de.armbrust.planz.amazonapi;

import com.amazon.SellingPartnerAPIAA.AWSAuthenticationCredentials;
import com.amazon.SellingPartnerAPIAA.AWSAuthenticationCredentialsProvider;
import com.amazon.SellingPartnerAPIAA.LWAAuthorizationCredentials;
import com.amazon.sellingpartner.ApiException;
import com.amazon.sellingpartner.api.ReportsApi;
import com.amazon.sellingpartner.api.SellersApi;
import com.amazon.sellingpartner.model.*;
import de.armbrust.planz.security.AppUser;
import de.armbrust.planz.security.AppUserDb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.amazon.SellingPartnerAPIAA.ScopeConstants.SCOPE_NOTIFICATIONS_API;
import static com.amazon.SellingPartnerAPIAA.ScopeConstants.SCOPE_MIGRATION_API;

@Service
@Slf4j
public class SellersApiService {

    public AppUserDb appUserDb;
    private RestTemplate restTemplate;

    @Autowired
    public SellersApiService(AppUserDb appUserDb, RestTemplate restTemplate) {
        this.appUserDb = appUserDb;
        this.restTemplate = restTemplate;
    }

    public AppUser findAppUserInAppUserDb(String appUser) {
        if (appUserDb.findById(appUser).isPresent()) {
            return appUserDb.findById(appUser).get();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User" + appUser + " is not in database");
    }

    public ReportsApi getReportsApi() {
        String mainAppUserId = "Rafael";
        AppUser mainAppUserDetails = findAppUserInAppUserDb(mainAppUserId);

        String mainAppUserId = "Rafael";
        AppUser mainAppUserDetails = findAppUserInAppUserDb(mainAppUserId);

        AWSAuthenticationCredentials awsAuthenticationCredentials = AWSAuthenticationCredentials.builder()
                .accessKeyId(mainAppUserDetails.getAccessKeyId())
                .secretKey(mainAppUserDetails.getSecretKey())
                .region(mainAppUserDetails.getRegion())
                .build();

        AWSAuthenticationCredentialsProvider awsAuthenticationCredentialsProvider = AWSAuthenticationCredentialsProvider.builder()
                .roleArn(mainAppUserDetails.getRoleArn())
                .roleSessionName(mainAppUserDetails.getRoleSessionName())
                .build();

        LWAAuthorizationCredentials lwaAuthorizationCredentials = LWAAuthorizationCredentials.builder()
                .clientId(mainAppUserDetails.getClientId())
                .clientSecret(mainAppUserDetails.getClientSecret())
                .refreshToken(mainAppUserDetails.getRefreshToken())
                .endpoint("https://api.amazon.com/auth/o2/token")
                .build();

        // GRANTLESS - REMOVE IF NOT NEEDED
        LWAAuthorizationCredentials lwaAuthorizationCredentialsGrantless =
                LWAAuthorizationCredentials.builder()
                        .clientId(mainAppUserDetails.getClientId())
                        .clientSecret(mainAppUserDetails.getClientSecret())
                        .withScopes(SCOPE_NOTIFICATIONS_API, SCOPE_MIGRATION_API)
                        .endpoint("https://api.amazon.com/auth/o2/token")
                        .build();

        ReportsApi reportsApi = new ReportsApi.Builder()
                .awsAuthenticationCredentials(awsAuthenticationCredentials)
                .lwaAuthorizationCredentials(lwaAuthorizationCredentials)
                .endpoint("https://sellingpartnerapi-eu.amazon.com")
                .build();

        return reportsApi;
    }



    @Async
    public String createReportAndGetReportID(ReportsApi reportsApi) {
        List<String> marketplaceList = new ArrayList<String>();
        marketplaceList.add("A1PA6795UKMFR9");

        CreateReportSpecification createReportSpecification = new CreateReportSpecification();
        createReportSpecification.setMarketplaceIds(marketplaceList);
        createReportSpecification.setReportType("GET_MERCHANT_LISTINGS_ALL_DATA");

        try {
            String reportID = reportsApi.createReport(createReportSpecification).getPayload().getReportId();

            // String reportDocumentId = reportsApi.getReport(reportID).getPayload().toString();
            return reportID;

        } catch (ApiException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @Async
    public ReportDocument getDownloadInformationsForReport (String reportID, ReportsApi reportsApi) {
        List<String> marketplaceList = new ArrayList<String>();
        marketplaceList.add("A1PA6795UKMFR9");

        CreateReportSpecification createReportSpecification = new CreateReportSpecification();
        createReportSpecification.setMarketplaceIds(marketplaceList);
        createReportSpecification.setReportType("GET_MERCHANT_LISTINGS_ALL_DATA");

        try {
            String reportDocumentId = reportsApi.getReport(reportID).getPayload().getReportDocumentId();
            ReportDocument reportsDocumentDownloadInformations = reportsApi.getReportDocument(reportDocumentId).getPayload();
            return reportsDocumentDownloadInformations;

        } catch (ApiException e) {
            log.warn(e.getMessage());
            return null;
        }
    }


    public File downloadReportDocument(ReportDocument reportsDocumentDownloadInformations) {
        String baseurl = "https://sellingpartnerapi-na.amazon.com/reports/2020-09-04/documents/";
        String downloadUrl = baseurl + reportsDocumentDownloadInformations.getUrl();

        String initializationVector = reportsDocumentDownloadInformations.getEncryptionDetails().getInitializationVector();
        String AesDecryptKey = reportsDocumentDownloadInformations.getEncryptionDetails().getKey();

        try {
            File response = restTemplate.execute(downloadUrl, HttpMethod.GET, null, null);

            //AES ENCRYPT? HERE?


            return response;
        } catch (RestClientException e) {
            log.warn(e.getMessage());
            return null;
        }
    }


    public void decryptFile(String algorithm, SecretKey key, IvParameterSpec iv,
                                   File inputFile, File outputFile) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

}

