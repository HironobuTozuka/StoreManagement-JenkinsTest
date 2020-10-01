package inc.roms.rcs.security;

import com.auth0.jwt.algorithms.Algorithm;

public class Security {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users/sign-up";

    public static Algorithm algorithm() {
        return Algorithm.HMAC512(SECRET);
    }
}
