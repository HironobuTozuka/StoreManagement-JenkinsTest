package inc.roms.rcs.service.operatorpanel.domain.barcode.model;

import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.operatorpanel.domain.barcode.config.BarcodeScannersUsbProperties;
import inc.roms.rcs.service.operatorpanel.domain.barcode.exception.WrongNumberOfUsbDevicesFoundException;
import inc.roms.rcs.service.operatorpanel.domain.barcode.usb.UsbFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.usb.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BarcodeScannerFactoryTest {

    private final UsbFacade usbFacade = mock(UsbFacade.class);
    private final FeatureFlagService featureFlagService = mock(FeatureFlagService.class);
    private final BarcodeScannerFactory factory = new BarcodeScannerFactory(usbFacade, featureFlagService);
    private final UsbHub usbHub = mock(UsbHub.class);

    @BeforeEach
    public void setup() throws UsbException {
        UsbServices usbServices = mock(UsbServices.class);

        when(usbFacade.getUsbServices()).thenReturn(usbServices);
        when(usbServices.getRootUsbHub()).thenReturn(usbHub);
        when(featureFlagService.validationOfUsbDevicesEnabled()).thenReturn(true);
        when(featureFlagService.usbDevicesEnabled()).thenReturn(true);
    }

    @Test
    public void shouldCreateSingleScanner() {
        //given
        UsbDeviceProperties scannerProperties = getScannerProperties("5555", "3333");
        UsbDevice usbDevice = getUsbScanner("5555", "3333");
        configureUsbHubWith(usbDevice);

        BarcodeScannersUsbProperties props = getProperties(scannerProperties);

        //when
        List<BarcodeScanner> scanners = factory.createScanners(props);

        //then
        assertThat(scanners).hasSize(1);
    }

    @Test
    public void shouldThrowExceptionIfNumberOfScannersDontMatch() {
        //given
        UsbDeviceProperties scannerProperties = getScannerProperties("5555", "3333");
        UsbDevice usbDevice1 = getUsbScanner("5555", "3333");
        UsbDevice usbDevice2 = getUsbScanner("5555", "3333");
        configureUsbHubWith(usbDevice1, usbDevice2);

        BarcodeScannersUsbProperties props = getProperties(scannerProperties);
        //when

        //then
        assertThatExceptionOfType(WrongNumberOfUsbDevicesFoundException.class).isThrownBy(() -> factory.createScanners(props));
    }


    @Test
    public void shouldCreateMultipleScannerTypes() {
        //given
        UsbDeviceProperties scannerProperties1 = getScannerProperties("5555", "3333");
        UsbDeviceProperties scannerProperties2 = getScannerProperties("5554", "3334");
        UsbDevice usbDevice1 = getUsbScanner("5555", "3333");
        UsbDevice usbDevice2 = getUsbScanner("5554", "3334");
        configureUsbHubWith(usbDevice1, usbDevice2);

        BarcodeScannersUsbProperties props = getProperties(scannerProperties1, scannerProperties2);

        //when
        List<BarcodeScanner> scanners = factory.createScanners(props);

        //then
        assertThat(scanners).hasSize(2);
    }

    private void configureUsbHubWith(UsbDevice... usbDevice) {
        List<UsbDevice> usbDevices = new ArrayList<>(Arrays.asList(usbDevice));
        when(usbHub.getAttachedUsbDevices()).thenReturn(usbDevices);
    }

    public BarcodeScannersUsbProperties getProperties(UsbDeviceProperties... properties) {
        BarcodeScannersUsbProperties props = new BarcodeScannersUsbProperties();
        props.setDevices(new ArrayList<>());

        for (UsbDeviceProperties property : properties) {
            props.getDevices().add(property);
        }

        return props;
    }

    private UsbDeviceProperties getScannerProperties(String idProduct, String idVendor) {
        UsbDeviceProperties properties = new UsbDeviceProperties();

        properties.setIdProduct(idProduct);
        properties.setIdVendor(idVendor);
        properties.setInterfaceAddress(0);
        properties.setOutputEndpointAddress(0x81);
        properties.setNumberOfScanners(1);
        return properties;
    }

    private UsbDevice getUsbScanner(String idProduct, String idVendor) {
        UsbDevice usbDevice = mock(UsbDevice.class);
        UsbDeviceDescriptor usbDeviceDescriptor = mock(UsbDeviceDescriptor.class);
        when(usbDevice.getUsbDeviceDescriptor()).thenReturn(usbDeviceDescriptor);
        when(usbDeviceDescriptor.idProduct()).thenReturn(Short.decode("0x" + idProduct));
        when(usbDeviceDescriptor.idVendor()).thenReturn(Short.decode("0x" + idVendor));
        return usbDevice;
    };
}