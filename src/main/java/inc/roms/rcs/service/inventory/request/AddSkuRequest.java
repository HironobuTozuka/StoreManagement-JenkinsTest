package inc.roms.rcs.service.inventory.request;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.bean.CsvCustomBindByPosition;
import inc.roms.rcs.vo.common.TemperatureRegimeSku;
import inc.roms.rcs.vo.sku.*;
import lombok.Data;

@Data
public class AddSkuRequest {

    @CsvCustomBindByPosition(position = 0, required = true, converter = ExternalIdConverter.class)
    private ExternalId externalId;

    @CsvCustomBindByPosition(position = 2, required = true, converter = NameConverter.class)
    private Name name;

    @CsvCustomBindByPosition(position = 22, required = true, converter = SkuIdConverter.class)
    private SkuId skuId;

    @CsvCustomBindByPosition(position = 11, required = true, converter = ImageUrlConverter.class)
    private ImageUrl imageUrl;

    @CsvCustomBindByPosition(position = 12, converter = CategoryConverter.class)
    private Category category;

    @CsvCustomBindByPosition(position = 15, required = true, converter = TemperatureRegimeConverter.class)
    private DistributionType distributionType;

    public static class ExternalIdConverter<T, I> extends AbstractBeanField<T, I> {
        @Override
        protected Object convert(String value) {
            return ExternalId.from(value);
        }
    }

    public static class SkuIdConverter<T, I> extends AbstractBeanField<T, I> {
        @Override
        protected Object convert(String value) {
            return SkuId.from(value);
        }
    }

    public static class NameConverter<T, I> extends AbstractBeanField<T, I> {
        @Override
        protected Object convert(String value) {
            return Name.from(value);
        }
    }

    public static class CategoryConverter<T, I> extends AbstractBeanField<T, I> {
        @Override
        protected Object convert(String value) {
            return Category.from(value);
        }
    }

    public static class ImageUrlConverter<T, I> extends AbstractBeanField<T, I> {
        @Override
        protected Object convert(String value) {
            return ImageUrl.from(value);
        }
    }


    public static class TemperatureRegimeConverter<T, I> extends AbstractBeanField<T, I> {
        @Override
        protected Object convert(String value) {
            return DistributionType.valueOf(value);
        }
    }
}
