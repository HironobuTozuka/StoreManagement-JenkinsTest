package inc.roms.rcs.service.mujin;

import inc.roms.rcs.service.mujin.config.MujinProperties;
import inc.roms.rcs.vo.sku.Name;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;

@Service
@RequiredArgsConstructor
public class MujinClient {

    private final MujinProperties mujinProperties;
    private final RestTemplate mujinRestTemplate;

    public boolean isRegistered(SkuId skuId) {
        if(mujinProperties.isEnabled()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBasicAuth(mujinProperties.getUsername(), mujinProperties.getPassword());
            HttpEntity entity = new HttpEntity(httpHeaders);
            ResponseEntity<List> response = mujinRestTemplate.exchange("/query/barcodes?barcodes={barcodes}", GET, entity, List.class, Map.of("barcodes", skuId));
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Request Successful.");
                return response.getBody() != null && !response.getBody().isEmpty();
            } else {
                throw new RuntimeException();
            }
        } else {
            return true;
        }
    }
}
