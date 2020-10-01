package inc.roms.rcs.api.external.v2_0.order;

import inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper;
import inc.roms.rcs.service.order.request.BatchOrderActionRequest;
import inc.roms.rcs.vo.common.StoreId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRequestResponseConverter_v2Test {

    public static final Instant NOW = LocalDateTime.of(2019, 1, 1, 10, 10, 10).toInstant(ZoneOffset.UTC);
    public static final StoreId STORE_CODE = StoreId.from("POC");

    private final OrderRequestResponseConverter_v2 converter = new OrderRequestResponseConverter_v2(STORE_CODE);
    private final TestingOrderRequestResponseFactory factory = new TestingOrderRequestResponseFactory();

    @BeforeAll
    public static void setup() {
        JapanTimeHelper.setClock(Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    public void shouldConvertCreateOrderRequestToBaseRequest() {
        //given
        CreateOrderRequest createOrderRequest = factory.createOrderRequest();
        inc.roms.rcs.service.order.request.CreateOrderRequest baseRequest = factory.createBaseOrderRequest();

        //when
        inc.roms.rcs.service.order.request.CreateOrderRequest conversionResult = converter.toBaseRequest(createOrderRequest);

        //then
        assertThat(conversionResult).isEqualToComparingFieldByField(baseRequest);
        baseRequest.validate();
    }

    @Test
    public void shouldConvertCreateOrderResponseFromBaseRequest() {
        //given
        CreateOrderResponse response = factory.createExpectedOrderResponse();
        inc.roms.rcs.service.order.response.CreateOrderResponse baseResponse = factory.createBaseOrderResponse();

        //when
        CreateOrderResponse convert = converter.convert(baseResponse);

        //then
        assertThat(convert).isEqualToComparingFieldByField(response);
    }

    @Test
    public void shouldConvertOrdersActionRequestToBatchActionRequest() {
        //given
        OrdersActionRequest_v2 request = factory.ordersActionRequest();
        BatchOrderActionRequest baseRequest = factory.createBatchOrderActionRequest(request.getTransactionId());

        //when
        BatchOrderActionRequest convert = converter.toBatchActionRequest(request);

        //then
        assertThat(convert).isEqualToComparingFieldByField(baseRequest);
    }
}