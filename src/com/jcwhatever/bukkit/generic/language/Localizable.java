package com.jcwhatever.bukkit.generic.language;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indicates a static final {@code String} field is a candidate for localization.
 * 
 * @author JC The Pants
 *
 */
@Documented
@Target({ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface Localizable {
}