package org.oddjob.arooa.parsing;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.registry.ChangeHow;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DragTransactionManagerTest {

    private static class OurSimpleTransaction implements DragTransaction {

        final AtomicInteger commits;

        final AtomicInteger rollbacks;

        private OurSimpleTransaction(AtomicInteger commits, AtomicInteger rollbacks) {
            this.commits = commits;
            this.rollbacks = rollbacks;
        }

        @Override
        public void commit() throws ArooaParseException {
            commits.incrementAndGet();
        }

        @Override
        public void rollback() {
            rollbacks.incrementAndGet();
        }
    }

    @Test
    void whenCommitWithTwoTransactionsThenOnlyOneCommit() throws ArooaParseException {

        AtomicInteger commits = new AtomicInteger();
        AtomicInteger rollbacks = new AtomicInteger();

        DragTransactionManager<OurSimpleTransaction> test = new DragTransactionManager<>(
                () -> new OurSimpleTransaction(commits, rollbacks));

        DragTransaction transaction1 = test.createTransaction(ChangeHow.FRESH);
        DragTransaction transaction2 = test.createTransaction(ChangeHow.EITHER);

        transaction2.commit();

        assertThat(commits.get(), is(0));
        assertThat(rollbacks.get(), is(0));

        transaction1.commit();

        assertThat(commits.get(), is(1));
        assertThat(rollbacks.get(), is(0));
    }

    @Test
    void whenRollbackWithTwoTransactionsThenOnlyOneRollback() throws ArooaParseException {

        AtomicInteger commits = new AtomicInteger();
        AtomicInteger rollbacks = new AtomicInteger();

        DragTransactionManager<OurSimpleTransaction> test = new DragTransactionManager<>(
                () -> new OurSimpleTransaction(commits, rollbacks));

        DragTransaction transaction1 = test.createTransaction(ChangeHow.FRESH);
        DragTransaction transaction2 = test.createTransaction(ChangeHow.EITHER);

        transaction2.rollback();

        assertThat(commits.get(), is(0));
        assertThat(rollbacks.get(), is(0));

        transaction1.commit();

        assertThat(commits.get(), is(0));
        assertThat(rollbacks.get(), is(1));
    }

    private static class OurFailTransaction implements DragTransaction {

        final AtomicInteger rollbacks;

        private OurFailTransaction(AtomicInteger rollbacks) {
            this.rollbacks = rollbacks;
        }

        @Override
        public void commit() throws ArooaParseException {
            throw new ArooaParseException("Can't Commit", Location.UNKNOWN_LOCATION);
        }

        @Override
        public void rollback() {

            rollbacks.incrementAndGet();
        }
    }

    @Test
    void whenCommitWithExceptionThenRollbackExecutedOk() {

        AtomicInteger rollbacks = new AtomicInteger();

        DragTransactionManager<OurFailTransaction> test = new DragTransactionManager<>(
                () -> new OurFailTransaction(rollbacks));

        DragTransaction transaction = test.createTransaction(ChangeHow.FRESH);

        try {
            transaction.commit();
        }
        catch (ArooaParseException e) {
            transaction.rollback();
        }

        assertThat(rollbacks.get(), is(1));
    }
}