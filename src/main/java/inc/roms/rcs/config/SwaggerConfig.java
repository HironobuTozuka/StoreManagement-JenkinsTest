package inc.roms.rcs.config;

import inc.roms.rcs.vo.common.*;
import inc.roms.rcs.vo.issue.IssueId;
import inc.roms.rcs.vo.issue.Notes;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.sku.*;
import inc.roms.rcs.vo.supply.SupplyId;
import inc.roms.rcs.vo.task.TaskBundleId;
import inc.roms.rcs.vo.task.TaskId;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.zones.ZoneFunction;
import inc.roms.rcs.vo.zones.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)

                .directModelSubstitute(ExternalId.class, String.class)
                .directModelSubstitute(SkuId.class, String.class)
                .directModelSubstitute(OrderId.class, String.class)
                .directModelSubstitute(OrderLineId.class, String.class)
                .directModelSubstitute(Quantity.class, Number.class)
                .directModelSubstitute(UserId.class, String.class)
                .directModelSubstitute(StoreId.class, String.class)
                .directModelSubstitute(GateId.class, String.class)
                .directModelSubstitute(TaskBundleId.class, String.class)
                .directModelSubstitute(TaskId.class, String.class)
                .directModelSubstitute(ToteId.class, String.class)
                .directModelSubstitute(TransactionId.class, String.class)
                .directModelSubstitute(RcsOperationId.class, String.class)
                .directModelSubstitute(Notes.class, String.class)
                .directModelSubstitute(IssueId.class, String.class)
                .directModelSubstitute(SupplyId.class, String.class)
                .directModelSubstitute(Category.class, String.class)
                .directModelSubstitute(Name.class, String.class)
                .directModelSubstitute(ImageUrl.class, String.class)
                .directModelSubstitute(ZoneId.class, String.class)

                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build();
    }
}
