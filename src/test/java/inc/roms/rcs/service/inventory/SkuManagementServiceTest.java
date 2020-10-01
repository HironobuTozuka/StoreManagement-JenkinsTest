package inc.roms.rcs.service.inventory;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.inventory.request.ImportSkusRequest;
import inc.roms.rcs.service.inventory.response.ImportSkusResponse;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.sku.*;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Optional;

import static inc.roms.rcs.vo.common.ResponseCode.ACCEPTED;
import static inc.roms.rcs.vo.common.ResponseCode.REJECTED;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = { "zonky.test.database.postgres.client.properties.currentSchema=sm", "inc.roms.machineoperator.enabled=false", "inc.roms.omnichannel.enabled=false", "inc.roms.usb.enabled=false" })
@AutoConfigureEmbeddedDatabase
class SkuManagementServiceTest extends BaseIntegrationTest {

    @Value("classpath:service/inventory/sku_import.csv")
    private Resource skuImport;

    @Value("classpath:service/inventory/no_sku_id.csv")
    private Resource noSkuId;

    private final SkuManagementService skuManagementService;

    @Autowired
    public SkuManagementServiceTest(OrderManagementService orderManagementService, ToteService toteService, SkuService skuService, TaskBundleService taskBundleService, OrderService orderService, SkuManagementService skuManagementService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
        this.skuManagementService = skuManagementService;
    }

    @Test
    @FlywayTest
    public void shouldRespondToProperImportSkus() throws IOException {
        ImportSkusRequest importSkusRequest = new ImportSkusRequest(skuImport.getInputStream());
        ImportSkusResponse importSkusResponse = skuManagementService.importSkus(importSkusRequest);

        assertThat(importSkusResponse.getResponseCode()).isEqualTo(ACCEPTED);
        assertThat(importSkusResponse.getImportDetails().getExceptions()).isEmpty();
        assertThat(importSkusResponse.getImportDetails().getSuccessfulImports()).isEqualTo(1);
    }


    @Test
    @FlywayTest
    public void shouldSaveImportedSku() throws IOException {
        ImportSkusRequest importSkusRequest = new ImportSkusRequest(skuImport.getInputStream());
        ImportSkusResponse importSkusResponse = skuManagementService.importSkus(importSkusRequest);
        Optional<Sku> sku = skuService.getSku(SkuId.from("Product code"));

        assertThat(sku).isPresent();
        assertThat(sku.get().getName()).isEqualTo(Name.from("Product name"));
        assertThat(sku.get().getCategory()).isEqualTo(Category.from("Product category ID"));
        assertThat(sku.get().getExternalId()).isEqualTo(ExternalId.from("Product ID"));
        assertThat(sku.get().getImageUrl()).isEqualTo(ImageUrl.from("Product image URL"));
        assertThat(importSkusResponse.getImportDetails().getSuccessfulImports()).isEqualTo(1);
    }


    @Test
    @FlywayTest
    public void shouldReportMissingReqSkuManagementServiceTestuiredFields() throws IOException {
        ImportSkusRequest importSkusRequest = new ImportSkusRequest(noSkuId.getInputStream());
        ImportSkusResponse importSkusResponse = skuManagementService.importSkus(importSkusRequest);

        assertThat(importSkusResponse.getResponseCode()).isEqualTo(REJECTED);
        assertThat(importSkusResponse.getImportDetails().getExceptions()).hasSize(1);
        assertThat(importSkusResponse.getImportDetails().getSuccessfulImports()).isEqualTo(0);
    }
}