package inc.roms.rcs.service.inventory;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.inventory.request.ScheduleSupplyRequest;
import inc.roms.rcs.service.inventory.response.ScheduleSupplyResponse;
import inc.roms.rcs.service.inventory.domain.model.ScheduledSupply;
import inc.roms.rcs.service.inventory.domain.repository.ScheduledSupplyRepository;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.DeliveryTurn;
import inc.roms.rcs.vo.sku.DistributionType;
import inc.roms.rcs.vo.sku.SkuId;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.util.List;

import static inc.roms.rcs.vo.sku.DeliveryTurn.FIRST;
import static inc.roms.rcs.vo.sku.DistributionType.AMBIENT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = { "zonky.test.database.postgres.client.properties.currentSchema=sm", "inc.roms.machineoperator.enabled=false", "inc.roms.omnichannel.enabled=false", "inc.roms.usb.enabled=false" })
@AutoConfigureEmbeddedDatabase
public class SupplyServiceTest extends BaseIntegrationTest {

    private final ScheduledSupplyRepository scheduledSupplyRepository;
    private final SupplyService supplyService;

    @Autowired
    public SupplyServiceTest(OrderManagementService orderManagementService, ToteService toteService, SkuService skuService, TaskBundleService taskBundleService, OrderService orderService, ScheduledSupplyRepository scheduledSupplyRepository, SupplyService supplyService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
        this.scheduledSupplyRepository = scheduledSupplyRepository;
        this.supplyService = supplyService;
    }

    @Test
    @FlywayTest
    public void shouldCreateScheduledSupply() {
        //given
        skus();
        ScheduleSupplyRequest request = scheduleSuppliesRequest(DELIVERY_DATE, FIRST, AMBIENT, Quantity.of(10), SELL_BY_DATE, SKU_ID_1);
        ScheduleSupplyResponse scheduleSupplyResponse = supplyService.scheduleSupply(request);

        //when
        List<ScheduledSupply> scheduledSupplies = scheduledSupplyRepository.findAll();

        //then
        assertThat(scheduledSupplies).hasSize(1);
        assertThat(scheduledSupplies.get(0).getDeliveryDate()).isEqualTo(DELIVERY_DATE);
        assertThat(scheduledSupplies.get(0).getDeliveryTurn()).isEqualTo(FIRST);
        assertThat(scheduledSupplies.get(0).getDistributionType()).isEqualTo(AMBIENT);
        assertThat(scheduledSupplies.get(0).getSupplyId()).isNotNull();
    }

    @Test
    @FlywayTest
    public void shouldCreateItemsSupply() {
        //given
        skus();
        ScheduleSupplyRequest request = scheduleSuppliesRequest(DELIVERY_DATE, FIRST, AMBIENT, Quantity.of(10), SELL_BY_DATE, SKU_ID_1);
        ScheduleSupplyResponse scheduleSupplyResponse = supplyService.scheduleSupply(request);

        //when
        List<ScheduledSupply> scheduledSupplies = scheduledSupplyRepository.findAll();

        //then
        assertThat(scheduledSupplies).hasSize(1);
        assertThat(scheduledSupplies.get(0).getItems()).hasSize(1);
        assertThat(scheduledSupplies.get(0).getItems().get(0).getQuantity()).isEqualTo(Quantity.of(10));
        assertThat(scheduledSupplies.get(0).getItems().get(0).getSellByDate()).isEqualTo(SELL_BY_DATE);
        assertThat(scheduledSupplies.get(0).getItems().get(0).getSkuId()).isEqualTo(SKU_ID_1);
    }


    @Test
    @FlywayTest
    public void shouldAddItemsToExistingSupply() {
        //given
        skus();

        ScheduleSupplyRequest request = scheduleSuppliesRequest(DELIVERY_DATE, FIRST, AMBIENT, Quantity.of(10), SELL_BY_DATE, SKU_ID_1);
        ScheduleSupplyRequest otherRequest = scheduleSuppliesRequest(DELIVERY_DATE, FIRST, AMBIENT, Quantity.of(15), SELL_BY_DATE, SKU_ID_2);
        ScheduleSupplyResponse scheduleSupplyResponse = supplyService.scheduleSupply(request);
        ScheduleSupplyResponse otherScheduleSupplyResponse = supplyService.scheduleSupply(request);

        //when
        List<ScheduledSupply> scheduledSupplies = scheduledSupplyRepository.findAll();

        //then
        assertThat(scheduledSupplies).hasSize(1);
        assertThat(scheduledSupplies.get(0).getItems()).hasSize(2);
    }


    private ScheduleSupplyRequest scheduleSuppliesRequest(LocalDate deliveryDate, DeliveryTurn turn, DistributionType type, Quantity quantity, LocalDateTime sellByDate, SkuId sku) {
        return ScheduleSupplyRequest.builder()
                .deliveryDate(deliveryDate)
                .deliveryTurn(turn)
                .distributionType(type)
                .quantity(quantity)
                .sellByDate(sellByDate)
                .skuId(sku)
                .build();
    }
}