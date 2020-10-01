package inc.roms.rcs.security.model;

import inc.roms.rcs.vo.security.AuthToken;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

//@Entity
//@Data
public class BlacklistedJwtToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private AuthToken authToken;

    private LocalDateTime expiryDate;

}
