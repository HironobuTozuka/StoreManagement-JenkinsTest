package inc.roms.rcs.service.operatorpanel.domain.barcode.config;

import inc.roms.rcs.service.operatorpanel.domain.barcode.model.UsbDeviceProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "inc.roms.usb")
@Data
public class BarcodeScannersUsbProperties {

    private List<UsbDeviceProperties> devices;

}
