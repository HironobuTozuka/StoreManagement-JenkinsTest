package inc.roms.rcs.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class RcsUserDetails implements UserDetails {

    private Set<GrantedAuthority> authorities;
    private String password;
    private String username;
    private boolean enabled;
    private String firstname;
    private String lastname;

    public RcsUserDetails(User user) {
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.enabled = user.isEnabled();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.authorities = user.getRoles().stream().flatMap(it -> it.getAuthorities().stream()).map(it -> new SimpleGrantedAuthority(it.getName())).collect(Collectors.toSet());
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
