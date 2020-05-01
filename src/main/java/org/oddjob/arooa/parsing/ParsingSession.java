package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaConfiguration;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Allow Rollback of Parsing. Required for cut and paste to be consistent.
 * This isn't a great solution because of its use of statics and needs to
 * be rethought.
 *
 * @see CutAndPasteSupport#replace(ArooaContext, ArooaConfiguration)
 */
public class ParsingSession {

    private static final ThreadLocal<Session> sessions =
            new ThreadLocal<>();

    @SuppressWarnings("unchecked")
    public static <T, E extends Exception> T doIn(Callable<T> callable)
    throws E {
        ParsingSessionRollback rollback = ParsingSession.begin();
        try {
            return callable.call();
        }
        catch (Exception e) {
            rollback.rollback();
            throw (E) e;
        }
        finally {
            rollback.clear();
        }
    }

    public static ParsingSessionRollback begin() {
        Session parentSession = sessions.get();
        Session session = new Session();
        sessions.set(session);
        return new ParsingSessionRollbackImp(parentSession, session);
    }

    public static void addRollback(Runnable runnable) {
        Optional.ofNullable(sessions.get())
                .ifPresent(sess -> sess.actions.add(0, runnable));
    }

    static class Session {

        List<Runnable> actions = new LinkedList<>();

        IllegalStateException start = new IllegalStateException(
                "Session Origin");
    }

    public static class ParsingSessionRollbackImp implements ParsingSessionRollback {

        private final Session parentSession;

        private final Session session;

        public ParsingSessionRollbackImp(Session parentSession, Session session) {
            this.parentSession = parentSession;
            this.session = session;
        }

        @Override
        public void rollback() {
            Session session  = sessions.get();
            if (session == null) {
                throw new IllegalStateException("No current session.");
            }
            if (session != this.session) {
                throw new IllegalStateException(
                        sessionStartedMessage(session.start));
            }

            session.actions.forEach(Runnable::run);

            clear();
        }

        @Override
        public void clear() {
            if (sessions.get() == this.session) {
                if (parentSession == null) {
                    sessions.remove();
                } else {
                    sessions.set(parentSession);
                }
            }
        }
    }

    private static String sessionStartedMessage(Exception startPoint) {
        StringBuilder builder = new StringBuilder();
        builder.append("Session started already at:\n");
        for (StackTraceElement ste : startPoint.getStackTrace()) {
            builder.append("\t\t");
            builder.append(ste.toString());
            builder.append('\n');
        }
        return builder.toString();
    }
}
