package inc.roms.rcs.service.omnichannel.v1.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthResponse3E {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private int expiresIn;
    private String scope;
    @JsonProperty("UUID")
    private String uuid;
    @JsonProperty("TENANT")
    private String tenant;
    private String jti;

}
