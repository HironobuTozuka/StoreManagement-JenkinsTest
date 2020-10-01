package inc.roms.rcs.service.order.response;

import inc.roms.rcs.vo.order.OrderId;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BatchOrderActionResponse {
    private final List<OrderId> success;
    private final List<OrderId> failed;
}
