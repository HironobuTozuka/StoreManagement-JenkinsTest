package inc.roms.rcs.config;

import inc.roms.rcs.config.converters.NumberToQuantityConverter;
import inc.roms.rcs.config.mappers.SnakeCaseArgumentMapper;
import inc.roms.rcs.vo.common.RcsOperationId;
import inc.roms.rcs.vo.common.StoreId;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.issue.IssueId;
import inc.roms.rcs.vo.issue.Notes;
import inc.roms.rcs.vo.sku.*;
import inc.roms.rcs.vo.supply.SupplyId;
import inc.roms.rcs.vo.task.TaskBundleId;
import inc.roms.rcs.vo.task.TaskId;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.common.UserId;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SnakeCaseArgumentMapper snakeCaseArgumentResolver;

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {
        formatterRegistry.addConverter(new NumberToQuantityConverter());

        formatterRegistry.addConverter(String.class, SkuId.class, SkuId::new);
        formatterRegistry.addConverter(String.class, ToteId.class, ToteId::new);
        formatterRegistry.addConverter(String.class, GateId.class, GateId::new);
        formatterRegistry.addConverter(String.class, UserId.class, UserId::new);
        formatterRegistry.addConverter(String.class, OrderId.class, OrderId::new);
        formatterRegistry.addConverter(String.class, OrderLineId.class, OrderLineId::new);
        formatterRegistry.addConverter(String.class, ExternalId.class, ExternalId::new);
        formatterRegistry.addConverter(String.class, StoreId.class, StoreId::new);
        formatterRegistry.addConverter(String.class, TaskId.class, TaskId::new);
        formatterRegistry.addConverter(String.class, TaskBundleId.class, TaskBundleId::new);
        formatterRegistry.addConverter(String.class, TransactionId.class, TransactionId::new);
        formatterRegistry.addConverter(String.class, RcsOperationId.class, RcsOperationId::new);
        formatterRegistry.addConverter(String.class, Notes.class, Notes::new);
        formatterRegistry.addConverter(String.class, IssueId.class, IssueId::new);
        formatterRegistry.addConverter(String.class, SupplyId.class, SupplyId::new);
        formatterRegistry.addConverter(String.class, Category.class, Category::new);
        formatterRegistry.addConverter(String.class, Name.class, Name::new);
        formatterRegistry.addConverter(String.class, ImageUrl.class, ImageUrl::new);
        formatterRegistry.addConverter(String.class, ZoneId.class, ZoneId::new);
    }


    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(snakeCaseArgumentResolver);
    }
}
