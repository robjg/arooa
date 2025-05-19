package org.oddjob.arooa.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class Log4j2AdapterTest {

    @Test
    void addAndRemoveOddjobAppender() {

        Logger logger = LogManager.getLogger("foo.bar");

        Log4j2Adapter adapter = new Log4j2Adapter();

        List<String> results = new ArrayList<>();

        Layout layout = adapter.layoutFor("%-5p [%t]: %m%n");

        Appender appender = loggingEvent -> {
            assertThat(loggingEvent.getMessage(), is("Hello Log4j2"));
            assertThat(loggingEvent.getLevel(), is(LogLevel.INFO));
            assertThat(loggingEvent.getLoggerName(), is("foo.bar"));
            results.add(layout.format(loggingEvent));
        };

        AppenderAdapter appenderAdapter = adapter.appenderAdapterFor(logger.getName());
        appenderAdapter.addAppender(appender);

        logger.info("Hello Log4j2");

        assertThat(results.size(), is(1));
        assertThat(results.get(0), is("INFO  [main]: Hello Log4j2" + System.lineSeparator()));

        appenderAdapter.removeAppender(appender);
    }
}