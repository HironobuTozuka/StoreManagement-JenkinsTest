package inc.roms.rcs.builders;

import inc.roms.rcs.service.inventory.domain.model.Reservation;

public class ReservationBuilder {

    private Integer id;
    private OrderLineBuilder orderLineBuilder;

    public static ReservationBuilder reservation() {
        return new ReservationBuilder();
    }

    public ReservationBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ReservationBuilder orderLineBuilder(OrderLineBuilder orderLineBuilder) {
        this.orderLineBuilder = orderLineBuilder;
        return this;
    }

    public Reservation build() {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        if(orderLineBuilder != null)
        reservation.setOrderLine(orderLineBuilder.build());
        return reservation;
    }
}
