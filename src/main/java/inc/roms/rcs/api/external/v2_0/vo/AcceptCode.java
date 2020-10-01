package inc.roms.rcs.api.external.v2_0.vo;

import inc.roms.rcs.vo.common.ResponseCode;

import static inc.roms.rcs.vo.common.ResponseCode.ACCEPTED;

public enum AcceptCode {
    SUCCESS, ERROR;

    public static AcceptCode from(ResponseCode responseCode) {
        if (responseCode == ACCEPTED) {
            return SUCCESS;
        }
        return ERROR;
    }
}
