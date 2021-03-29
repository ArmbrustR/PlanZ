package de.armbrust.planz.amazonapi;

import com.amazon.SellingPartnerAPIAA.AWSAuthenticationCredentials;
import com.amazon.SellingPartnerAPIAA.AWSAuthenticationCredentialsProvider;
import com.amazon.SellingPartnerAPIAA.LWAAuthorizationCredentials;
import de.armbrust.planz.security.AppUser;
import org.springframework.stereotype.Service;

@Service
public class AuthBuilderService {

    public AWSAuthenticationCredentials getAwsAuthenticationCredentials(AppUser mainAppUserDetails) {

        AWSAuthenticationCredentials awsAuthenticationCredentials = AWSAuthenticationCredentials.builder()
                .accessKeyId(mainAppUserDetails.getAccessKeyId())
                .secretKey(mainAppUserDetails.getSecretKey())
                .region(mainAppUserDetails.getRegion())
                .build();

        return awsAuthenticationCredentials;
    }

    public AWSAuthenticationCredentialsProvider getAwsAuthenticationCredentialsProvider (AppUser mainAppUserDetails) {
        AWSAuthenticationCredentialsProvider awsAuthenticationCredentialsProvider = AWSAuthenticationCredentialsProvider.builder()
                .roleArn(mainAppUserDetails.getRoleArn())
                .roleSessionName(mainAppUserDetails.getRoleSessionName())
                .build();
        return awsAuthenticationCredentialsProvider;
    }

    public LWAAuthorizationCredentials getLwaAuthorizationCredentials(AppUser mainAppUserDetails) {
        LWAAuthorizationCredentials lwaAuthorizationCredentials = LWAAuthorizationCredentials.builder()
                .clientId(mainAppUserDetails.getClientId())
                .clientSecret(mainAppUserDetails.getClientSecret())
                .refreshToken(mainAppUserDetails.getRefreshToken())
                .endpoint("https://api.amazon.com/auth/o2/token")
                .build();

        return lwaAuthorizationCredentials;
    }
}
