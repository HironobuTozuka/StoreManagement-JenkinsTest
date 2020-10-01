package inc.roms.rcs.security.model;

import lombok.Data;

@Data
public class JwtAuthenticateRequest {
    private String username;
    private String password;
}
