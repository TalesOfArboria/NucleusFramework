package com.jcwhatever.bukkit.generic.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a method in a class that implements
 * {@code GenericsEventListener} as a Generics event handler.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GenericsEventHandler {

    /**
     * The priority/order that the event should be executed in.
     */
    GenericsEventPriority priority() default GenericsEventPriority.NORMAL;
}
