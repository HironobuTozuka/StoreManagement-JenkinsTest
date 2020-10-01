package inc.roms.rcs.service.omnichannel.kannart.model;

import inc.roms.rcs.vo.order.OrderStatus;

public enum ActionCode {
    GATE_CLOSED,
    IN_PICKUP_GATE,
    PREORDER_READY,
    SYSTEM_ERROR,
    RESUPPLY_FAILED,
    ORDER_FAILED;

    public static ActionCode from(OrderStatus orderStatus) {
        if(OrderStatus.FAILED.equals(orderStatus)) {
            return ORDER_FAILED;
        }
        return ActionCode.valueOf(orderStatus.toString());
    }
}
