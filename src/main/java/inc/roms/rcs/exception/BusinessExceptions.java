package inc.roms.rcs.exception;

import inc.roms.rcs.service.inventory.exception.SkuNotFoundException;
import inc.roms.rcs.service.inventory.exception.SkuNotReadyException;
import inc.roms.rcs.service.inventory.exception.ToteNotFoundException;
import inc.roms.rcs.service.order.exception.NoEmptyTotesException;
import inc.roms.rcs.service.order.exception.OrderNotFoundException;
import inc.roms.rcs.vo.common.TemperatureRegime;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessExceptions {

    public SkuNotFoundException skuNotFoundException(SkuId skuId) {
        return new SkuNotFoundException(skuId);
    }

    public SkuNotReadyException skuNotReadyException(SkuId skuId) {
        return new SkuNotReadyException(skuId);
    }

    public NoEmptyTotesException noEmptyTotesException(TemperatureRegime temperatureRegime) {
        return new NoEmptyTotesException(temperatureRegime);
    }

    public NoEmptyTotesException noEmptyTotesException() {
        return new NoEmptyTotesException();
    }

    public ToteNotFoundException toteNotFoundException(ToteId toteId) {
        return new ToteNotFoundException(toteId);
    }

    public OrderNotFoundException orderNotFoundException(OrderId orderId) {
        return new OrderNotFoundException(orderId);
    }
}
