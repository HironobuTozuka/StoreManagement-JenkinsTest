package inc.roms.rcs.security.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    private String username;
    private String password;

    private boolean enabled;

    private String firstname;
    private String lastname;

}
