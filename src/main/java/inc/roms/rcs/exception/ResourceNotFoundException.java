package inc.roms.rcs.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
@Getter
public class ResourceNotFoundException extends BusinessException {
    private final String resourceId;
    private final Class<?> resourceType;

    public ResourceNotFoundException(String resourceId, Class<?> resourceType) {
        super("Cannot find resource of type " + resourceType + " with id " +resourceId);
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.RESOURCE_NOT_FOUND;
    }
}
