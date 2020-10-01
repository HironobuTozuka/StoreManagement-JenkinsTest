package inc.roms.rcs.service.operatorpanel.domain.barcode.model;

import lombok.Data;

import javax.usb.UsbDevice;
import javax.usb.util.UsbUtil;

@Data
public class UsbDeviceProperties {

    private String idProduct;
    private String idVendor;

    private Integer numberOfScanners;

    private int outputEndpointAddress;
    private int interfaceAddress;

    public boolean matches(UsbDevice usbDevice) {
        return UsbUtil.toHexString(usbDevice.getUsbDeviceDescriptor().idVendor()).equalsIgnoreCase(idVendor)
                && UsbUtil.toHexString(usbDevice.getUsbDeviceDescriptor().idProduct()).equalsIgnoreCase(idProduct);
    }
}
