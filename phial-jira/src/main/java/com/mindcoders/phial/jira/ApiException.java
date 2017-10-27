package com.mindcoders.phial.jira;

import java.io.IOException;

/**
 * Created by rost on 10/26/17.
 */

class ApiException extends IOException {
    private final int code;
    private final String message;
    private final String body;

    ApiException(int code, String message, String body) {
        super("not successful(" + code + ")");
        this.code = code;
        this.message = message;
        this.body = body;
    }

    int getCode() {
        return code;
    }

    String getMessageText() {
        return message;
    }

    String getBody() {
        return body;
    }
}
