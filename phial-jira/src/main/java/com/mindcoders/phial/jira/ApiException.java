package com.mindcoders.phial.jira;

import java.io.IOException;

/**
 * Created by rost on 10/26/17.
 */

public class ApiException extends IOException {
    private final int code;
    private final String message;
    private final String body;

    public ApiException(int code, String message, String body) {
        super("not successful(" + code + ")");
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
