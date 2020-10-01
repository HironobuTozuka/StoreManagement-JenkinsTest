package inc.roms.rcs.service.order.domain.repository;

import inc.roms.rcs.service.order.domain.model.OrderLine;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLineRepository extends CrudRepository<OrderLine, Integer> {
}
