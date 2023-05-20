package org.csu.api.common;

import lombok.Getter;

@Getter
public enum ResponseCode {

    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    ARGUMENT_INVALID(10, "ARGUMENT INVALID"),
    NEED_LOGIN(11, "NEED LOGIN");

    private final Integer code;
    private final String description;

    ResponseCode(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
