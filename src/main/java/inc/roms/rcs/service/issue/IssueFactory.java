package inc.roms.rcs.service.issue;

import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.issue.IssueAction;
import inc.roms.rcs.vo.issue.IssueReason;
import inc.roms.rcs.vo.issue.Notes;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.tote.ToteStatus;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;

import static inc.roms.rcs.service.issue.request.CreateIssueRequest.issue;

@Component
@RequiredArgsConstructor
public class IssueFactory {

    private final Clock clock;

    public CreateIssueRequest toteNotFound(ToteId toteId) {
        return issue()
                .issueAction(IssueAction.CHECK_TOTE)
                .deadlineTomorrow(clock)
                .reason(IssueReason.UNKNOWN_TOTE)
                .toteId(toteId)
                .build();
    }

    public CreateIssueRequest skuNotFound(SkuId skuId) {
        return issue()
                .issueAction(IssueAction.CHECK_SKU)
                .deadlineTomorrow(clock)
                .reason(IssueReason.UNKNOWN_SKU)
                .skuId(skuId)
                .build();
    }

    public CreateIssueRequest outOfStock(SkuId skuId) {
        return issue()
                .issueAction(IssueAction.CHECK_SKU_BATCH)
                .skuId(skuId)
                .deadlineTomorrow(clock)
                .reason(IssueReason.OUT_OF_STOCK)
                .build();
    }

    public CreateIssueRequest cannotDeliverPreorder(OrderId orderId) {
        return issue()
                .issueAction(IssueAction.CHECK_ORDER)
                .reason(IssueReason.CANNOT_DELIVER)
                .deadlineTomorrow(clock)
                .orderId(orderId)
                .build();
    }

    public CreateIssueRequest orderNotFound(OrderId orderId) {
        return issue()
                .issueAction(IssueAction.CHECK_ORDER)
                .reason(IssueReason.ORDER_NOT_FOUND)
                .deadlineTomorrow(clock)
                .orderId(orderId)
                .build();
    }

    public CreateIssueRequest orderAlreadyDelivered(OrderId orderId) {
        return issue()
                .issueAction(IssueAction.CHECK_ORDER)
                .reason(IssueReason.ORDER_ALREADY_DELIVERED)
                .deadlineTomorrow(clock)
                .orderId(orderId)
                .build();
    }

    public CreateIssueRequest noEmptyTotes() {
        return issue()
                .issueAction(IssueAction.EMPTY_TOTES)
                .reason(IssueReason.NO_EMPTY_TOTES)
                .deadlineTomorrow(clock)
                .build();
    }

    public CreateIssueRequest cannotPick(OrderId orderId, SkuId skuId) {
        return issue()
                .issueAction(IssueAction.CHECK_SKU_BATCH)
                .orderId(orderId)
                .skuId(skuId)
                .deadlineTomorrow(clock)
                .reason(IssueReason.CANNOT_PICK)
                .build();
    }

    public CreateIssueRequest noEmptySpaceForTote(ZoneId zoneId) {
        return issue()
                .issueAction(IssueAction.CLEAN_ZONE)
                .zoneId(zoneId)
                .deadlineTomorrow(clock)
                .reason(IssueReason.NO_SPACE_FOR_TOTE)
                .build();
    }

    public CreateIssueRequest noSpaceForStock(SkuId skuId) {
        return issue()
                .issueAction(IssueAction.CHECK_SKU)
                .skuId(skuId)
                .reason(IssueReason.NO_SPACE_FOR_STOCK)
                .deadlineTomorrow(clock)
                .build();
    }

    public CreateIssueRequest mheOutOfService() {
        return issue()
                .deadlineTomorrow(clock)
                .reason(IssueReason.COULDNT_CONNECT_TO_MHE)
                .issueAction(IssueAction.VERIFY_MHE_OPERATOR)
                .build();
    }

    public CreateIssueRequest orderNotCollected(OrderId orderId) {
        return issue()
                .deadlineTomorrow(clock)
                .reason(IssueReason.ORDER_NOT_COLLECTED)
                .orderId(orderId)
                .issueAction(IssueAction.CHECK_TOTE)
                .orderId(orderId).build();
    }

    public CreateIssueRequest orderToBig(OrderId orderId, Quantity actualOrderSize, Integer maxOrderSize) {
        return issue()
                .deadlineTomorrow(clock)
                .reason(IssueReason.ORDER_TO_BIG)
                .issueAction(IssueAction.VERIFY_ORDERS_CONSTRAINTS)
                .orderId(orderId)
                .notes(Notes.from("Actual size: " + actualOrderSize + " conifgured max size: " + maxOrderSize)).build();
    }

    public CreateIssueRequest destToteFailure(ToteId toteId) {
        return issue()
                .deadlineTomorrow(clock)
                .reason(IssueReason.COULDNT_PLACE)
                .toteId(toteId)
                .issueAction(IssueAction.CHECK_TOTE).build();
    }

    public CreateIssueRequest orderStatusNotSent(OrderId orderId) {
        return issue()
                .deadlineTomorrow(clock)
                .reason(IssueReason.OMNICHANNEL_NOT_RESPONDING)
                .orderId(orderId)
                .issueAction(IssueAction.CHECK_OMNICHANNEL).build();
    }

    public CreateIssueRequest gateNotFound(OrderId orderId, GateId gateId, List<GateId> availableOrderGateIds) {
        return issue()
                .deadlineTomorrow(clock)
                .reason(IssueReason.UNKNOWN_GATE)
                .orderId(orderId)
                .notes(Notes.from("Requested gate: " + gateId + " available gates: " + availableOrderGateIds)).build();

    }

    public CreateIssueRequest toteNoRead(ToteId toteId) {
        return issue()
                .deadlineTomorrow(clock)
                .reason(IssueReason.NO_READ)
                .issueAction(action(ToteStatus.NO_READ))
                .toteId(toteId)
                .build();
    }

    public CreateIssueRequest toteOnTechnicalLocation(ToteId toteId, ToteStatus toteStatus) {
        return issue()
                .deadlineTomorrow(clock)
                .reason(IssueReason.TOTE_PROBLEM)
                .toteId(toteId)
                .issueAction(action(toteStatus))
                .notes(notes(toteStatus))
                .build();
    }

    private Notes notes(ToteStatus toteStatus) {
        switch(toteStatus) {
            case OVERFILL:
                return Notes.from("Stock detected above tote edge");
            case NO_READ:
                return Notes.from("Check if barcodes are OK, no read detected");
            case ZONE_NOT_ASSIGNED:
                return Notes.from("Tote was put into order gate, but not inducted. Induct this tote again");
            default:
                return Notes.from("Unknown issue");
        }
    }

    private IssueAction action(ToteStatus toteStatus) {
        switch(toteStatus) {
            case OVERFILL:
                return IssueAction.REMOVE_OVERFILL;
            case NO_READ:
                return IssueAction.CHECK_TOTE_BARCODES;
            case ZONE_NOT_ASSIGNED:
                return IssueAction.REINDUCT_TOTE;
            default:
                return IssueAction.UNKNOWN;
        }
    }
}
