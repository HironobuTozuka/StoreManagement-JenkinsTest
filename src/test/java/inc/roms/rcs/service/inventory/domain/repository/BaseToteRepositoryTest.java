package inc.roms.rcs.service.inventory.domain.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

abstract class BaseToteRepositoryTest {

    protected ToteRepository toteRepository;

    @Test
    public void simple() {
        assertThat(toteRepository.findAll()).isNullOrEmpty();
    }

}