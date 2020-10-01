package inc.roms.rcs.service.cubing;

import inc.roms.rcs.service.cubing.model.request.DetermineBestToteTypeRequest;
import inc.roms.rcs.service.cubing.model.response.DetermineBestToteTypeResponse;
import inc.roms.rcs.service.inventory.ReservationService;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.inventory.domain.model.SlotDimensions;
import inc.roms.rcs.vo.common.Dimensions;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.tote.ToteHeight;
import inc.roms.rcs.vo.tote.TotePartitioning;
import inc.roms.rcs.vo.tote.ToteType;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CubingServiceTest {

    private final ToteService toteService = mock(ToteService.class);
    private final SkuService skuService = mock(SkuService.class);
    private final ReservationService reservationService = mock(ReservationService.class);
    private final CubingService cubingService = new CubingService(toteService, skuService, reservationService);

    @Test
    public void singleValidTote() {
        //given
        Set<ToteType> availableToteTypes = new HashSet<>();
        ToteType bipartiteHigh = new ToteType(TotePartitioning.BIPARTITE, ToteHeight.HIGH);
        availableToteTypes.add(bipartiteHigh);

        Sku sku = new Sku();
        sku.setDimensions(new Dimensions(1, 1, 1));

        SlotDimensions slotDimensions = new SlotDimensions();
        slotDimensions.setToteType(bipartiteHigh);
        slotDimensions.setDimensions(new Dimensions(2, 2, 2));

        when(toteService.getSlotDimenstionsFor(bipartiteHigh.getToteHeight(), bipartiteHigh.getTotePartitioning())).thenReturn(slotDimensions);

        DetermineBestToteTypeResponse bestToteType = cubingService.determineBestToteType(new DetermineBestToteTypeRequest(sku, availableToteTypes, 0.8));

        assertThat(bestToteType.getMaxQuantity()).isEqualTo(Quantity.of(6));
        assertThat(bestToteType.getBestToteType()).isEqualTo(bipartiteHigh);
    }

    @Test
    public void noValidTote() {
        //given
        Set<ToteType> availableToteTypes = new HashSet<>();
        ToteType bipartiteHigh = new ToteType(TotePartitioning.BIPARTITE, ToteHeight.HIGH);
        availableToteTypes.add(bipartiteHigh);

        Sku sku = new Sku();
        sku.setDimensions(new Dimensions(3, 3, 3));

        SlotDimensions slotDimensions = new SlotDimensions();
        slotDimensions.setToteType(bipartiteHigh);
        slotDimensions.setDimensions(new Dimensions(2, 2, 2));

        when(toteService.getSlotDimenstionsFor(bipartiteHigh.getToteHeight(), bipartiteHigh.getTotePartitioning())).thenReturn(slotDimensions);

        DetermineBestToteTypeResponse bestToteType = cubingService.determineBestToteType(new DetermineBestToteTypeRequest(sku, availableToteTypes, 0.8));

        assertThat(bestToteType.getBestToteType()).isNull();
        assertThat(bestToteType.getMaxQuantity()).isEqualTo(Quantity.of(0));
    }


    @Test
    public void preferLowTripartiteTotes() {
        //given
        Set<ToteType> availableToteTypes = new HashSet<>();
        ToteType bipartiteHigh = new ToteType(TotePartitioning.BIPARTITE, ToteHeight.HIGH);
        ToteType tripartiteLow = new ToteType(TotePartitioning.TRIPARTITE, ToteHeight.LOW);
        availableToteTypes.add(bipartiteHigh);
        availableToteTypes.add(tripartiteLow);

        Sku sku = new Sku();
        sku.setDimensions(new Dimensions(1, 1, 1));

        SlotDimensions slotDimensionsTL = new SlotDimensions();
        slotDimensionsTL.setToteType(tripartiteLow);
        slotDimensionsTL.setDimensions(new Dimensions(2, 2, 2));

        SlotDimensions slotDimensionsBH = new SlotDimensions();
        slotDimensionsBH.setToteType(bipartiteHigh);
        slotDimensionsBH.setDimensions(new Dimensions(3, 3, 3));

        when(toteService.getSlotDimenstionsFor(bipartiteHigh.getToteHeight(), bipartiteHigh.getTotePartitioning())).thenReturn(slotDimensionsBH);
        when(toteService.getSlotDimenstionsFor(tripartiteLow.getToteHeight(), tripartiteLow.getTotePartitioning())).thenReturn(slotDimensionsTL);

        DetermineBestToteTypeResponse bestToteType = cubingService.determineBestToteType(new DetermineBestToteTypeRequest(sku, availableToteTypes, 0.8));

        assertThat(bestToteType.getBestToteType()).isEqualTo(tripartiteLow);
        assertThat(bestToteType.getMaxQuantity()).isEqualTo(Quantity.of(6));
    }

    @Test
    public void shouldSkipPreferredToteIfItsToSmall() {
        //given
        Set<ToteType> availableToteTypes = new HashSet<>();
        ToteType bipartiteHigh = new ToteType(TotePartitioning.BIPARTITE, ToteHeight.HIGH);
        ToteType tripartiteLow = new ToteType(TotePartitioning.TRIPARTITE, ToteHeight.LOW);
        availableToteTypes.add(bipartiteHigh);
        availableToteTypes.add(tripartiteLow);

        Sku sku = new Sku();
        sku.setDimensions(new Dimensions(1, 1, 1));

        SlotDimensions slotDimensionsTL = new SlotDimensions();
        slotDimensionsTL.setToteType(tripartiteLow);
        slotDimensionsTL.setDimensions(new Dimensions(2, 2, 2));

        SlotDimensions slotDimensionsBH = new SlotDimensions();
        slotDimensionsBH.setToteType(bipartiteHigh);
        slotDimensionsBH.setDimensions(new Dimensions(3, 3, 3));

        when(toteService.getSlotDimenstionsFor(bipartiteHigh.getToteHeight(), bipartiteHigh.getTotePartitioning())).thenReturn(slotDimensionsBH);
        when(toteService.getSlotDimenstionsFor(tripartiteLow.getToteHeight(), tripartiteLow.getTotePartitioning())).thenReturn(slotDimensionsTL);

        DetermineBestToteTypeResponse bestToteType = cubingService.determineBestToteType(new DetermineBestToteTypeRequest(sku, availableToteTypes, 0.8));

        assertThat(bestToteType.getBestToteType()).isEqualTo(tripartiteLow);
        assertThat(bestToteType.getMaxQuantity()).isEqualTo(Quantity.of(6));
    }
}