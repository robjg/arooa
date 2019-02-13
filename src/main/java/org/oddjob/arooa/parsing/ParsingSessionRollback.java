package org.oddjob.arooa.parsing;

/**
 * Also parsing to be rolled back.
 *
 * @see ParsingSession
 */
public interface ParsingSessionRollback {

    /**
     * Clear the current session. This isn't commit because actions have
     * already been done.
     */
    void clear();

    /**
     * Rollback actions that have been done in the current session.
     */
    void rollback();

}
