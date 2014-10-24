package com.jcwhatever.bukkit.generic.language;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;


/**
 * Indicates the annotated method returns language localized strings
 * or a parameter requires a localized string.
 * 
 * @author JC The Pants
 *
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Localized {
}
