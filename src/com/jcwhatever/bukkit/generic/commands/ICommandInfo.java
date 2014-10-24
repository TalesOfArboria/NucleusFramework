package com.jcwhatever.bukkit.generic.commands;

import org.bukkit.permissions.PermissionDefault;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface ICommandInfo {
	
	/**
	 * Optional parent command sanity check.
     * If set, the parent command must match the name provided.
	 */
	String parent() default "";
	
	/**
	 * The command as it should be typed by the user
	 * 
	 * <p>format example: { "command" }</p>
	 * <p>format example: { "command", "c" } (multiple commands defined, first one is primary)</p>
	 */
	String[] command();
	
    /**
     * Define static parameters of command.
     * i.e. /command static1 static2 --floating1 floating1val --floatingFlag
     * 
     * <p>format example: { "parameter1Name", "parameter2Name" }</p>
     * <p>format example: { "parameter1Name", "parameter2Name=defaultValue" }</p>  
     * 
     * <p>note: default value only allowed on last parameter.</p>
     */
    String[] staticParams() default {}; 
    
    /**
     * Define floating parameters of command.
     * i.e. /command static1 static2 --floating1 floating1val --floatingFlag
     * 
     * <p>format example: { "parameter1Name", "parameter2Name" }</p>
     * <p>format example: { "parameter1Name=defaultValue", "parameter2Name" }</p>
     */
    String[] floatingParams() default {};
	
    /**
     * Define descriptions for parameters. Include descriptions for both static
     * and floating parameters.
     * 
     * <p>format example: { "paramName1=description", "paramName2=description" }
     */
    String[] paramDescriptions() default {};
    
    /**
     * Give usage instructions for the command
     * i.e "/pv command <parameter1> [parameter2]"
     */
    String usage() default "";

    /**
     * A short description of the commands function
     */
    String description();
    
    /**
     * A longer description of the commands function
     */
    String longDescription() default "";
        
    /**
     * Define if the command can be seen in help command lists. 
     */
    boolean isHelpVisible() default true;
    
    
    /**
     * Define the default permission for the command
     */
    PermissionDefault permissionDefault() default PermissionDefault.OP;
    
}
