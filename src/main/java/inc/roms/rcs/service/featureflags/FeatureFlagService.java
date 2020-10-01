package inc.roms.rcs.service.featureflags;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeatureFlagService {

    @Value("${inc.roms.omnichannel.enabled:true}")
    private boolean omniChannelEnabled;

    @Value("${inc.roms.machineoperator.enabled:true}")
    private boolean machineOperatorEnabled;

    @Value("${inc.roms.usb.validate:true}")
    private boolean usbValidationEnabled;

    @Value("${inc.roms.usb.enabled:true}")
    private boolean usbDevicesEnabled;

    @Value("${inc.roms.security.enabled:false}")
    private boolean securityEnabled;

    @Value("${inc.roms.feature.delayed_pick_preorder}")
    private boolean delayedPickPreorder;

    public boolean isOmniChannelEnabled() {
        return omniChannelEnabled;
    }

    public boolean isMachineOperatorEnabled() {
        return machineOperatorEnabled;
    }

    public boolean validationOfUsbDevicesEnabled() {
        return usbValidationEnabled;
    }

    public boolean usbDevicesEnabled() {
        return usbDevicesEnabled;
    }

    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    public boolean isDelayedPickPreorder() {
        return delayedPickPreorder;
    }
}
