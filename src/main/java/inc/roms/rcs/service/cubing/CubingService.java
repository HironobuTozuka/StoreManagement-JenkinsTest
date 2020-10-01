package inc.roms.rcs.service.cubing;

import inc.roms.rcs.service.cubing.model.request.DetermineBestToteTypeRequest;
import inc.roms.rcs.service.cubing.model.response.DetermineBestToteTypeResponse;
import inc.roms.rcs.service.inventory.ReservationService;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.DeliveryInventory;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.vo.common.Dimensions;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.tote.ToteType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static inc.roms.rcs.vo.tote.ToteHeight.HIGH;
import static inc.roms.rcs.vo.tote.ToteHeight.LOW;
import static inc.roms.rcs.vo.tote.TotePartitioning.BIPARTITE;
import static inc.roms.rcs.vo.tote.TotePartitioning.TRIPARTITE;

@Service
@RequiredArgsConstructor
public class CubingService {

    public static final double DELIVERY_SLOT_USABLE_VOLUME_COEFICCIENT = 0.6;
    private final ToteService toteService;
    private final SkuService skuService;
    private final ReservationService reservationService;

    private final List<ToteType> orderToteTypes = List.of(
            new ToteType(TRIPARTITE, LOW),
            new ToteType(TRIPARTITE, HIGH),
            new ToteType(BIPARTITE, LOW),
            new ToteType(BIPARTITE, HIGH)
    );

    public DetermineBestToteTypeResponse determineBestToteType(DetermineBestToteTypeRequest determineBestToteTypeRequest) {
        Set<ToteType> availableToteTypes = determineBestToteTypeRequest.getAvailableToteTypes();
        Sku sku = determineBestToteTypeRequest.getSku();
        for (ToteType type : orderToteTypes) {
            if (availableToteTypes.contains(type)) {
                Dimensions slotDimensions = toteService.getSlotDimenstionsFor(type.getToteHeight(), type.getTotePartitioning()).getDimensions();
                Quantity maxItemsInSlot = calculteHowManyTimesFirstFitsIntoSecond(sku.getDimensions(), slotDimensions, determineBestToteTypeRequest.getUsableVolumeCoefficient());
                if (maxItemsInSlot.gt(0)) {
                    return new DetermineBestToteTypeResponse(type, maxItemsInSlot);
                }
            }
        }
        return new DetermineBestToteTypeResponse(null, Quantity.of(0));
    }

    public Quantity calculteHowManyTimesFirstFitsIntoSecond(Dimensions first, Dimensions second, double usableVolumeCoefficient) {
        if (first.max() > second.max()) {
            return Quantity.of(0);
        }

        if (first.min() > second.min()) {
            return Quantity.of(0);
        }

        return Quantity.of((int) Math.floor(second.volume() * usableVolumeCoefficient / (first.volume())));
    }

    public boolean canReservationFitIntoDeliverySlot(Reservation reservation, DeliveryInventory deliveryInventory) {
        if(deliveryInventory == null) return true;
        Dimensions deliverySlotDimensions = toteService.getSlotDimenstionsFor(LOW, BIPARTITE).getDimensions();
        Sku reservationSku = skuService.getReadySku(reservation.getSkuId());
        List<Reservation> currentReservations = reservationService.getReservationsForDeliveryInventory(deliveryInventory);
        Double totalReservedVolume = currentReservations.stream().map(res -> {
            Sku sku = skuService.getReadySku(res.getSkuId());
            return sku.getDimensions().volume() * res.getQuantity().getQuantity();
        }).reduce(Double::sum).orElse(0D);
        Double usableVolume = deliverySlotDimensions.volume() * DELIVERY_SLOT_USABLE_VOLUME_COEFICCIENT;
        return (usableVolume - totalReservedVolume) > (reservation.getQuantity().getQuantity() * reservationSku.getDimensions().volume());
    }

}
