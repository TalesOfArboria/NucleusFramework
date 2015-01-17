/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.nucleus.commands;

import com.jcwhatever.nucleus.utils.language.Localizable;

import org.bukkit.permissions.PermissionDefault;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Required annotation for all commands. Defines
 * basic operation of command.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {

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
     * Define static arguments of command.
     *
     * <p>Static parameters are typed by the user as the argument value in
     * the order specified in the staticParams annotation property. Static
     * parameters must always be typed as the first group of arguments.</p>
     *
     * <p>Only the last static parameter may have a default value.</p>
     *
     * <p>i.e. /command param1Arg param2Arg</p>
     *
     * <p>Annotation format example: staticParams = { "param1Name", "param2Name" }</p>
     *
     * <p>Annotation format example: staticParams = { "param1Name", "param2Name=defaultValue" }</p>
     */
    String[] staticParams() default {};

    /**
     * Define floating parameters of command.
     *
     * <p>The floating parameter is typed as 2 dashes followed by the parameter name,
     * a space, then the parameter value.</p>
     *
     * <p>The order that users type floating parameters is not enforced, except that
     * they must occur after all static arguments.</p>
     *
     * <p>Any floating parameter may have a default value.</p>
     *
     * <p>i.e. /command static1Arg static2Arg --param1Name param1Arg</p>
     *
     * <p>Annotation format example: floatingParams = { "param1Name", "param2Name" }</p>
     * <p>Annotation format example: floatingParams = { "param1Name=defaultValue", "param2Name" }</p>
     */
    String[] floatingParams() default {};

    /**
     * Define flag parameters.
     *
     * <p>Flag parameters return false if not present
     * as a command argument or true when present. They are written by
     * the user a single dash followed by the flag name.</p>
     *
     * <p>i.e. /command static1 static2 -flag1Name</p>
     *
     * <p>Annotation format example: { "flag1Name", "flag2Name" }</p>
     */
    String[] flags() default {};

    /**
     * Define descriptions for parameters. Include descriptions for
     * static, floating and flag arguments. (Optional)
     *
     * <p>If descriptions are not provided, a substitute may be provided where possible.</p>
     *
     * <p>Annotation format example: paramDescriptions = { "paramName1=description", "paramName2=description" }
     */
    String[] paramDescriptions() default {};

    /**
     * Optional usage instructions for the command. This is normally
     * auto generated. You can override the auto-generated usage by providing
     * a template or hardcoded usage.
     *
     * <p>A template has markers that define where to insert text as well
     * as colors. See {@link com.jcwhatever.nucleus.utils.text.TextUtils#format}.</p>
     *
     * <p>The following are the format markers used in a template:</p>
     *
     * <ul>
     *     <li>{0} - root command name</li>
     *     <li>{1} - command path excluding the root command and the command.</li>
     *     <li>{2} - the command name</li>
     *     <li>{3} - command parameters</li>
     * </ul>
     *
     * <p>Hardcode example: "/command subcommand <parameter1> [parameter2] [--floatingParam] [-flag]"</p>
     *
     * <p>Template example: "{GOLD}/{0}{GREEN}{1}{2}{BLUE}{3}"</p>
     *
     * <p>Auto generated is recommended. Templates are preferable to hardcoded usage.</p>
     */
    String usage() default "";

    /**
     * A short description of the commands function
     */
    @Localizable
    String description();

    /**
     * A longer description of the commands function
     */
    @Localizable
    String longDescription() default "";

    /**
     * Define if the command can be seen in help command lists. 
     */
    boolean isHelpVisible() default true;

    /**
     * Define the default permission for the command.
     *
     * <p>The permission name is generated using the owning plugins name and
     * the commands path.</p>
     *
     * <p>i.e. mypluginname.commands.mycommand.mysubcommand</p>
     */
    PermissionDefault permissionDefault() default PermissionDefault.OP;

}
