package inc.roms.rcs.service.issue.domain.model;

import inc.roms.rcs.vo.common.TemperatureRegime;
import inc.roms.rcs.vo.issue.*;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreationTimestamp
    private LocalDateTime created;

    @Version
    private Integer version;

    private IssueId issueId;

    private LocalDateTime issueDeadline;

    @Enumerated(EnumType.STRING)
    private IssueAction issueAction;

    @Enumerated(EnumType.STRING)
    private IssueStatus issueStatus;

    @Enumerated(EnumType.STRING)
    private IssueReason reason;

    private ToteId toteId;

    private SkuId skuId;

    private OrderId orderId;

    @Enumerated(EnumType.STRING)
    private TemperatureRegime temperatureRegime;

    private Notes notes;
}
