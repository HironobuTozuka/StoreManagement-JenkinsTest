package inc.roms.rcs.api.external.v2_0;

import com.fasterxml.jackson.databind.ObjectMapper;
import inc.roms.rcs.api.error.ExceptionMappings;
import inc.roms.rcs.api.error.model.ApiErrorFactory;
import inc.roms.rcs.security.repository.UserRepository;
import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.issue.domain.repository.IssueRepository;
import inc.roms.rcs.service.omnichannel.IssueReporterService;
import inc.roms.rcs.service.operatorpanel.LoadingGateService;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Import({ApiV2TestConfig.class})
public class ApiV2BaseMvcTest {
    @Autowired
    protected MockMvc web;

    @Autowired
    protected ObjectMapper objectMapper;

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
    protected ApiErrorFactory apiErrorFactory;

    @MockBean
    protected ExceptionMappings exceptionMappings;

    @MockBean
    protected FeatureFlagService featureFlagService;

    @MockBean
    protected LoadingGateService loadingGateService;
}
