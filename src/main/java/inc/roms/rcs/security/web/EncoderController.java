package inc.roms.rcs.security.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class EncoderController {

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/encode")
    public void encode(@RequestParam String password){
        log.info("Encoded: {}", passwordEncoder.encode(password));
    }

}
