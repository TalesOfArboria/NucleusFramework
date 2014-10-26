package com.jcwhatever.bukkit.generic.utils;

/**
 * Generic handler that can be passed into methods and classes
 * to add custom validation functionality.
 *
 * @param <T>  The type being validated
 */
public abstract class ItemValidator<T> {

    /**
     * Called to validate an item.
     *
     * @param item  The item to be validated.
     */
	public abstract boolean isValid(T item);
}
