package org.oddjob.arooa.logging;

public interface ThrowableProxy {

	
    String getMessage();

    String getClassName();

    StackTraceElement[] getStackTraceElementArray();

    ThrowableProxy getCause();

    ThrowableProxy[] getSuppressed();
}
