package inc.roms.rcs.api.external.v1_0;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;

import inc.roms.rcs.validation.Validator;
import inc.roms.rcs.security.Security;
import inc.roms.rcs.security.model.JwtAuthenticateRequest;
import inc.roms.rcs.security.model.JwtInvalidateRequest;
import inc.roms.rcs.security.model.JwtResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/1.0/")
public class UserController {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final Validator validator;

    // TODO this is the same as JwtAuthenticationController.
    // Dont forget to remove JwtAuthenticationController when new frontend arrives.
    @PostMapping(value = "user:authenticate")
    public JwtResponse authenticate(@RequestBody JwtAuthenticateRequest authenticationRequest) throws Exception {
        log.info("sys:sm; message:user authentication request; username:{}", authenticationRequest.getUsername());
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList());

        String jwt = JWT.create().withClaim("sub", userDetails.getUsername())
                .withClaim("exp", new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .withClaim("name", userDetails.getUsername())
                .withClaim("roles", roles)
                .sign(Security.algorithm());
        return new JwtResponse(jwt);
    }

    // FIXME this should implement the real invalidate,
    // it is here for service panel frontend.
    @PostMapping(value = "user:invalidate", produces = { "application/json" })
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String invalidate(@RequestBody JwtInvalidateRequest invalidateRequest) throws Exception {
        log.info("sys:sm; message:user invalidate request; username:{}", invalidateRequest.getUsername());
        validator.validate(invalidateRequest);
        return "{\n    \"message\": \"ignore this. nothing will been done. this is a mock method. \"\n}";
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
