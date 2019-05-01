package org.oddjob.arooa.design.screem;

import java.util.concurrent.Callable;

/**
 * Implemented by {@link Form}s that want to provide some action that is to be performed
 * when OK is clicked.
 *
 * @see org.oddjob.arooa.design.view.ViewHelper
 */
public interface OkAware {

    /**
     * Provide the action.
     *
     * @return A Callable that should return true if OK is OK or false if it isn't.
     */
    Callable<Boolean> getOkAction();
}
