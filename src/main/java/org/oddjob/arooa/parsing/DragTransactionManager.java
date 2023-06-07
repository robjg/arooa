package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.registry.ChangeHow;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Provides management of a {@link DragTransaction}. This was created by extracting functionality from the
 * local {@link DragContext} so that it could also be used by Oddjob's remote handler to fix a bug where
 * delete wasn't happening before paste so id's were duplicated when dragging within a server container.
 * There is lots of Threading stuff that in hindsight might not have been necessary.
 *
 * @param <T> The Transaction type being managed.
 */
public class DragTransactionManager<T extends DragTransaction> {

    private final Supplier<? extends T> transactionSupplier;

    private TransactionWrapper transaction;

    public DragTransactionManager(Supplier<? extends T> transactionSupplier) {
        this.transactionSupplier = transactionSupplier;
    }

    public void withTransaction(Consumer<? super T> transactionConsumer) {
        synchronized (this) {
            if (transaction == null) {
                throw new IllegalStateException("There is no Drag Transaction in progress.");
            }
            transactionConsumer.accept(transaction.delegate);
        }
    }

    public DragTransaction createTransaction(ChangeHow how) {

        synchronized (this) {
            switch (how) {
                case AGAIN:
                    if (transaction == null) {
                        throw new IllegalStateException("There is no transaction in progress.");
                    }
                case MAYBE:
                    if (transaction == null) {
                        return null;
                    }
                    if (transaction.isNotTransactionThread()) {
                        throw new IllegalStateException(
                                "Transaction is on a different thread.",
                                transaction.getTransactionCreation());
                    }
                    return transaction;
                case EITHER:
                    if (transaction != null) {
                        if (transaction.isNotTransactionThread()) {
                            throw new IllegalStateException(
                                    "Transaction is on a different thread.",
                                    transaction.getTransactionCreation());
                        }
                        return new DragContext.SimpleTransaction() {
                            public void commit() {
                            }

                            public void rollback() {
                                transaction.rollbackOnly = true;
                            }
                        };
                    }
                case FRESH:
                    if (transaction != null) {
                        throw new IllegalStateException(
                                "There is already a transaction in progress.",
                                transaction.getTransactionCreation());
                    }
                    transaction = new TransactionWrapper(transactionSupplier.get());
                    return transaction;
                default:
                    throw new IllegalArgumentException("Unrecognized how.");
            }
        }
    }


    class TransactionWrapper implements DragTransaction {

        private final Exception transactionCreation;

        private final Thread transactionThread;

        private final T delegate;

        private boolean rollbackOnly;

        private int nestedCount;

        public TransactionWrapper(T delegate) {
            this.transactionCreation = new Exception("Transaction Creation Point.");
            this.transactionThread = Thread.currentThread();
            this.delegate = delegate;
        }

        Exception getTransactionCreation() {
            return transactionCreation;
        }

        boolean isNotTransactionThread() {
            return Thread.currentThread() != transactionThread;
        }

        public void commit() throws ArooaParseException {
            synchronized (DragTransactionManager.this) {
                if (transaction == null) {
                    throw new IllegalStateException("Transaction already complete.");
                }

                if (rollbackOnly) {
                    rollback();
                } else {
                    delegate.commit();
                    transaction = null;
                }
            }
        }

        public void rollback() {
            synchronized (DragTransactionManager.this) {
                if (transaction == null) {
                    throw new IllegalStateException("Transaction already complete.");
                }

                transaction = null;
                delegate.rollback();
            }
        }
    }
}
