package inc.roms.rcs.service.order.domain.model;

import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.order.OrderId;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "customer_order_transaction")
@Data
@NoArgsConstructor
public class OrderTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private OrderId orderId;

    private TransactionId transactionId;

    public OrderTransaction(TransactionType transactionType, OrderId orderId, TransactionId transactionId) {
        this.transactionType = transactionType;
        this.orderId = orderId;
        this.transactionId = transactionId;
    }
}
