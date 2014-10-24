package com.jcwhatever.bukkit.generic.views;

/**
 * Meta data specifically for returning a result from a view instance
 * after it is closed.
 */
public class ViewResult extends ViewMeta {

    private ViewInstance _viewInstance;
    private boolean _isCancelled = false;

    /**
     * Constructor.
     *
     * @param viewInstance  The view instance the result is for.
     */
    public ViewResult(ViewInstance viewInstance) {

        _viewInstance = viewInstance;
    }

    /**
     * Get the view instance the result is for.
     */
    public ViewInstance getViewInstance () {

        return _viewInstance;
    }

    /**
     * Get instance meta used to initialized the
     * view instance the result is from.
     */
    public ViewMeta getInstanceMeta () {

        return _viewInstance.getInstanceMeta();
    }

    /**
     * Determine if the result is cancelled.
     * Results should be disregarded if this returns true.
     */
    public boolean isCancelled () {

        return _isCancelled;
    }

    /**
     * Set the cancelled flag.
     *
     * @param isCancelled
     */
    public void setIsCancelled (boolean isCancelled) {

        _isCancelled = isCancelled;
    }

}
