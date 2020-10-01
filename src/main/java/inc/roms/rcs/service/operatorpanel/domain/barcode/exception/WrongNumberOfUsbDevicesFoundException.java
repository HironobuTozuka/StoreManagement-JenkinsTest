package inc.roms.rcs.service.operatorpanel.domain.barcode.exception;

import inc.roms.rcs.service.operatorpanel.domain.barcode.model.UsbDeviceProperties;

public class WrongNumberOfUsbDevicesFoundException extends RuntimeException {

    public final UsbDeviceProperties properties;
    public final Integer foundDevices;

    public WrongNumberOfUsbDevicesFoundException(UsbDeviceProperties properties, Integer foundDevices) {
        super("Found " + foundDevices + " devices with " +
                "idVendor " + properties.getIdVendor() +
                ", idProduct " + properties.getIdProduct() +
                " , expected " + properties.getNumberOfScanners());
        this.properties = properties;
        this.foundDevices = foundDevices;
    }

}
