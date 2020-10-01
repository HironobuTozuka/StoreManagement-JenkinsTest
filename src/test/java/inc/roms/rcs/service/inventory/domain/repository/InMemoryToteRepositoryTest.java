package inc.roms.rcs.service.inventory.domain.repository;

public class InMemoryToteRepositoryTest extends BaseToteRepositoryTest {

    public InMemoryToteRepositoryTest() {
        this.toteRepository = new InMemoryToteRepository();
    }

}
