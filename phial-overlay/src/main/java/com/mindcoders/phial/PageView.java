package com.mindcoders.phial;

public interface PageView {

    /**
     * Called when device back button has been pressed
     * @return true if the event was consumed; false otherwise.
     */
    boolean onBackPressed();

}
