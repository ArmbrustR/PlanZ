package de.armbrust.planz.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "appusers")
public class AppUser {

    @Id
    private String username;
    private String password;

    // for AWSAuthenticationCredentials
    private String accessKeyId;
    private String secretKey;
    private String region;

    // for AWSAuthenticationCredentialsProvider
    private String roleArn;
    private String roleSessionName;

    // for LWAAuthorizationCredentials
    private String clientId;
    private String clientSecret;
    private String refreshToken;

    //some more
    private String user_agent;

}
