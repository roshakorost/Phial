package com.mindcoders.phial.logging;

import android.content.Context;
import android.util.Log;

import com.mindcoders.phial.Attacher;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

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

/**
 * Log Attacher. It writes logs using slf4j and logback to html log files, which will be included in Phial share attachment
 * <p>
 * You should not directly write logs in PhialLogger, because in these case both release and debug version
 * will be dependent on phial. Use some logging facade (e.g. Timber) and add PhialLogger to it only in debug build
 */
public class PhialLogger implements Attacher {
    private static final String LOG_PREFIX = "Log";
    private static final String PATTERN = "%d{HH:mm:ss.SSS}%thread%-5level%logger{36}%msg";
    private static final String HISTORY_FILE_NAME_PATTERN = ".%d{yyyy-MM-dd}.%i.html";
    private static final String LOG_DIR_NAME = "logs";
    private static final String MAX_FILE_SIZE = "5MB";
    private static final long CLEAR_LOGS_OLDER_THEN_MS = TimeUnit.DAYS.toMillis(1);

    private final File logDir;

    /**
     * Creates Logger that will write logs in html file using slf4j and logback.
     * The logs will be included in Phial Attachment
     *
     * @param context application context
     */
    public PhialLogger(Context context) {
        logDir = createLogDir(context);
        clearOldLogs(logDir);

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

    /**
     * Create new log entry in file.
     * <p>
     * You should not call it directly from your application build some facade (or use Timber) and set it to write to
     * PhialLogger.
     *
     * @param priority Log level. See {@link Log} for constants.
     * @param tag      Explicit or inferred tag. May be {@code null}.
     * @param message  Formatted log message. May be {@code null}.
     * @param t        Accompanying exceptions. May be {@code null}.
     */
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void clearOldLogs(File dir) {
        final File[] files = dir.listFiles();
        final long maxAge = System.currentTimeMillis() - CLEAR_LOGS_OLDER_THEN_MS;
        for (File file : files) {
            if (file.lastModified() < maxAge) {
                file.delete();
            }
        }
    }

    @Override
    public File provideAttachment() throws Exception {
        return logDir;
    }

    @Override
    public void onPreDebugWindowCreated() {
        //not interested when debug window is shown
    }

    @Override
    public void onAttachmentNotNeeded() {
        //not interested when debug window is closed
    }
}
