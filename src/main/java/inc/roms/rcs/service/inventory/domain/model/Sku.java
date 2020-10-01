package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.common.TemperatureRegime;
import inc.roms.rcs.vo.sku.SkuStatus;
import inc.roms.rcs.vo.sku.*;
import inc.roms.rcs.vo.common.Dimensions;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Entity
public class Sku {

    @EmbeddedId
    private SkuId skuId;

    private Name name;

    private MujinName mujinName;

    @Enumerated(EnumType.STRING)
    private SkuStatus status;

    private Category category;

    private ImageUrl imageUrl;

    private ExternalId externalId;

    private Dimensions dimensions;

    private Double weight;

    private Double maxAcc;

    @Enumerated(EnumType.STRING)
    private DistributionType distributionType;

}
