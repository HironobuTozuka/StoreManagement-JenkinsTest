package inc.roms.rcs.api.external.v2_0;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inc.roms.rcs.api.external.v1_0.UserController;
import inc.roms.rcs.validation.Validator;

@RestController
@RequestMapping("api/2.0/")
public class UserController_v2 extends UserController {

    public UserController_v2(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
            Validator validator) {
        super(authenticationManager, userDetailsService, validator);
    }

}