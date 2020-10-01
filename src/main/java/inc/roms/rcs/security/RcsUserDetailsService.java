package inc.roms.rcs.security;

import inc.roms.rcs.security.model.RcsUserDetails;
import inc.roms.rcs.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class RcsUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findFirstByUsername(username).map(RcsUserDetails::new).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
