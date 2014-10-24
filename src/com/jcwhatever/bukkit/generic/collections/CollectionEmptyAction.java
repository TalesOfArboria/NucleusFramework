package com.jcwhatever.bukkit.generic.collections;

/**
 * A handler used when a collection detects it is empty.
 *
 * @param <T>  Collection type
 */
public abstract class CollectionEmptyAction<T> {

    public abstract void onEmpty(T emptyCollection);
}
