package com.jcwhatever.bukkit.generic.views;

/**
 * Marks the reason a view instance is being closed.
 */
public enum ViewCloseReason {
	/**
	 * The view is being closed to go back to the previous view, if any.
	 */
	GOING_BACK,
	
	/**
	 * The view is being closed so the next view can be shown.
	 */
	OPEN_NEXT,

    /**
     * The view is being closed in order to refresh/re-open.
     */
    REFRESH
}
