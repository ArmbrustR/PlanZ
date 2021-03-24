package de.armbrust.planz.amazonapi;

import com.amazon.SellingPartnerAPIAA.AWSAuthenticationCredentials;
import com.amazon.SellingPartnerAPIAA.LWAAuthorizationCredentials;
import com.amazon.sellingpartner.api.ReportsApi;
import de.armbrust.planz.security.AppUser;
import de.armbrust.planz.service.AppUserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportsApiServiceTest {

    private final AppUserService appUserService = mock(AppUserService.class);
    private final DownloadAndDecryptService downloadAndDecryptService = mock(DownloadAndDecryptService.class);
    private final ReportParsingService reportParsingService = mock(ReportParsingService.class);
    private final AuthBuilderService authBuilderService = mock(AuthBuilderService.class);
    private final ReportsApiService reportsApiService = new ReportsApiService(appUserService, downloadAndDecryptService, reportParsingService, authBuilderService);

    @Test
    @DisplayName("Should return a reportsapi")
    public void getReportsApi() {
        //GIVEN
        AWSAuthenticationCredentials awsAuthenticationCredentials = AWSAuthenticationCredentials.builder()
                .accessKeyId("12345")
                .secretKey("67890")
                .region("europa")
                .build();

        LWAAuthorizationCredentials lwaAuthorizationCredentials = LWAAuthorizationCredentials.builder()
                .clientId("9876")
                .clientSecret("topsecret")
                .refreshToken("refreshtoken")
                .endpoint("https://api.amazon.com/auth/o2/token")
                .build();

        String mainAppUserId = "Rafael";

        AppUser mainAppUserDetails = AppUser.builder()
                        .accessKeyId("12345")
                        .secretKey("67890")
                        .clientId("9876")
                        .clientSecret("topsecret")
                        .password("tollespasswort")
                        .refreshToken("refreshtoken")
                        .region("europa")
                        .build();

        when(appUserService.findAppUserInAppUserDb(mainAppUserId)).thenReturn(mainAppUserDetails);
        when(authBuilderService.getAwsAuthenticationCredentials(mainAppUserDetails)).thenReturn(awsAuthenticationCredentials);
        when(authBuilderService.getLwaAuthorizationCredentials(mainAppUserDetails)).thenReturn(lwaAuthorizationCredentials);

        //WHEN
        ReportsApi actual = reportsApiService.getReportsApi();

        ReportsApi expected = new ReportsApi.Builder()
                .awsAuthenticationCredentials(awsAuthenticationCredentials)
                .lwaAuthorizationCredentials(lwaAuthorizationCredentials)
                .endpoint("https://sellingpartnerapi-eu.amazon.com")
                .build();

        //THEN
        assertThat(actual.getApiClient().getAuthentications(), Matchers.is(expected.getApiClient().getAuthentications()));

    }

}