package org.oddjob.arooa.utils;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ListenerSupportBaseTest {

    @Test
    public void whenAddRemoveThenCommandsRunAsExpected() {

        AtomicInteger onFirstCount = new AtomicInteger();
        AtomicInteger onEmptyCount = new AtomicInteger();

        ListenerSupportBase<String> test = new ListenerSupportBase<>();
        test.setOnFirst(onFirstCount::incrementAndGet);
        test.setOnEmpty(onEmptyCount::incrementAndGet);

        test.addListener("foo");

        assertThat(onFirstCount.get(), is(1));
        assertThat(onEmptyCount.get(), is(0));

        test.addListener("bar");

        assertThat(onFirstCount.get(), is(1));
        assertThat(onEmptyCount.get(), is(0));

        test.removeListener("foo");

        assertThat(onFirstCount.get(), is(1));
        assertThat(onEmptyCount.get(), is(0));

        test.removeListener("bar");

        assertThat(onFirstCount.get(), is(1));
        assertThat(onEmptyCount.get(), is(1));
    }

    @Test
    public void whenSameListenerAddedThenRemovedThenCommandsRunAsExpected() {

        AtomicInteger onFirstCount = new AtomicInteger();
        AtomicInteger onEmptyCount = new AtomicInteger();

        ListenerSupportBase<String> test = new ListenerSupportBase<>();
        test.setOnFirst(onFirstCount::incrementAndGet);
        test.setOnEmpty(onEmptyCount::incrementAndGet);

        test.addListener("foo");

        assertThat(onFirstCount.get(), is(1));
        assertThat(onEmptyCount.get(), is(0));

        test.addListener("foo");

        assertThat(onFirstCount.get(), is(1));
        assertThat(onEmptyCount.get(), is(0));

        test.removeListener("foo");

        assertThat(onFirstCount.get(), is(1));
        assertThat(onEmptyCount.get(), is(1));

        test.removeListener("foo");

        assertThat(onFirstCount.get(), is(1));
        assertThat(onEmptyCount.get(), is(1));
    }

    @Test
    public void whenUnknownListenerRemovedThenOnEmptyNotRun() {

        AtomicInteger onFirstCount = new AtomicInteger();
        AtomicInteger onEmptyCount = new AtomicInteger();

        ListenerSupportBase<String> test = new ListenerSupportBase<>();
        test.setOnFirst(onFirstCount::incrementAndGet);
        test.setOnEmpty(onEmptyCount::incrementAndGet);

        test.removeListener("foo");

        assertThat(onFirstCount.get(), is(0));
        assertThat(onEmptyCount.get(), is(0));
    }
}