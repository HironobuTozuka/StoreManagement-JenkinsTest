package inc.roms.rcs.service.operatorpanel.service;

import inc.roms.rcs.exception.BusinessExceptions;
import inc.roms.rcs.service.configuration.ConfigurationService;
import inc.roms.rcs.service.cubing.CubingService;
import inc.roms.rcs.service.cubing.model.request.DetermineBestToteTypeRequest;
import inc.roms.rcs.service.cubing.model.response.DetermineBestToteTypeResponse;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.SupplyService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.*;
import inc.roms.rcs.service.location.LocationService;
import inc.roms.rcs.service.machineoperator.MachineOperatorService;
import inc.roms.rcs.service.machineoperator.ZoneService;
import inc.roms.rcs.service.machineoperator.model.ZoneState;
import inc.roms.rcs.service.omnichannel.v1.OmniChannel3EService;
import inc.roms.rcs.service.operatorpanel.LoadingGateService;
import inc.roms.rcs.service.operatorpanel.domain.barcode.config.BarcodeScannersPrefixProperties;
import inc.roms.rcs.service.operatorpanel.exception.NoSpaceForStockException;
import inc.roms.rcs.service.operatorpanel.request.InductRequest;
import inc.roms.rcs.service.operatorpanel.request.StorageSlotModel;
import inc.roms.rcs.service.operatorpanel.request.TotesForSkuRequest;
import inc.roms.rcs.vo.common.Dimensions;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.TemperatureRegime;
import inc.roms.rcs.vo.location.LocationId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.DistributionType;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.tote.ToteOrientation;
import inc.roms.rcs.vo.tote.ToteType;
import inc.roms.rcs.vo.zones.ZoneFunction;
import inc.roms.rcs.vo.zones.ZoneId;
import org.junit.jupiter.api.Test;

import java.util.*;

