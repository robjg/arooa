package org.oddjob.arooa.logging;


public interface LoggingEvent {

    LogLevel getLevel();

    String getMdc(String mdc);
    
    String getLoggerName();

    String getMessage();

    String getThreadName();

    Object[] getArgumentArray();

    long getTimeStamp();

    Throwable getThrowable();
}
