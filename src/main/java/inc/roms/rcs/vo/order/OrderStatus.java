package inc.roms.rcs.vo.order;

public enum OrderStatus {
    READY_TO_BE_PICKED,
    NOT_STARTED,
    PICKING,
    GOING_TO_PICKUP_GATE,
    IN_PICKUP_GATE,
    PREORDER_READY,
    COLLECTED,
    NOT_COLLECTED,
    FAILED,
    ABANDONED,
    DISPOSED,
    CANCELLED,
    REMOVED
}
