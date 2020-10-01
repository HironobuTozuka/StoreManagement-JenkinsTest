package inc.roms.rcs.service.mujin;

import inc.roms.rcs.vo.sku.SkuId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MujinService {

    private final MujinClient mujinClient;

    public boolean isRegistered(SkuId skuId) {
        try {
            return mujinClient.isRegistered(skuId);
        } catch (RuntimeException ex) {
            log.error("Mujin client error!", ex);
            return false;
        }
    }

}
