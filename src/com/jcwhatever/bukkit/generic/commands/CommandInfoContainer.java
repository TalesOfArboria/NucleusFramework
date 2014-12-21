/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.commands;

import com.jcwhatever.bukkit.generic.commands.parameters.CommandParameter;
import com.jcwhatever.bukkit.generic.commands.parameters.FlagParameter;
import com.jcwhatever.bukkit.generic.commands.parameters.ParameterDescriptions;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localized;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.text.TextFormatter.ITagFormatter;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;

import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Container for a commands {@code ICommandInfo} annotation.
 */
public class CommandInfoContainer {

    private final CommandInfo _commandInfo;
    private final String _masterCommandName;
    private final Plugin _plugin;
    private final String _usage;

    private List<CommandParameter> _staticParameters;
    private List<CommandParameter> _floatingParameters;
    private List<FlagParameter> _flags;

    private ParameterDescriptions _descriptions;

    /**
     * Constructor.
     *
     * @param plugin             The commands owning plugin.
     * @param commandInfo        The command info annotation.
     * @param masterCommandName  The name of the top level command in the commands hierarchy.
     */
    public CommandInfoContainer(Plugin plugin, CommandInfo commandInfo, @Nullable String masterCommandName) {
        PreCon.notNull(plugin);
        PreCon.notNull(commandInfo);

        _plugin = plugin;
        _commandInfo = commandInfo;
        _masterCommandName = masterCommandName != null ? masterCommandName : commandInfo.command()[0];

        Map<String, ITagFormatter> formatters = TextUtils.getPluginFormatters(plugin);
        formatters.put("command", new ITagFormatter() {
            @Override
            public String getTag() {
                return "command";
            }

            @Override
            public void append(StringBuilder sb, String rawTag) {
                sb.append(_masterCommandName);
            }
        });

        _usage = TextUtils.TEXT_FORMATTER.format(formatters, commandInfo.usage());

        _staticParameters = toCommandParameters(getRawStaticParams());
        _floatingParameters = toCommandParameters(getRawFloatingParams());
        _flags = toFlagParameters(getRawFlagParams());

        _descriptions = new ParameterDescriptions(this);
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
     * Get the name of the top level command in the commands
     * hierarchy.
     */
    public String getMasterCommandName() {
        return _masterCommandName;
    }

    /**
     * Get the primary command name.
     */
    public String getCommandName() {
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
        return Lang.get(_plugin, _commandInfo.description());
    }

    /**
     * Get a language localized long description of the command.
     */
    @Localized
    public String getLongDescription() {
        return Lang.get(_plugin, _commandInfo.longDescription());
    }


    private List<CommandParameter> toCommandParameters(String[] rawParameters) {

        List<CommandParameter> results = new ArrayList<>(rawParameters.length);
        for (String rawParam : rawParameters) {
            results.add(new CommandParameter(rawParam));
        }

        return Collections.unmodifiableList(results);
    }

    private List<FlagParameter> toFlagParameters(String[] parameters) {

        List<FlagParameter> results = new ArrayList<>(parameters.length);
        for (int i=0; i < parameters.length; i++) {
            results.add(new FlagParameter(parameters[i], i));
        }

        return Collections.unmodifiableList(results);
    }
}
