package inc.roms.rcs.service.operatorpanel.domain.barcode.usb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbServices;

@Component
@Slf4j
public class UsbFacade {

    public synchronized UsbServices getUsbServices() throws UsbException {
        return UsbHostManager.getUsbServices();
    }

}
