package inc.roms.rcs.service.configuration.repository;

import inc.roms.rcs.service.configuration.model.Configuration;
import inc.roms.rcs.vo.config.DbConfigKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends CrudRepository<Configuration, Integer> {

    Optional<Configuration> getByKey(DbConfigKey key);

}
