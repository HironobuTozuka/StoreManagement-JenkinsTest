package inc.roms.rcs.service.order.domain.repository;

import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.vo.order.OrderId;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    
    Optional<Order> findByOrderId(OrderId orderId);

    List<Order> findAllByOrderIdIn(List<OrderId> orderIds);

}
