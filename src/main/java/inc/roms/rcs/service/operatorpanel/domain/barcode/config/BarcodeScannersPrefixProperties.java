package inc.roms.rcs.service.operatorpanel.domain.barcode.config;

import inc.roms.rcs.vo.tote.TotePartitioning;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "inc.roms.scanners.prefix")
@Data
@Component
@Slf4j
public class BarcodeScannersPrefixProperties {

    private Map<TotePartitioning, PrefixToSlotMapping> totePartitions;
    private String handheld;

    public boolean isHandHeld(String prefix) {
        return handheld.equalsIgnoreCase(prefix);
    }

    public String getPrefix(TotePartitioning partitioning, Integer index) {
        return totePartitions.get(partitioning).mapping.stream().filter(it -> it.match(index)).findAny().orElseThrow().getPrefix();
    }

    public Integer getSlotIndex(TotePartitioning totePartitioning, String prefix) {
        log.info("Tote Partitioning: {}, prefix: {}", totePartitioning, prefix);
        return totePartitions.get(totePartitioning).getMapping()
                .stream()
                .filter(it -> it.match(prefix))
                .findAny()
                .orElseThrow()
                .getSlotIndex();
    }

    @Data
    public static class PrefixToSlotMapping {
        private List<PrefixToSlot> mapping;
    }

    @Data
    public static class PrefixToSlot {
        private String prefix;
        private Integer slotIndex;

        public boolean match(String prefix) {
            return prefix.equalsIgnoreCase(this.prefix);
        }

        public boolean match(Integer slotIndex) {
            return slotIndex.equals(this.slotIndex);
        }
    }
}
