package com.jcwhatever.bukkit.generic.events;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Documentation annotation to indicate that an event is cancellable.
 */
@Documented
@Target({ElementType.TYPE})
public @interface Cancellable {
}
