package org.oddjob.arooa.logging;

public interface AppenderAdapter {

	AppenderAdapter setLevel(LogLevel level);
	
	AppenderAdapter addAppender(Appender appender);
	
	AppenderAdapter removeAppender(Appender appender);

}