import static inc.roms.rcs.vo.tote.ToteHeight.LOW;
import static inc.roms.rcs.vo.tote.TotePartitioning.BIPARTITE;
import static inc.roms.rcs.vo.zones.ZoneFunction.STORAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LoadingGateServiceTest {

    public static final SkuId SKU_1 = SkuId.from("SKU_1");
    public static final ToteType BIPARTITE_LOW = new ToteType(BIPARTITE, LOW);

    public static final ToteId EMPTY_TOTE_1 = ToteId.from("ID1");
    public static final Integer EMPTY_TOTE_1_ID = 2;
    public static final ToteId TOTE_2 = ToteId.from("ID2");
    public static final Integer TOTE_2_ID = 1;
    public static final ZoneId AMBIENT = ZoneId.from("AMBIENT");
    public static final ZoneId CHILL = ZoneId.from("CHILL");
    public static final ZoneId LOADING_GATE = ZoneId.from("LOADING_GATE");

    private final ToteService toteService = mock(ToteService.class);
    private final CubingService cubingService = mock(CubingService.class);
    private final SkuService skuService = mock(SkuService.class);
    private final OmniChannel3EService omniChannelService = mock(OmniChannel3EService.class);
    private final MachineOperatorService machineOperatorService = mock(MachineOperatorService.class);
    private final LocationService locationService = mock(LocationService.class);
    private final BarcodeScannersPrefixProperties barcodeScannersPrefixProperties = mock(BarcodeScannersPrefixProperties.class);
    private final SupplyService supplyService = mock(SupplyService.class);
    private final ZoneService zoneService = mock(ZoneService.class);
    private final ConfigurationService configurationService = mock(ConfigurationService.class);
    private final BusinessExceptions businessExceptions = new BusinessExceptions();

    private final LoadingGateService loadingGateService = new LoadingGateService(
            toteService,
            machineOperatorService,
            cubingService,
            skuService,
            omniChannelService,
            locationService,
            barcodeScannersPrefixProperties,
            supplyService,
            zoneService,
            businessExceptions,
            configurationService
    );

    public static final DetermineBestToteTypeResponse CUBING_RESPONSE = new DetermineBestToteTypeResponse(BIPARTITE_LOW, Quantity.of(4));

    @Test
    void shouldCallExternalServices() {
        //given
        setupZones();
        setupSku();
        Tote tote = setupOneEmptyTote();
        when(toteService.findToteByToteId(eq(EMPTY_TOTE_1))).thenReturn(Optional.of(tote));

        InductRequest inductRequest = new InductRequest();
        inductRequest.setToteId(EMPTY_TOTE_1);
        inductRequest.setId(EMPTY_TOTE_1_ID);
        List<StorageSlotModel> slots = new ArrayList<>();

        StorageSlotModel storageSlotModel = new StorageSlotModel();
        storageSlotModel.setOrdinal(0);
        storageSlotModel.setQuantity(Quantity.of(10));
        storageSlotModel.setSkuId(SKU_1);
        slots.add(storageSlotModel);
        inductRequest.setSlots(slots);

        loadingGateService.induct(inductRequest);

        verify(omniChannelService).updateInventory(any(SkuId.class), any(Quantity.class));
        verify(machineOperatorService).moveTote(eq(tote.getToteId()), eq(AMBIENT));
        verify(locationService).removeToteFromLocation(eq(LocationId.LOADING_GATE));
    }

    @Test
    void shouldMoveChillSkuToChillZone() {
        //given
        setupZones();
        setupChillSku();
        Tote tote = setupOneEmptyTote();
        when(toteService.findToteByToteId(eq(EMPTY_TOTE_1))).thenReturn(Optional.of(tote));

        InductRequest inductRequest = new InductRequest();
        inductRequest.setToteId(EMPTY_TOTE_1);
        inductRequest.setId(EMPTY_TOTE_1_ID);
        List<StorageSlotModel> slots = new ArrayList<>();

        StorageSlotModel storageSlotModel = new StorageSlotModel();
        storageSlotModel.setOrdinal(0);
        storageSlotModel.setQuantity(Quantity.of(10));
        storageSlotModel.setSkuId(SKU_1);
        slots.add(storageSlotModel);
        inductRequest.setSlots(slots);

        loadingGateService.induct(inductRequest);

        verify(omniChannelService).updateInventory(any(SkuId.class), any(Quantity.class));
        verify(machineOperatorService).moveTote(eq(tote.getToteId()), eq(CHILL));
        verify(locationService).removeToteFromLocation(eq(LocationId.LOADING_GATE));
    }

    private void setupZones() {
        when(zoneService.getZone(ZoneFunction.LOADING_GATE))
                .thenReturn(new ZoneState(LOADING_GATE, TemperatureRegime.ANY, List.of(ZoneFunction.LOADING_GATE), Quantity.of(1)));
        when(zoneService.getZone(STORAGE, TemperatureRegime.AMBIENT))
                .thenReturn(new ZoneState(AMBIENT, TemperatureRegime.AMBIENT, List.of(STORAGE), Quantity.of(20)));
        when(zoneService.getZone(STORAGE, TemperatureRegime.CHILL))
                .thenReturn(new ZoneState(CHILL, TemperatureRegime.CHILL, List.of(STORAGE), Quantity.of(20)));
    }

    @Test
    void shouldInductTote() {
        //given
        setupZones();
        setupSku();
        Tote tote = setupOneEmptyTote();
        when(toteService.findToteByToteId(eq(EMPTY_TOTE_1))).thenReturn(Optional.of(tote));

        InductRequest inductRequest = new InductRequest();
        inductRequest.setToteId(EMPTY_TOTE_1);
        inductRequest.setId(EMPTY_TOTE_1_ID);
        List<StorageSlotModel> slots = new ArrayList<>();

        StorageSlotModel storageSlotModel = new StorageSlotModel();
        storageSlotModel.setOrdinal(0);
        storageSlotModel.setQuantity(Quantity.of(10));
        storageSlotModel.setSkuId(SKU_1);
        slots.add(storageSlotModel);
        inductRequest.setSlots(slots);

        loadingGateService.induct(inductRequest);

        assertThat(tote.getSlotByOrdinal(0)).isNotNull();
        assertThat(tote.getSlotByOrdinal(0).getStorageInventory()).isNotNull();
        assertThat(tote.getSlotByOrdinal(0).getStorageInventory().getSkuBatch()).isNotNull();
        assertThat(tote.getSlotByOrdinal(0).getStorageInventory().getSkuBatch().getSkuId()).isEqualTo(SKU_1);
        assertThat(tote.getSlotByOrdinal(0).getStorageInventory().getSkuBatch().getQuantity()).isEqualTo(Quantity.of(10));
    }

    @Test
    void shouldNotOverrideDeliveryInventoryOnInduct() {
        //given
        setupZones();
        setupSku();
        Tote tote = setupToteWithDeliveryInventory();
        DeliveryInventory deliveryInventory = tote.getSlotByOrdinal(1).getDeliveryInventory();
        when(toteService.findToteByToteId(eq(EMPTY_TOTE_1))).thenReturn(Optional.of(tote));

        InductRequest inductRequest = new InductRequest();
        inductRequest.setToteId(EMPTY_TOTE_1);
        inductRequest.setId(EMPTY_TOTE_1_ID);
        List<StorageSlotModel> slotModels = new ArrayList<>();

        StorageSlotModel storageSlotModel = new StorageSlotModel();
        storageSlotModel.setOrdinal(0);
        storageSlotModel.setQuantity(Quantity.of(10));
        storageSlotModel.setSkuId(SKU_1);
        slotModels.add(storageSlotModel);
        inductRequest.setSlots(slotModels);

        loadingGateService.induct(inductRequest);

        assertThat(tote.getSlotByOrdinal(1)).isNotNull();
        assertThat(tote.getSlotByOrdinal(1).getStorageInventory()).isNull();
        assertThat(tote.getSlotByOrdinal(1).getDeliveryInventory()).isNotNull();
        assertThat(tote.getSlotByOrdinal(1).getDeliveryInventory()).isEqualToComparingFieldByField(deliveryInventory);
    }

    private Tote setupToteWithDeliveryInventory() {
        Tote tote = setupOneEmptyTote();
        ArrayList<Slot> slots = new ArrayList<>();
        Slot deliverySlot = new Slot();
        DeliveryInventory deliveryInventory = new DeliveryInventory();
        List<SkuBatch> skuBatches = new ArrayList<>();
        SkuBatch skuBatch = new SkuBatch();
        skuBatch.setSkuId(SKU_1);
        skuBatch.setQuantity(Quantity.of(1));
        skuBatches.add(skuBatch);
        deliveryInventory.addSkuBatches(skuBatches);
        deliveryInventory.setOrderId(OrderId.generate());
        deliverySlot.setDeliveryInventory(deliveryInventory);
        deliverySlot.setOrdinal(1);
        slots.add(deliverySlot);
        tote.setSlots(slots);
        return tote;
    }

    @Test
    void requestTotes() {
        //given
        setupZones();
        setupSku();
        setupTotes();
        when(cubingService.determineBestToteType(any(DetermineBestToteTypeRequest.class))).thenReturn(CUBING_RESPONSE);
        when(toteService.getStorageToteTypesOfAvailableTotesWithEmptySlots()).thenReturn(Set.of(BIPARTITE_LOW));

        //when
        TotesForSkuRequest totesForSkuRequest = new TotesForSkuRequest(SKU_1, Quantity.of(10));
         Map<Tote, Quantity> toteQuantityMap = loadingGateService.requestTotes(totesForSkuRequest);

        //then
        assertThat(toteQuantityMap).hasSize(2);
        assertThat(toteQuantityMap).containsValues(Quantity.of(8), Quantity.of(4));
    }

    @Test
    void assertExceptionIsThrownIfNoTotesAreAvailable() {
        //given
        setupZones();
        setupSku();
        setupNoEmptyTotes();
        when(cubingService.determineBestToteType(any(DetermineBestToteTypeRequest.class))).thenReturn(CUBING_RESPONSE);

        //when
        TotesForSkuRequest totesForSkuRequest = new TotesForSkuRequest(SKU_1, Quantity.of(10));
        try {
            loadingGateService.requestTotes(totesForSkuRequest);
            fail("Expected to throw NoEmptyTotesException!");
        } catch (NoSpaceForStockException nete) {
            assertThat(nete).isExactlyInstanceOf(NoSpaceForStockException.class);
        }
    }

    private void setupNoEmptyTotes() {

    }

    private Tote setupOneEmptyTote() {
        return getEmptyTote(EMPTY_TOTE_1, EMPTY_TOTE_1_ID);
    }

    private void setupTotes() {
        Tote tote1 = getEmptyTote(EMPTY_TOTE_1, EMPTY_TOTE_1_ID);
        Tote tote2 = getEmptyTote(TOTE_2, TOTE_2_ID);
        fillSingleSlotIn(tote2);

        when(toteService.findAvailableStorageToteByTypeWithEmptySlot(BIPARTITE_LOW))
                .thenReturn(Optional.of(tote1))
                .thenReturn(Optional.of(tote2));
    }

    private void fillSingleSlotIn(Tote tote2) {
        Slot e1 = new Slot();
        SkuBatch skuBatch = new SkuBatch();
        skuBatch.setSkuId(SKU_1);
        skuBatch.setQuantity(Quantity.of(1));
        StorageInventory storageInventory = new StorageInventory();
        storageInventory.setSkuBatch(skuBatch);
        e1.setStorageInventory(storageInventory);
        tote2.setSlots(List.of(e1));
    }

    private Tote getEmptyTote(ToteId id1, Integer tote2Id) {
        Tote tote1 = new Tote();
        tote1.setToteType(BIPARTITE_LOW);
        tote1.setToteId(id1);
        tote1.setId(tote2Id);
        tote1.setToteOrientation(ToteOrientation.NORMAL);
        return tote1;
    }

    private void setupSku() {
        Sku sku = new Sku();
        sku.setSkuId(SKU_1);
        sku.setDistributionType(DistributionType.AMBIENT);
        sku.setDimensions(new Dimensions(1, 1, 1));
        when(skuService.getReadySku(SKU_1)).thenReturn(sku);
    }

    private void setupChillSku() {
        Sku sku = new Sku();
        sku.setSkuId(SKU_1);
        sku.setDistributionType(DistributionType.CHILLED);
        sku.setDimensions(new Dimensions(1, 1, 1));
        when(skuService.getReadySku(SKU_1)).thenReturn(sku);
    }
}