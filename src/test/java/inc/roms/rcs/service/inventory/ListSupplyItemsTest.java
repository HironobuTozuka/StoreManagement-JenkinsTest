package inc.roms.rcs.service.inventory;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.inventory.request.ScheduleSupplyRequest;
import inc.roms.rcs.service.inventory.response.ScheduleSupplyResponse;
import inc.roms.rcs.service.inventory.domain.model.ScheduledSupply;
import inc.roms.rcs.service.inventory.domain.repository.ScheduledSupplyRepository;
import inc.roms.rcs.service.inventory.response.ListSupplyItemsRequest;
import inc.roms.rcs.service.inventory.response.ListSupplyItemsResponse;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static inc.roms.rcs.vo.sku.DeliveryTurn.FIRST;
import static inc.roms.rcs.vo.sku.DistributionType.AMBIENT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = { "zonky.test.database.postgres.client.properties.currentSchema=sm", "inc.roms.machineoperator.enabled=false", "inc.roms.omnichannel.enabled=false", "inc.roms.usb.enabled=false" })
@AutoConfigureEmbeddedDatabase
public class ListSupplyItemsTest extends BaseIntegrationTest {

    private final ScheduledSupplyRepository scheduledSupplyRepository;
    private final SupplyService supplyService;

    @Autowired
    public ListSupplyItemsTest(OrderManagementService orderManagementService, ToteService toteService, SkuService skuService, TaskBundleService taskBundleService, OrderService orderService, ScheduledSupplyRepository scheduledSupplyRepository, SupplyService supplyService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
        this.scheduledSupplyRepository = scheduledSupplyRepository;
        this.supplyService = supplyService;
    }

    @Test
    @FlywayTest
    public void shouldFillItemsDetails() {
        skus();

        ScheduleSupplyRequest request = scheduleSuppliesRequest(DELIVERY_DATE, FIRST, AMBIENT, Quantity.of(10), SELL_BY_DATE, SKU_ID_1);
        ScheduleSupplyResponse scheduleSupplyResponse = supplyService.scheduleSupply(request);
        ScheduledSupply scheduledSupply = scheduleSupplyResponse.getDetails().getScheduledSupply();


        ListSupplyItemsResponse list = supplyService.list(new ListSupplyItemsRequest(scheduledSupply.getSupplyId()));

        assertThat(list.getItems()).hasSize(1);
        assertThat(list.getItems().get(0).getCategoryId()).isEqualTo(CATEGORY_1);
        assertThat(list.getItems().get(0).getSkuName()).isEqualTo(SKU_NAME_1);
        assertThat(list.getItems().get(0).getItemId()).isEqualTo(scheduledSupply.getItems().get(0).getScheduledSupplyItemId());
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
