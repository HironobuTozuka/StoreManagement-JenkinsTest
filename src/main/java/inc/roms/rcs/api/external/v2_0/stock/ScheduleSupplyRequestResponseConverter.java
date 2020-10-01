package inc.roms.rcs.api.external.v2_0.stock;

import inc.roms.rcs.api.external.v2_0.vo.AcceptCode;
import inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper;
import inc.roms.rcs.service.inventory.request.BatchDisposeSkuBatchRequest;
import inc.roms.rcs.service.inventory.request.DisposeSkuBatchRequest;
import inc.roms.rcs.service.inventory.response.BatchDisposeSkuBatchResponse;
import inc.roms.rcs.service.inventory.response.ScheduleSupplyResponse;
import inc.roms.rcs.vo.common.ResponseCode;
import inc.roms.rcs.vo.common.StoreId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.nowInJapan;
import static inc.roms.rcs.vo.common.ResponseCode.ACCEPTED;
import static inc.roms.rcs.vo.common.ResponseCode.REJECTED;

@Component
public class ScheduleSupplyRequestResponseConverter {

    @Value("${rcs.store.code:POC}")
    private StoreId storeCode;

    public inc.roms.rcs.service.inventory.request.ScheduleSupplyRequest toBaseRequest(ScheduleSupplyRequest supplyRequest) {
        inc.roms.rcs.service.inventory.request.ScheduleSupplyRequest baseRequest = new inc.roms.rcs.service.inventory.request.ScheduleSupplyRequest();
        baseRequest.setDeliveryDate(supplyRequest.getDeliveryDate());
        baseRequest.setDeliveryTurn(supplyRequest.getDeliveryTurn());
        baseRequest.setDistributionType(supplyRequest.getDistributionType());
        baseRequest.setQuantity(supplyRequest.getQuantity());
        baseRequest.setSkuId(supplyRequest.getSku());
        baseRequest.setSellByDate(LocalDateTime.ofInstant(supplyRequest.getSellByDate().toInstant(), ZoneOffset.UTC));
        return baseRequest;
    }

    public SupplyResponse convert(ScheduleSupplyResponse scheduleSupply) {
        SupplyResponse response = new SupplyResponse();
        response.setReceiveTime(nowInJapan());
        response.setStoreCode(storeCode);
        response.setAcceptCode(AcceptCode.from(scheduleSupply.getResponseCode()));
        return response;
    }

    public BatchDisposeSkuBatchRequest toBaseRequest(DisposeStockRequest disposeStockRequest) {
        List<DisposeSkuBatchRequest> atomicRequests = disposeStockRequest.getItems().stream()
                .map(it -> new DisposeSkuBatchRequest(it.getSku(), JapanTimeHelper.toUtc(it.getSellByDate())))
                .collect(Collectors.toList());
        return new BatchDisposeSkuBatchRequest(atomicRequests);
    }

    public SupplyResponse convert(BatchDisposeSkuBatchResponse batchDispose) {
        ResponseCode code =
                batchDispose.getDisposeSkuBatchRespons().stream().allMatch(it -> ACCEPTED.equals(it.getResponseCode()))
                ? ACCEPTED : REJECTED;

        SupplyResponse response = new SupplyResponse();
        response.setReceiveTime(nowInJapan());
        response.setStoreCode(storeCode);
        response.setAcceptCode(AcceptCode.from(code));
        return response;
    }
}
