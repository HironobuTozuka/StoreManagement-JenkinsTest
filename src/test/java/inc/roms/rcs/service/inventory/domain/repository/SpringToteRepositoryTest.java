package inc.roms.rcs.service.inventory.domain.repository;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = {"zonky.test.database.postgres.client.properties.currentSchema=sm"})
@AutoConfigureEmbeddedDatabase
public class SpringToteRepositoryTest extends BaseToteRepositoryTest {

    @Autowired
    public SpringToteRepositoryTest(ToteRepository toteRepository) {
        this.toteRepository = toteRepository;
    }

}
