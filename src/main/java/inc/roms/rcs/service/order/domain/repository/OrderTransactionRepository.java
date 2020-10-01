package inc.roms.rcs.service.order.domain.repository;

import inc.roms.rcs.service.order.domain.model.OrderTransaction;
import inc.roms.rcs.service.order.domain.model.TransactionType;
import inc.roms.rcs.vo.order.OrderId;
import org.springframework.data.repository.CrudRepository;

public interface OrderTransactionRepository extends CrudRepository<OrderTransaction, Integer> {

    OrderTransaction findFirstByOrderIdAndTransactionType(OrderId orderId, TransactionType transactionType);

}
