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

import com.jcwhatever.nucleus.commands.parameters.CommandParameter;
import com.jcwhatever.nucleus.commands.parameters.FlagParameter;
import com.jcwhatever.nucleus.commands.parameters.ParameterDescriptions;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.utils.language.Localized;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Container for a commands {@link CommandInfo} annotation.
 */
public class CommandInfoContainer {

    private final AbstractCommand _command;
    private final CommandInfo _commandInfo;
    private final AbstractCommand _rootCommand;
    private final Plugin _plugin;
    private final String _usage;

    private final Set<String> _parameterNames;
    private final List<CommandParameter> _staticParameters;
    private final List<CommandParameter> _floatingParameters;
    private final List<FlagParameter> _flags;

    private ParameterDescriptions _descriptions;
    private String _sessionRootName;

    /**
     * Constructor.
     *
     * @param command      The command to get annotation info from.
     * @param rootCommand  The commands root command.
     */
    public CommandInfoContainer(AbstractCommand command, @Nullable AbstractCommand rootCommand) {
        this(command, rootCommand, command.getClass().getAnnotation(CommandInfo.class));
    }

    /**
     * Constructor.
     *
     * @param command      The command to get annotation info from.
     * @param rootCommand  The commands root command.
     * @param info         The commands {CommandInfo} annotation.
     */
    public CommandInfoContainer(AbstractCommand command, @Nullable AbstractCommand rootCommand,
                                CommandInfo info) {
        PreCon.notNull(command);
        PreCon.notNull(info);

        _plugin = command.getPlugin();
        _command = command;
        _commandInfo = info;
        _rootCommand = rootCommand;
        _usage = info.usage();

        _parameterNames = new HashSet<>(
                getRawStaticParams().length + getRawFloatingParams().length + getRawFlagParams().length);

        _staticParameters = toCommandParameters(getRawStaticParams());
        _floatingParameters = toCommandParameters(getRawFloatingParams());
        _flags = toFlagParameters(getRawFlagParams());
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Determine if the command can be seen in help views.
     */
    public boolean isHelpVisible() {
        return _commandInfo.isHelpVisible();
    }

    /**
     * Get the default permission.
     *
     * <p>The permission name is generated using the owning plugins
     * name and the commands path.</p>
     *
     * <p>i.e. mypluginname.commands.mycommand.mysubcommand</p>
     */
    public PermissionDefault getPermissionDefault() {
        return _commandInfo.permissionDefault();
    }

    /**
     * Get the parent name sanity check.
     *
     * <p>This is optional and may return an empty string.</p>
     *
     * <p>If the string returned is not empty, the parent must have the same name
     * as the string returned.</p>
     */
    public String getParentName() {
        return _commandInfo.parent();
    }

    /**
     * Get the root command in the commands hierarchy.
     *
     * @return  Null if the command is the root.
     */
    @Nullable
    public AbstractCommand getRoot() {
        return _rootCommand;
    }

    /**
     * Get the name of the top level command in the commands
     * hierarchy.
     */
    public String getRootName() {
        if (_rootCommand == null)
            return getName();

        return _rootCommand.getInfo().getName();
    }

    /**
     * Get the name of the top level command in the commands
     * hierarchy.
     */
    public String getRootSessionName() {
        if (_rootCommand == null)
            return getSessionName();

        return _rootCommand.getInfo().getSessionName();
    }

    /**
     * Get the name of the root command for display purposes in the
     * current session.
     */
    public String getSessionName() {
        return _sessionRootName;
    }

    /**
     * Set the name of the root command for display in the
     * current session.
     *
     * @param rootName  The root command name.
     */
    void setSessionRootName(String rootName) {
        _sessionRootName = rootName;
    }

    /**
     * Get the primary command name.
     */
    public String getName() {
        return _commandInfo.command()[0];
    }

    /**
     * Get all command names.
     */
    public String[] getCommandNames() {
        return _commandInfo.command();
    }

    /**
     * Get the usage text.
     */
    public String getUsage() {
        return _usage;
    }

    /**
     * Get unparsed static parameters.
     */
    public String[] getRawStaticParams() {
        return _commandInfo.staticParams();
    }

    /**
     * Get unparsed floating parameters.
     */
    public String[] getRawFloatingParams() {
        return _commandInfo.floatingParams();
    }

    /**
     * Get unparsed flag parameters.
     */
    public String[] getRawFlagParams() {
        return _commandInfo.flags();
    }

    /**
     * Get unparsed parameter descriptions.
     */
    public String[] getRawParamDescriptions() {
        return _commandInfo.paramDescriptions();
    }

    /**
     * Get parameter descriptions.
     */
    public ParameterDescriptions getParamDescriptions() {
        if (_descriptions == null) {
            _descriptions = new ParameterDescriptions(_command);
        }

        return _descriptions;
    }

    /**
     * Get static parameters.
     *
     * @return  Unmodifiable list.
     */
    public List<CommandParameter> getStaticParams() {
        return _staticParameters;
    }

    /**
     * Get floating parameters.
     *
     * @return  Unmodifiable list.
     */
    public List<CommandParameter> getFloatingParams() {
        return _floatingParameters;
    }

    /**
     * Get the flag parameters.
     *
     * @return  Unmodifiable list.
     */
    public List<FlagParameter> getFlagParams() {
        return _flags;
    }

    /**
     * Get a language localized description of the command.
     */
    @Localized
    @Nullable
    public String getDescription() {
        return NucLang.get(_plugin, _commandInfo.description());
    }

    /**
     * Get a language localized long description of the command.
     */
    @Localized
    public String getLongDescription() {
        return NucLang.get(_plugin, _commandInfo.longDescription());
    }

    // setup command parameters
    private List<CommandParameter> toCommandParameters(String[] rawParameters) {

        List<CommandParameter> results = new ArrayList<>(rawParameters.length);
        for (String rawParam : rawParameters) {

            CommandParameter parameter = new CommandParameter(rawParam);

            if (_parameterNames.contains(parameter.getName()))
                throw new RuntimeException("Duplicate parameter '" + rawParam + "' detected in command.");

            results.add(parameter);
            _parameterNames.add(parameter.getName());
        }

        return Collections.unmodifiableList(results);
    }

    // setup flag parameters
    private List<FlagParameter> toFlagParameters(String[] parameters) {

        List<FlagParameter> results = new ArrayList<>(parameters.length);
        for (int i=0; i < parameters.length; i++) {

            FlagParameter flag = new FlagParameter(parameters[i], i);

            if (_parameterNames.contains(flag.getName()))
                throw new RuntimeException("Duplicate parameter '" + flag.getName() + "' detected in command.");

            results.add(flag);
            _parameterNames.add(flag.getName());
        }

        return Collections.unmodifiableList(results);
    }
}
