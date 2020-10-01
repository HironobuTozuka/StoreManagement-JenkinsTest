package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class SkuBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private SkuId skuId;

    private Quantity quantity;

    private LocalDateTime sellByDate;

    @Enumerated(EnumType.STRING)
    private SkuBatchState state;

    public SkuBatch(SkuId skuId) {
        this(skuId, Quantity.of(0));
    }

    public SkuBatch(SkuId skuId, Quantity quantity) {
        this.skuId = skuId;
        this.quantity = quantity;
    }

    @PrePersist
    @PreUpdate
    public void correctSku() {
        if (skuId != null && skuId.getSkuId().isEmpty()) {
            skuId = null;
        }

        if (quantity == null || quantity.getQuantity() == null) {
            quantity = Quantity.of(0);
        }
    }

    public Quantity getQuantity() {
        return quantity != null ? quantity : Quantity.of(0);
    }

    public void add(Quantity quantity) {
        if(this.quantity == null) {
            this.quantity = Quantity.of(0);
        }

        this.quantity = this.quantity.plus(quantity);
    }

    public void subtract(Quantity picked) {
        if(this.quantity == null) {
            this.quantity = Quantity.of(0);
        }

        this.quantity = this.quantity.minus(picked);
    }
}
