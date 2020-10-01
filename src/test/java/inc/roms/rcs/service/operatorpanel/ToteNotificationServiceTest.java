package inc.roms.rcs.service.operatorpanel;

import inc.roms.rcs.service.operatorpanel.request.ToteNotificationRequest;
import inc.roms.rcs.vo.location.LocationId;
import inc.roms.rcs.vo.tote.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ToteNotificationServiceTest {

    private final NoReadNotificationHandler noReadNotificationHandler = mock(NoReadNotificationHandler.class);
    private final TechnicalLocationToteHandler technicalLocationToteHandler  = mock(TechnicalLocationToteHandler.class);
    private final LoadingGateUnknownToteHandler loadingGateUnknownToteHandler = mock(LoadingGateUnknownToteHandler.class);
    private final NoToteHandler noToteHandler = mock(NoToteHandler.class);

    private final ToteNotificationService toteNotificationService = new ToteNotificationService(loadingGateUnknownToteHandler, noReadNotificationHandler, technicalLocationToteHandler, noToteHandler);

    @Test
    public void shouldCallUnknownToteHandlerForLoadingGateTote() {
        ToteNotificationRequest request = request();

        toteNotificationService.handle(request);

        verify(loadingGateUnknownToteHandler).handle(request);
    }

    @Test
    public void shouldCallNoReadHandlerForNoReadNotification() {
        ToteNotificationRequest request = noRead();

        toteNotificationService.handle(request);

        verify(noReadNotificationHandler).handle(request);
    }

    @Test
    public void shouldCallTechLocatioHandler() {
        ToteNotificationRequest request = techLocation();

        toteNotificationService.handle(request);

        verify(technicalLocationToteHandler).handle(request);
    }

    @Test
    public void shouldCallNoToteForNoToteNotification() {
        ToteNotificationRequest request = noTote();

        toteNotificationService.handle(request);

        verify(noToteHandler).handle(request);
    }

    @NotNull
    private ToteNotificationRequest request() {
        return new ToteNotificationRequest(
                    ToteId.from("0001"),
                    TotePartitioning.BIPARTITE,
                    ToteHeight.HIGH,
                    ToteOrientation.NORMAL,
                    null,
                    LocationId.LOADING_GATE
            );
    }

    @NotNull
    private ToteNotificationRequest noRead() {
        return new ToteNotificationRequest(
                ToteId.from("0001"),
                TotePartitioning.BIPARTITE,
                ToteHeight.HIGH,
                ToteOrientation.NORMAL,
                ToteStatus.NO_READ,
                LocationId.AMBIENT
        );
    }

    @NotNull
    private ToteNotificationRequest noTote() {
        return new ToteNotificationRequest(
                ToteId.NOTOTE,
                TotePartitioning.UNKNOWN,
                ToteHeight.UNKNOWN,
                ToteOrientation.UNKNOWN,
                ToteStatus.READY,
                LocationId.LOADING_GATE
        );
    }

    @NotNull
    private ToteNotificationRequest techLocation() {
        return new ToteNotificationRequest(
                ToteId.NOREAD,
                TotePartitioning.UNKNOWN,
                ToteHeight.UNKNOWN,
                ToteOrientation.UNKNOWN,
                ToteStatus.READY,
                LocationId.TECHNICAL
        );
    }
}