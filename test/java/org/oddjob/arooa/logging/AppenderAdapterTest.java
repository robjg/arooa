package org.oddjob.arooa.logging;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class AppenderAdapterTest {

	private static final Logger logger = LoggerFactory.getLogger(AppenderAdapterTest.class);
	

	@Test
	public void testAppenderReceivesMessage() {
		
		List<LoggingEvent> results = new ArrayList<>();

		Appender appender = new Appender() {

			@Override
			public void append(LoggingEvent event) {
				results.add(event);
			}
			
		};
		
		logger.info("Starting Test");
		
		LoggerAdapter.appenderAdapterFor(getClass()).addAppender(appender);
		
		logger.info("This should be captured");
		
		LoggerAdapter.appenderAdapterFor(getClass()).removeAppender(appender);
		
		logger.info("But this shouldn't");
		
		LoggerAdapter.appenderAdapterForRoot().addAppender(appender);
		
		logger.warn("And this should be captured");
		
		LoggerAdapter.appenderAdapterForRoot().removeAppender(appender);
		
		logger.info("But this shouldn't");
		
		assertThat(results.size(), equalTo(2));

		assertThat(results.get(0).getMessage(), equalTo("This should be captured"));
		assertThat(results.get(0).getLevel(), equalTo(LogLevel.INFO));
		
		assertThat(results.get(1).getMessage(), equalTo("And this should be captured"));
		assertThat(results.get(1).getLevel(), equalTo(LogLevel.WARN));
	}
	
	@Test
	public void testMDC() {
		
		List<String> results = new ArrayList<>();

		Appender appender = new Appender() {

			@Override
			public void append(LoggingEvent event) {
				results.add(event.getMdc("FOO"));
			}
			
		};

		AppenderAdapter test = LoggerAdapter
				.appenderAdapterForRoot()
				.addAppender(appender);
		
		MDC.put("FOO", "foo");
		
		logger.info("What MDC");
		
		MDC.remove("FOO");
		
		test.removeAppender(appender);
		
		assertThat(results.size(), equalTo(1));
		assertThat(results.get(0), equalTo("foo"));
	}
	
	@Test
	public void testLayout() {
		
		List<String> results = new ArrayList<>();

		Layout layout = LoggerAdapter.layoutFor("%m");
		
		Appender appender = new Appender() {

			@Override
			public void append(LoggingEvent event) {
				results.add(layout.format(event));
			}
			
		};

		AppenderAdapter appenderAdapter = LoggerAdapter
				.appenderAdapterForRoot()
				.addAppender(appender);
		
		logger.info("Lay me out.");
		
		appenderAdapter.removeAppender(appender);
		
		assertThat(results.size(), equalTo(1));
		assertThat(results.get(0), equalTo("Lay me out."));
	}

}
