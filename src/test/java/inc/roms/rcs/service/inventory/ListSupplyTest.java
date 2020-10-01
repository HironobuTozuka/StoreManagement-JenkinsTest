package inc.roms.rcs.service.inventory;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.inventory.domain.model.ScheduledSupply;
import inc.roms.rcs.service.inventory.domain.model.ScheduledSupplyItem;
import inc.roms.rcs.service.inventory.domain.repository.ScheduledSupplyRepository;
import inc.roms.rcs.service.inventory.request.ListSupplyRequest;
import inc.roms.rcs.service.inventory.response.ListSupplyResponse;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.DeliveryTurn;
import inc.roms.rcs.vo.sku.DistributionType;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.supply.SupplyId;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;

import static inc.roms.rcs.vo.sku.DeliveryTurn.FOURTH;
import static inc.roms.rcs.vo.sku.DistributionType.AMBIENT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = { "zonky.test.database.postgres.client.properties.currentSchema=sm", "inc.roms.machineoperator.enabled=false", "inc.roms.omnichannel.enabled=false", "inc.roms.usb.enabled=false" })
@AutoConfigureEmbeddedDatabase
public class ListSupplyTest extends BaseIntegrationTest {

    public static final SupplyId SUPPLY_ID = SupplyId.generate();
    public static final Clock FIXED_CLOCK = Clock.fixed(Instant.ofEpochMilli(10000000), ZoneOffset.UTC);
    public static final LocalDateTime SELL_BY_DATE = LocalDateTime.now(FIXED_CLOCK).plusDays(1);
    public static final LocalDate TODAY = LocalDate.now(FIXED_CLOCK);
    private final ScheduledSupplyRepository scheduledSupplyRepository;
    private final SupplyService supplyService;

    @Autowired
    public ListSupplyTest(OrderManagementService orderManagementService, ToteService toteService, SkuService skuService, TaskBundleService taskBundleService, OrderService orderService, ScheduledSupplyRepository scheduledSupplyRepository, SupplyService supplyService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
        this.scheduledSupplyRepository = scheduledSupplyRepository;
        this.supplyService = supplyService;
    }

    @Test
    @FlywayTest
    public void shouldFillAllSupplyDetails() {
        //given
        scheduledSupplies(SUPPLY_ID, AMBIENT, FOURTH, Quantity.of(30), SELL_BY_DATE, TODAY, SKU_ID_1);

        //when
        ListSupplyResponse response = supplyService.list(new ListSupplyRequest(false));

        //then
        assertThat(response.getSupply()).hasSize(1);
        assertThat(response.getSupply().get(0).getDeliveryTurn()).isEqualTo(FOURTH);
        assertThat(response.getSupply().get(0).getDistributionType()).isEqualTo(AMBIENT);
        assertThat(response.getSupply().get(0).getSupplyId()).isEqualTo(SUPPLY_ID);
    }

    @Test
    @FlywayTest
    public void shouldAggregateScheduledSuppliesForDifferentSkus() {
        //given
        scheduledSupplies(SUPPLY_ID, AMBIENT, FOURTH, Quantity.of(30), SELL_BY_DATE, TODAY, SKU_ID_1);

        //when
        ListSupplyResponse response = supplyService.list(new ListSupplyRequest(false));

        //then
        assertThat(response.getSupply()).hasSize(1);
        assertThat(response.getSupply().get(0).getDeliveryTurn()).isEqualTo(FOURTH);
        assertThat(response.getSupply().get(0).getDistributionType()).isEqualTo(AMBIENT);
        assertThat(response.getSupply().get(0).getSupplyId()).isEqualTo(SUPPLY_ID);
    }

    private void scheduledSupplies(SupplyId supplyId, DistributionType distributionType, DeliveryTurn deliveryTurn, Quantity quantity, LocalDateTime sellByDate, LocalDate today, SkuId skuId) {
        ScheduledSupply scheduledSupply = new ScheduledSupply();
        scheduledSupply.setDeliveryDate(today);
        scheduledSupply.setDeliveryTurn(deliveryTurn);
        scheduledSupply.setDistributionType(distributionType);
        scheduledSupply.setSupplyId(supplyId);
        ScheduledSupplyItem scheduledSupplyItem = new ScheduledSupplyItem();
        scheduledSupplyItem.setQuantity(quantity);
        scheduledSupplyItem.setSellByDate(sellByDate);
        scheduledSupplyItem.setSkuId(skuId);
        scheduledSupply.getItems().add(scheduledSupplyItem);
        scheduledSupplyRepository.save(scheduledSupply);
    }

}