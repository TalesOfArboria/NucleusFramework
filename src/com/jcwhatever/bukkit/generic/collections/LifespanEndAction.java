package com.jcwhatever.bukkit.generic.collections;

/**
 * A handler used when the lifespan of an item in a
 * collection ends.
 */
public abstract class LifespanEndAction<T> {

    public abstract void onEnd(T item);

}

