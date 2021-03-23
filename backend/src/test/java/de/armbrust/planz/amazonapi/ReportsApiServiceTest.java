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
    private final ReportsApi reportsApi = mock(ReportsApi.class);
    private final ReportsApiService reportsApiService = new ReportsApiService(appUserService);


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

        when(appUserService.findAppUserInAppUserDb(mainAppUserId)).thenReturn(AppUser.builder()
                .accessKeyId("12345")
                .secretKey("67890")
                .clientId("9876")
                .clientSecret("topsecret")
                .password("tollespasswort")
                .refreshToken("refreshtoken")
                .region("europa")
                .build());


        //WHEN
        ReportsApi actual = reportsApiService.getReportsApi();

        //THEN
        assertThat(actual, Matchers.is(new ReportsApi.Builder()
                .awsAuthenticationCredentials(awsAuthenticationCredentials)
                .lwaAuthorizationCredentials(lwaAuthorizationCredentials)
                .endpoint("https://sellingpartnerapi-eu.amazon.com")
                .build()));

    }

}