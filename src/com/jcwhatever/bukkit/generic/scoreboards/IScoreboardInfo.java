package com.jcwhatever.bukkit.generic.scoreboards;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IScoreboardInfo {

    public String name();

    public String description();

}
