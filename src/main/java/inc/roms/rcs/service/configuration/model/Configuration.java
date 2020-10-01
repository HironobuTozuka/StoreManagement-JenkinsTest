package inc.roms.rcs.service.configuration.model;

import inc.roms.rcs.vo.config.DbConfigKey;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Configuration {

    @EmbeddedId
    private DbConfigKey key;

    private String value;

    public Configuration() {
    }

    public Configuration(DbConfigKey key, String value) {
        this.key = key;
        this.value = value;
    }

}
