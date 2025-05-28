package org.oddjob.arooa.logging;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


class AppenderAdapterTest {

    private static final Logger logger = LoggerFactory.getLogger(AppenderAdapterTest.class);

    @BeforeAll
    static void before() {
        logger.info("Slf4j implementation is {}", LoggerFactory.getILoggerFactory());
    }

    @Test
    void testAppenderReceivesMessage() {

        List<LoggingEvent> results = new ArrayList<>();

        Layout layout = LoggerAdapter.layoutFor("%m");

        Appender appender = results::add;

        logger.info("Starting Test");

        LoggerAdapter.appenderAdapterFor(getClass())
                .addAppender(appender, layout);

        logger.info("This should be captured");

        LoggerAdapter.appenderAdapterFor(getClass())
                .removeAppender(appender);

        logger.info("But this shouldn't");

        LoggerAdapter.appenderAdapterForRoot()
                .addAppender(appender, layout);

        logger.warn("And this should be captured");

        LoggerAdapter.appenderAdapterForRoot()
                .removeAppender(appender);

        logger.info("But this shouldn't");

        assertThat(results.toString(), results.size(), is(2));

        assertThat(results.get(0).getMessage(), is("This should be captured"));
        assertThat(results.get(0).getLevel(), is(LogLevel.INFO));

        assertThat(results.get(1).getMessage(), is("And this should be captured"));
        assertThat(results.get(1).getLevel(), is(LogLevel.WARN));
    }

    @Test
    void testMDC() {

        List<String> results = new ArrayList<>();

        Layout layout = LoggerAdapter.layoutFor("%m");

        Appender appender = new Appender() {

            @Override
            public void append(LoggingEvent event) {
                results.add(event.getMdc("FOO"));
            }

        };

        AppenderAdapter test = LoggerAdapter
                .appenderAdapterForRoot()
                .addAppender(appender, layout);

        MDC.put("FOO", "foo");

        logger.info("What MDC");

        MDC.remove("FOO");

        test.removeAppender(appender);

        assertThat(results.size(), is(1));
        assertThat(results.get(0), is("foo"));
    }

    @Test
    void testLayout() {

        List<String> results = new ArrayList<>();

        Layout layout = LoggerAdapter.layoutFor("%message");

        Appender appender = new Appender() {

            @Override
            public void append(LoggingEvent event) {

                results.add(event.getMessage());
            }

        };

        AppenderAdapter appenderAdapter = LoggerAdapter
                .appenderAdapterForRoot()
                .addAppender(appender, layout);

        logger.info("Lay me out.");

        appenderAdapter.removeAppender(appender);

        assertThat(results.size(), is(1));
        assertThat(results.get(0), is("Lay me out."));
    }

}
