package de.armbrust.planz.amazonapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenResponse {

private String access_token;
private String refresh_token;
private String token_type;
private String expires_in;

}
