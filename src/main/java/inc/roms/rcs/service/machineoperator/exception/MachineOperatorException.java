package inc.roms.rcs.service.machineoperator.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MachineOperatorException extends BusinessException {
    public MachineOperatorException(RuntimeException ise) {
        super(ise);
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.MHE_EXCEPTION;
    }
}
