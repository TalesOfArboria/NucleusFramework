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


package com.jcwhatever.nucleus.internal.managed.commands;

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.IRegisteredCommandInfo;
import com.jcwhatever.nucleus.managed.commands.parameters.ICommandParameter;
import com.jcwhatever.nucleus.managed.commands.parameters.IFlagParameter;
import com.jcwhatever.nucleus.managed.language.Localized;
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
class CommandInfoContainer implements IRegisteredCommandInfo {

    private final CommandContainer _command;
    private final CommandInfo _commandInfo;
    private final CommandContainer _rootCommand;
    private final Plugin _plugin;
    private final String _usage;

    private final Set<String> _parameterNames;
    private final List<ICommandParameter> _staticParameters;
    private final List<ICommandParameter> _floatingParameters;
    private final List<IFlagParameter> _flags;

    private ParameterDescriptions _descriptions;
    private String _sessionRootName;

    /**
     * Constructor.
     *
     * @param command      The command to get annotation info from.
     * @param rootCommand  The commands root command.
     */
    public CommandInfoContainer(CommandContainer command, @Nullable CommandContainer rootCommand) {
        this(command, rootCommand, command.getCommand().getClass().getAnnotation(CommandInfo.class));
    }

    /**
     * Constructor.
     *
     * @param command      The command to get annotation info from.
     * @param rootCommand  The commands root command.
     * @param info         The commands {@link CommandInfo} annotation.
     */
    public CommandInfoContainer(CommandContainer command, @Nullable CommandContainer rootCommand,
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

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public String getName() {
        return _commandInfo.command()[0];
    }

    @Override
    public String[] getCommandNames() {
        return _commandInfo.command();
    }

    @Override
    public boolean isHelpVisible() {
        return _commandInfo.isHelpVisible();
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return _commandInfo.permissionDefault();
    }

    @Override
    public String getParentName() {
        return _commandInfo.parent();
    }

    @Override
    @Nullable
    public CommandContainer getRoot() {
        return _rootCommand;
    }

    @Override
    public String getRootName() {
        if (_rootCommand == null)
            return getName();

        return _rootCommand.getInfo().getName();
    }

    @Override
    public String getRootAliasName() {
        if (_rootCommand == null)
            return getCurrentAlias();

        return _rootCommand.getInfo().getCurrentAlias();
    }

    @Override
    public String getCurrentAlias() {
        return _sessionRootName;
    }

    /**
     * Set the current alias name of the command used in the current command
     * context.
     *
     * @param aliasName  The command alias.
     */
    void setCurrentAlias(String aliasName) {
        _sessionRootName = aliasName;
    }

    @Override
    public String getUsage() {
        return _usage;
    }

    @Override
    public String[] getRawStaticParams() {
        return _commandInfo.staticParams();
    }

    @Override
    public String[] getRawFloatingParams() {
        return _commandInfo.floatingParams();
    }

    @Override
    public String[] getRawFlagParams() {
        return _commandInfo.flags();
    }

    @Override
    public String[] getRawParamDescriptions() {
        return _commandInfo.paramDescriptions();
    }

    @Override
    public ParameterDescriptions getParamDescriptions() {
        if (_descriptions == null) {
            _descriptions = new ParameterDescriptions(_command);
        }

        return _descriptions;
    }

    @Override
    public List<ICommandParameter> getStaticParams() {
        return _staticParameters;
    }

    @Override
    public List<ICommandParameter> getFloatingParams() {
        return _floatingParameters;
    }

    @Override
    public List<IFlagParameter> getFlagParams() {
        return _flags;
    }

    @Override
    @Localized
    @Nullable
    public String getDescription() {
        return NucLang.get(_plugin, _commandInfo.description());
    }

    @Override
    @Localized
    public String getLongDescription() {
        return NucLang.get(_plugin, _commandInfo.longDescription());
    }

    // setup command parameters
    private List<ICommandParameter> toCommandParameters(String[] rawParameters) {

        List<ICommandParameter> results = new ArrayList<>(rawParameters.length);
        for (String rawParam : rawParameters) {

            Parameter parameter = new Parameter(rawParam);

            if (_parameterNames.contains(parameter.getName()))
                throw new RuntimeException("Duplicate parameter '" + rawParam + "' detected in command.");

            results.add(parameter);
            _parameterNames.add(parameter.getName());
        }

        return Collections.unmodifiableList(results);
    }

    // setup flag parameters
    private List<IFlagParameter> toFlagParameters(String[] parameters) {

        List<IFlagParameter> results = new ArrayList<>(parameters.length);
        for (int i=0; i < parameters.length; i++) {

            Flag flag = new Flag(parameters[i], i);

            if (_parameterNames.contains(flag.getName()))
                throw new RuntimeException("Duplicate parameter '" + flag.getName() + "' detected in command.");

            results.add(flag);
            _parameterNames.add(flag.getName());
        }

        return Collections.unmodifiableList(results);
    }
}
