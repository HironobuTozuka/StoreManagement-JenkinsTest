package inc.roms.rcs.service.order.domain.model;

import inc.roms.rcs.vo.common.UserId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderStatus;
import inc.roms.rcs.vo.order.OrderType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity(name = "customer_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    private OrderId orderId;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime pickupTime;

    private LocalDateTime collectedAt;

    private LocalDateTime deliveredAt;

    private GateId gate;

    private UserId userId;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_order_id")
    private List<OrderLine> orderLines;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
        orderLines.forEach(ol -> ol.setOrder(this));
    }
}
