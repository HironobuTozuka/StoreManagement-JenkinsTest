package inc.roms.rcs.security.repository;

import inc.roms.rcs.security.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findFirstByUsername(String username);

}
