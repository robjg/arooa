package org.oddjob.arooa.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class LogbackAdapterTest {

    @Test
    void addAndRemoveOddjobAppender() {

        LoggerContext loggerContext = new LoggerContext();
        loggerContext.putProperty("logback.debug", "true");

        LogbackAdapter adapter = new LogbackAdapter(loggerContext);

        Logger logger = loggerContext.getLogger("foo.bar.Stuff");

        List<String> results = new ArrayList<>();

        Layout layout = adapter.layoutFor("%-5p [%t]: %m%n");

        Appender appender = loggingEvent -> {
            assertThat(loggingEvent.getLevel(), is(LogLevel.INFO));
            assertThat(loggingEvent.getLoggerName(), is("foo.bar.Stuff"));
            results.add(loggingEvent.getMessage());
        };

        AppenderAdapter appenderAdapter = adapter.appenderAdapterFor(logger.getName());
        appenderAdapter.addAppender(appender, layout);

        logger.info("Hello Log4j2");

        assertThat(results.size(), is(1));
        assertThat(results.get(0), is("INFO  [main]: Hello Log4j2" + System.lineSeparator()));

        appenderAdapter.removeAppender(appender);
    }
}