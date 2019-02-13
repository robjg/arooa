package org.oddjob.arooa.parsing;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ParsingSessionTest {

    @Test
    public void testActionsExecutedOnRollback() {

        AtomicBoolean done = new AtomicBoolean();

        ParsingSessionRollback parsingSessionRollback = ParsingSession.begin();

        ParsingSession.addRollback(() -> done.set(true));

        parsingSessionRollback.rollback();

        assertThat(done.get(), is(true));
    }

    @Test
    public void testActionsCleared() {

        AtomicBoolean done = new AtomicBoolean();

        ParsingSessionRollback parsingSessionRollback = ParsingSession.begin();

        ParsingSession.addRollback(() -> done.set(true));

        parsingSessionRollback.clear();

        try {
            parsingSessionRollback.rollback();
            fail("Should fail");
        }
        catch (IllegalStateException e) {
            // expected.
        }

        assertThat(done.get(), is(false));
    }

    @Test
    public void testClearAndBeginAgainIsOk() {

        ParsingSessionRollback parsingSessionRollback = ParsingSession.begin();
        parsingSessionRollback.clear();
        ParsingSessionRollback parsingSessionRollback2 = ParsingSession.begin();
        parsingSessionRollback2.clear();
        ParsingSessionRollback parsingSessionRollback3 = ParsingSession.begin();
        parsingSessionRollback3.rollback();
        ParsingSessionRollback parsingSessionRollback4 = ParsingSession.begin();
        parsingSessionRollback4.clear();
    }
}