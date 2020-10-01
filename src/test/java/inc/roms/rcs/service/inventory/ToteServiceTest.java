package inc.roms.rcs.service.inventory;

import inc.roms.rcs.builders.ToteBuilder;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.repository.SkuRepository;
import inc.roms.rcs.service.inventory.domain.repository.ToteRepository;
import inc.roms.rcs.service.inventory.response.ToteTechnicalData;
import inc.roms.rcs.service.machineoperator.ZoneService;
import inc.roms.rcs.service.omnichannel.OmniChannelService;
import inc.roms.rcs.service.order.domain.OrderProgressService;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static inc.roms.rcs.builders.StorageSlotBuilder.storageSlot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ToteServiceTest {

    public static final ToteId TOTE_ID = ToteId.from("TOTE_ID");

    public static final SkuId SKU_1 = SkuId.from("SKU_1");
    public static final double MAX_ACC_5 = 5D;
    public static final double WEIGHT_2 = 2D;

    public static final SkuId SKU_2 = SkuId.from("SKU_2");
    public static final double MAX_ACC_2 = 2D;
    public static final double WEIGHT_5 = 5D;

    private final SkuRepository skuRepository = mock(SkuRepository.class);
    private final ToteRepository toteRepository = mock(ToteRepository.class);
    private final ZoneService zoneService = mock(ZoneService.class);
    private final OmniChannelService omniChannelService = mock(OmniChannelService.class);
    private final TaskBundleService taskBundleService = mock(TaskBundleService.class);

    private final ToteService toteService = new ToteService(null, toteRepository, skuRepository, zoneService, taskBundleService, omniChannelService);

    @Test
    public void shouldReturnProperDetailsForToteWithOneStorageSlot() {
        Tote tote = ToteBuilder.defaultTote()
                .toteId(TOTE_ID)
                .slots(storageSlot().ordinal(0).quantity(10).skuId(SKU_1)).build();

        Sku sku = new Sku();
        sku.setSkuId(SKU_1);
        sku.setMaxAcc(MAX_ACC_5);
        sku.setWeight(WEIGHT_2);

        when(toteRepository.findByToteId(TOTE_ID)).thenReturn(Optional.of(tote));
        when(skuRepository.findById(eq(SKU_1))).thenReturn(Optional.of(sku));

        ToteTechnicalData toteDetails = toteService.getToteDetails(TOTE_ID);

        assertThat(toteDetails.getToteId()).isEqualTo(TOTE_ID);
        assertThat(toteDetails.getMaxAcc()).isEqualTo(MAX_ACC_5);
        assertThat(toteDetails.getWeight()).isEqualTo(WEIGHT_2 * 10);
    }

    @Test
    public void shouldReturnProperDetailsForToteWithTwoStorageSlot() {
        int sku2Quantity = 4;
        int sku1Quantity = 10;
        Tote tote = ToteBuilder.defaultTote()
                .toteId(TOTE_ID)
                .slots(storageSlot().ordinal(0).quantity(sku1Quantity).skuId(SKU_1),
                        storageSlot().ordinal(1).quantity(sku2Quantity).skuId(SKU_2)).build();

        Sku sku1 = new Sku();
        sku1.setSkuId(SKU_1);
        sku1.setMaxAcc(MAX_ACC_5);
        sku1.setWeight(WEIGHT_2);

        Sku sku2 = new Sku();
        sku2.setSkuId(SKU_2);
        sku2.setMaxAcc(MAX_ACC_2);
        sku2.setWeight(WEIGHT_5);

        when(toteRepository.findByToteId(TOTE_ID)).thenReturn(Optional.of(tote));
        when(skuRepository.findById(eq(SKU_1))).thenReturn(Optional.of(sku1));
        when(skuRepository.findById(eq(SKU_2))).thenReturn(Optional.of(sku2));

        ToteTechnicalData toteDetails = toteService.getToteDetails(TOTE_ID);

        assertThat(toteDetails.getToteId()).isEqualTo(TOTE_ID);
        assertThat(toteDetails.getMaxAcc()).isEqualTo(MAX_ACC_2);
        assertThat(toteDetails.getWeight()).isEqualTo(WEIGHT_2 * sku1Quantity + WEIGHT_5 * sku2Quantity);
    }
}