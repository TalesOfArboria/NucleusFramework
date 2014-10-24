package com.jcwhatever.bukkit.generic.mixins;

/**
 * Mixin to add dispose method to classes.
 * <p>
 *     A class that implements {@code IDisposable} must have
 *     its {@code dispose} method called when it is no longer needed.
 * </p>
 */
public interface IDisposable {

    /**
     * Release resources used by the instance.
     */
    void dispose();
}
