package inc.roms.rcs.service.operatorpanel.domain.barcode.model;

import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.operatorpanel.domain.barcode.config.BarcodeScannersUsbProperties;
import inc.roms.rcs.service.operatorpanel.domain.barcode.exception.WrongNumberOfUsbDevicesFoundException;
import inc.roms.rcs.service.operatorpanel.domain.barcode.usb.UsbFacade;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.usb4java.javax.Services;

import javax.usb.UsbDevice;
import javax.usb.UsbHub;
import javax.usb.UsbServices;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
@Slf4j
public class BarcodeScannerFactory {

    private final UsbFacade usbFacade;
    private final FeatureFlagService featureFlagService;

    public List<BarcodeScanner> createScanners(BarcodeScannersUsbProperties properties) {
        return properties.getDevices().stream()
                .map(this::findUsbDevices)
                .flatMap(Collection::stream)
                .map(this::createBarcodeScanner)
                .collect(toList());
    }

    private BarcodeScanner createBarcodeScanner(UsbDeviceWithProps deviceWithProps) {
        return new BarcodeScanner(
                deviceWithProps.getUsbDevice(),
                (byte) deviceWithProps.getProps().getOutputEndpointAddress(),
                (byte) deviceWithProps.getProps().getInterfaceAddress(),
                deviceWithProps.getProps().getIdProduct(),
                deviceWithProps.getProps().getIdVendor(),
                this
        );
    }

    @SneakyThrows
    public List<UsbDeviceWithProps> findUsbDevices(UsbDeviceProperties usbDeviceProperties) {
        UsbServices services = usbFacade.getUsbServices();
        if(services instanceof Services)
            ((Services) services).scan();
        UsbHub rootUsbHub = services.getRootUsbHub();
        log.info("Usb hub: " + rootUsbHub.toString());
        List<UsbDeviceWithProps> usbDevices = ((List<UsbDevice>) rootUsbHub.getAttachedUsbDevices())
                .stream()
                .flatMap(this::streamNestedUsbDevices)
                .peek(this::log)
                .filter(usbDeviceProperties::matches)
                .map(it -> new UsbDeviceWithProps(it, usbDeviceProperties))
                .collect(toList());

        if (featureFlagService.validationOfUsbDevicesEnabled())
            validate(usbDevices, usbDeviceProperties);

        return usbDevices;
    }

    private Stream<? extends UsbDevice> streamNestedUsbDevices(UsbDevice usbDevice) {
        if(usbDevice instanceof UsbHub) {
            return ((List<UsbDevice>)((UsbHub)usbDevice).getAttachedUsbDevices()).stream().flatMap(this::streamNestedUsbDevices);
        } else {
            return Stream.of(usbDevice);
        }
    }

    private void log(UsbDevice usbDevice) {
        try {
            log.info("Found usb device: {}: {}", usbDevice.getProductString(), usbDevice.getManufacturerString());
            log.info("Found usb device: {}: {}", usbDevice.getUsbDeviceDescriptor().idProduct(), usbDevice.getUsbDeviceDescriptor().idVendor());
            if(usbDevice.isUsbHub()) {
                log.info("Found usb device is an USB hub!");
                if(usbDevice instanceof UsbHub) {
                    UsbHub usbHub = (UsbHub) usbDevice;
                    ((List<UsbDevice>)usbHub.getAttachedUsbDevices()).forEach(this::log);
                }
            }
        } catch (Exception ex) {
            log.error("Cought Exception!", ex);
        }
    }

    private void validate(List<UsbDeviceWithProps> usbDevices, UsbDeviceProperties usbDeviceProperties) {
        log.info("Number of configured scanners: {}, number of found scanners: {}", usbDeviceProperties.getNumberOfScanners(), usbDevices.size());
        if (usbDevices.size() != usbDeviceProperties.getNumberOfScanners()) {
            throw new WrongNumberOfUsbDevicesFoundException(usbDeviceProperties, usbDevices.size());
        }
    }

    @Value
    public static class UsbDeviceWithProps {
        private final UsbDevice usbDevice;
        private final UsbDeviceProperties props;
    }
}
