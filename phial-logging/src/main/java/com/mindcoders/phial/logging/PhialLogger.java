package com.mindcoders.phial.logging;

import android.content.Context;
import android.util.Log;

import com.mindcoders.phial.Attacher;

import org.slf4j.LoggerFactory;

import java.io.File;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.html.HTMLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.StatusPrinter;

public class PhialLogger implements Attacher {
    private static final String LOG_PREFIX = "Log";
    private static final String PATTERN = "%d{HH:mm:ss.SSS}%thread%-5level%logger{36}%msg%n";
    private static final String HISTORY_FILE_NAME_PATTERN = ".%d{yyyy-MM-dd}.%i.html";
    private static final String LOG_DIR_NAME = "logs";
    private static final String MAX_FILE_SIZE = "5MB";

    private final File logDir;

    public PhialLogger(Context context) {
        logDir = createLogDir(context);
        final String logDirectory = logDir.getAbsolutePath();
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setContext(loggerContext);
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setFile(logDirectory + "/" + LOG_PREFIX + "-latest.html");

        SizeAndTimeBasedFNATP<ILoggingEvent> fileNamingPolicy = new SizeAndTimeBasedFNATP<>();
        fileNamingPolicy.setContext(loggerContext);
        fileNamingPolicy.setMaxFileSize(MAX_FILE_SIZE);

        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setFileNamePattern(logDirectory + "/" + LOG_PREFIX + HISTORY_FILE_NAME_PATTERN);
        rollingPolicy.setMaxHistory(5);
        rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(fileNamingPolicy);
        rollingPolicy.setParent(rollingFileAppender);  // parent and context required!
        rollingPolicy.start();

        HTMLLayout htmlLayout = new HTMLLayout();
        htmlLayout.setContext(loggerContext);
        htmlLayout.setPattern(PATTERN);
        htmlLayout.start();

        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(loggerContext);
        encoder.setLayout(htmlLayout);
        encoder.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);
        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);
        root.addAppender(rollingFileAppender);
        // print any status messages (warnings, etc) encountered in logback config
        StatusPrinter.print(loggerContext);
    }

    public void log(int priority, String tag, String message, Throwable t) {
        final org.slf4j.Logger logger = LoggerFactory.getLogger(tag);

        switch (priority) {
            case Log.VERBOSE:
                logger.trace(message, t);
                break;
            case Log.DEBUG:
                logger.debug(message, t);
                break;
            case Log.INFO:
                logger.info(message, t);
                break;
            case Log.WARN:
                logger.warn(message, t);
                break;
            case Log.ERROR:
                logger.error(message, t);
                break;
            default:
                logger.error("unexpected Log priority: " + priority + " " + message, t);
                break;
        }
    }

    private static File createLogDir(Context context) {
        final File directory = new File(context.getExternalFilesDir(null), LOG_DIR_NAME);
        directory.mkdir();
        return directory;
    }

    @Override
    public File provideAttachment() throws Exception {
        return logDir;
    }

    @Override
    public void onPreDebugWindowCreated() {

    }

    @Override
    public void onAttachmentNotNeeded() {

    }
}
