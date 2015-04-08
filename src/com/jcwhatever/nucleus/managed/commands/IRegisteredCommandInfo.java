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

package com.jcwhatever.nucleus.managed.commands;

import com.jcwhatever.nucleus.managed.commands.parameters.ICommandParameter;
import com.jcwhatever.nucleus.managed.commands.parameters.IFlagParameter;
import com.jcwhatever.nucleus.managed.commands.parameters.IParameterDescriptions;
import com.jcwhatever.nucleus.managed.language.Localized;
import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.mixins.IPluginOwned;

import org.bukkit.permissions.PermissionDefault;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Container for a commands {@link CommandInfo} annotation.
 */
public interface IRegisteredCommandInfo extends INamed, IPluginOwned {

    /**
     * Get the primary command name.
     */
    @Override
    String getName();

    /**
     * Get all command names.
     */
    String[] getCommandNames();

    /**
     * Determine if the command can be seen in help views.
     */
    boolean isHelpVisible();

    /**
     * Get the default permission.
     *
     * <p>The permission name is generated using the owning plugins
     * name and the commands path.</p>
     *
     * <p>i.e. mypluginname.commands.mycommand.mysubcommand</p>
     */
    PermissionDefault getPermissionDefault();

    /**
     * Get the parent name sanity check.
     *
     * <p>This is optional and may return an empty string.</p>
     *
     * <p>If the string returned is not empty, the parent must have the same
     * name as the string returned.</p>
     */
    String getParentName();

    /**
     * Get the root command in the commands hierarchy.
     *
     * @return  Null if the command is the root.
     */
    @Nullable
    IRegisteredCommand getRoot();

    /**
     * Get the name of the top level command in the commands hierarchy.
     */
    String getRootName();

    /**
     * Get the current alias name of the root command in the commands
     * hierarchy used in the current command context.
     */
    String getRootAliasName();

    /**
     * Get the current alias name of the command used in the current
     * command context.
     */
    String getCurrentAlias();

    /**
     * Get the usage text.
     */
    String getUsage();

    /**
     * Get unparsed static parameters.
     */
    String[] getRawStaticParams();

    /**
     * Get unparsed floating parameters.
     */
    String[] getRawFloatingParams();

    /**
     * Get unparsed flag parameters.
     */
    String[] getRawFlagParams();

    /**
     * Get unparsed parameter descriptions.
     */
    String[] getRawParamDescriptions();

    /**
     * Get parameter descriptions.
     */
    IParameterDescriptions getParamDescriptions();

    /**
     * Get static parameters.
     *
     * @return  Unmodifiable list.
     */
    List<ICommandParameter> getStaticParams();

    /**
     * Get floating parameters.
     *
     * @return  Unmodifiable list.
     */
    List<ICommandParameter> getFloatingParams();

    /**
     * Get the flag parameters.
     *
     * @return  Unmodifiable list.
     */
    List<IFlagParameter> getFlagParams();

    /**
     * Get a language localized description of the command.
     */
    @Localized
    @Nullable
    String getDescription();

    /**
     * Get a language localized long description of the command.
     */
    @Localized
    String getLongDescription();
}
