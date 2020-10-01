package inc.roms.rcs.service.operatorpanel.domain.barcode.model;

import inc.roms.rcs.service.operatorpanel.domain.barcode.exception.BarcodeScannerInitializationException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.usb.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public class BarcodeScanner implements Runnable {

    private final UUID uuid = UUID.randomUUID();
    private UsbDevice usbDevice;
    private final byte outputEndpointAddress;
    private final byte usbInterfaceAddress;
    private final String idProduct;
    private final String idVendor;
    private final BarcodeScannerFactory factory;

    private UsbPipe pipe;
    private List<BarcodeScannerListener> barcodeScannerListeners = new ArrayList<>();
    private UsbInterface usbInterface;
    private boolean aborted = false;

    public BarcodeScanner(UsbDevice usbDevice, byte outputEndpointAddress, byte interfaceAddress, String idProduct, String idVendor, BarcodeScannerFactory barcodeScannerFactory) {
        this.usbDevice = usbDevice;
        this.outputEndpointAddress = outputEndpointAddress;
        this.usbInterfaceAddress = interfaceAddress;
        this.idProduct = idProduct;
        this.idVendor = idVendor;
        this.factory = barcodeScannerFactory;
    }


    @SneakyThrows
    public boolean connectWithPhysicalDevice() {
        if(usbInterface == null || !usbInterface.isClaimed()) {
            usbInterface = usbDevice.getActiveUsbConfiguration().getUsbInterface(usbInterfaceAddress);
            boolean claimed = safeClaimInterface(usbInterface);
            long counter = 0;
            while (!claimed && counter++ < 100) {
                claimed = safeClaimInterface(usbInterface);
                Thread.sleep(50);
            }
            if (!claimed) {
                throw new BarcodeScannerInitializationException();
            } else {
                safetyReclaim(counter);
            }
        }

        UsbEndpoint endpoint = usbInterface.getUsbEndpoint(outputEndpointAddress);
        pipe = endpoint.getUsbPipe();
        pipe.open();
        return true;
    }

    private void safetyReclaim(long counter) throws InterruptedException {
        boolean claimed;
        if (!usbInterface.isClaimed()) {
            claimed = false;
            while (!claimed && counter++ < 100) {
                claimed = safeClaimInterface(usbInterface);
                Thread.sleep(50);
            }
            if (!claimed) {
                throw new BarcodeScannerInitializationException();
            }
        }
    }


    private boolean safeClaimInterface(UsbInterface usbInterface) {
        try {
            usbInterface.claim(it -> true);
            log.info("{}: {}, Interface claimed", usbDevice, uuid);
            return true;
        } catch (UsbException e) {
            log.debug("Exception on interface claim!", e);
        }

        return false;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            Optional<Barcode> scan = scan();
            scan.ifPresent(this::notifyAboutBarcode);
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("{}: {}, Thread was interrupted, aborting all submissions", usbDevice, uuid);
        pipe.abortAllSubmissions();
        log.info("{}: {}, Thread was interrupted, exiting...", usbDevice, uuid);
    }

    private synchronized void notifyAboutBarcode(Barcode barcode) {
        barcodeScannerListeners.forEach(it -> it.notifyAboutBarcode(barcode));
    }

    public synchronized void registerListener(BarcodeScannerListener listener) {
        barcodeScannerListeners.add(listener);
    }

    public synchronized void cleanListeners() {
        barcodeScannerListeners = new ArrayList<>();
    }

    public Optional<Barcode> scan() {
        try {
            log.info("{}: {}, Barcode scan method called", usbDevice, uuid);
            return readFullBarcodeFrom();
        } catch (UsbAbortException uae) {
            return Optional.empty();
        } catch (UsbException e) {
            log.warn(usbDevice + ": " + uuid + ", Exception while reading barcode", e);
            log.info("{}: {}, Trying to reconnect...", usbDevice, uuid);
//            close();
            log.info("{}: {}, Closed interface and pipe...", usbDevice, uuid);
            while(!recreateDevice()) {
                try {
                    log.warn("{}: {}, Couldn't recreate device, waiting for 2000 ms before retry", usbDevice, uuid);
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            log.info("{}: {}, Reconnected!", usbDevice, uuid);
        }
        return Optional.empty();
    }

    private boolean recreateDevice() {
        try {
            UsbDeviceProperties usbDeviceProperties = new UsbDeviceProperties();
            usbDeviceProperties.setIdProduct(idProduct);
            usbDeviceProperties.setIdVendor(idVendor);
            List<BarcodeScannerFactory.UsbDeviceWithProps> usbDevicesWithProps = factory.findUsbDevices(usbDeviceProperties).stream()
                    .filter(it -> !it.getUsbDevice().getActiveUsbConfiguration().getUsbInterface(usbInterfaceAddress).isClaimed())
                    .collect(toList());

            log.info("{}, {}: Found not claimed usb devices: {}", usbDevice, uuid, usbDevicesWithProps);
            for(BarcodeScannerFactory.UsbDeviceWithProps usbDeviceWithProps : usbDevicesWithProps) {
                this.usbDevice = usbDeviceWithProps.getUsbDevice();
                this.usbInterface = null;
                this.pipe = null;

                if(connectWithPhysicalDevice()) {
                    log.info("{}, {}: Successfully reconnected, breaking out of a loop!", usbDevice, uuid);
                    return true;
                }
            }
        } catch (Exception e) {
            log.warn("{}: {}, Exception (message: {}) while recreating USB device", usbDevice, uuid, e.getMessage());
            return false;
        }

        return true;
    }

    private Optional<Barcode> readFullBarcodeFrom() throws UsbException {
        Barcode.BarcodeBuilder barcodeBuilder = Barcode.builder();
        log.info("{}: {}, Thread name in scanner: {}", usbDevice, uuid, Thread.currentThread().getName());
        while (!barcodeBuilder.isFinished()) {
            BarcodeCharacter character = readSingleCharacter();
            barcodeBuilder.append(character);
            if (Thread.currentThread().isInterrupted() || aborted) {
                log.info("{}: {}, Thread interrupted!", usbDevice, uuid);
                return Optional.empty();
            }
        }
        Barcode barcode = barcodeBuilder.build();
        log.info("{}: {}, Scanned barcode: {}", usbDevice, uuid, barcode);
        return Optional.of(barcode);
    }

    private BarcodeCharacter readSingleCharacter() throws UsbException {
        byte[] data = new byte[16];
        pipe.syncSubmit(data);

        if (isNotEmpty(data)) {
            log.info("{}: {}, received {} data", usbDevice, uuid, data);
        }

        return ConversionTable.decode(data);
    }

    private boolean isNotEmpty(byte[] data) {
        for (byte b : data) {
            if (b != 0) return true;
        }
        return false;
    }

    public boolean matches(UsbDeviceProperties usbDeviceProperties) {
        return usbDeviceProperties.matches(this.usbDevice);
    }

    public boolean close() {
        this.aborted = true;
        boolean result = true;
        log.info("{}: {}, checking if pipe is open, if yes - close it", usbDevice, uuid);
        if (pipe.isOpen()) {
            result = closePipe();
            log.info("{}: {}, trying to close pipe, result is: {}", usbDevice, uuid, result);
        }
        if (usbInterface.isClaimed()) {
            boolean interfaceReleased = releaseInterface();
            result = result && interfaceReleased;
            log.info("{}: {}, trying to release interface, result is: {}", usbDevice, uuid, interfaceReleased);
        }
        log.info("{}: {}, trying to cleanup usbDevice, result is: {}", usbDevice, uuid, result);
        this.aborted = false;
        return result;
    }

    private boolean releaseInterface() {
        try {
            if (usbInterface.isClaimed()) {
                log.info("{}: {}, interface is claimed, trying to release", usbDevice, uuid);
                usbInterface.release();
                log.info("{}: {}, interface released", usbDevice, uuid);
            }
            log.info("{}: {}, interface is not claimed", usbDevice, uuid);
            return true;
        } catch (UsbException e) {
            log.error(usbDevice + ": " + uuid + ", Couldn't release interface!", e);
        }
        return false;
    }

    private boolean closePipe() {
        try {
            if (pipe.isOpen()) {
                log.info("{}: {}, pipe is open, aborting all submissions", usbDevice, uuid);
                pipe.abortAllSubmissions();
                log.info("{}: {}, pipe is open, submissions aborted", usbDevice, uuid);
                pipe.close();
                log.info("{}: {}, pipe closed", usbDevice, uuid);
            }
            log.info("{}: {}, pipe is not open", usbDevice, uuid);
            return true;
        } catch (UsbException e) {
            log.error(usbDevice + ": " + uuid + ", Couldn't close pipe!", e);
        }
        return false;
    }
}
