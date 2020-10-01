package inc.roms.rcs.api.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import inc.roms.rcs.api.error.ExceptionMappings;
import inc.roms.rcs.api.error.model.ApiErrorFactory;
import inc.roms.rcs.api.internal.supply.SupplyController;
import inc.roms.rcs.security.repository.UserRepository;
import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.inventory.SupplyService;
import inc.roms.rcs.service.inventory.ToteManagementService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.IssueService;
import inc.roms.rcs.service.issue.domain.repository.IssueRepository;
import inc.roms.rcs.service.omnichannel.IssueReporterService;
import inc.roms.rcs.service.operatorpanel.LoadingGateService;
import inc.roms.rcs.service.operatorpanel.ToteNotificationService;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.Mockito.when;

@Import({
        ApiErrorFactory.class,
        IssueFactory.class,
        IssueService.class
})
public class ApiInternalBaseMvcTest {

    @BeforeEach
    public void setup() {
        when(clock.instant()).thenReturn(Instant.ofEpochMilli(1000000000));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    }

    @Autowired
    protected MockMvc web;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected Clock clock;

    @MockBean
    protected OrderManagementService orderService;

    @MockBean
    protected IssueRepository issueRepository;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected IssueReporterService issueReporterService;

    @MockBean
    protected Validator validator;

    @MockBean
    protected ExceptionMappings exceptionMappings;

    @MockBean
    protected FeatureFlagService featureFlagService;

    @MockBean
    protected LoadingGateService loadingGateService;

    @MockBean
    protected ToteService toteService;

    @MockBean
    protected SupplyService supplyService;

    @MockBean
    protected ToteNotificationService toteNotificationService;

    @MockBean
    protected ToteManagementService toteManagementService;
}
