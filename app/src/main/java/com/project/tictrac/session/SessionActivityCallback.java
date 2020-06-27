package com.project.tictrac.session;

import com.project.tictrac.session.presession.SessionDetails;

/**
 * This interface defines callback methods for the SessionActivity
 */
public interface SessionActivityCallback {

    void beginSessionButtonClicked(SessionDetails details);
}
