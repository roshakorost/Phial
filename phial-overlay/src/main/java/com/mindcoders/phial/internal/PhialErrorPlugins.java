package com.mindcoders.phial.internal;

/**
 * Created by rost on 10/23/17.
 */

public class PhialErrorPlugins {
    public interface ErrorHandler {
        void onError(Throwable throwable);
    }

    private static ErrorHandler handler;

    public static void setHandler(ErrorHandler handler) {
        PhialErrorPlugins.handler = handler;
    }

    public static void onError(Throwable throwable) {
        if (handler != null) {
            handler.onError(throwable);
        }
    }
}
