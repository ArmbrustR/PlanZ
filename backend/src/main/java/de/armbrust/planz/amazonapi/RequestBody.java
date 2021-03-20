package de.armbrust.planz.amazonapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestBody {

    private String grant_type;
    private String refresh_token;
    private String client_id;
    private String client_secret;

}
