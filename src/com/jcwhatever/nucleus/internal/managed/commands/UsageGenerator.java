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
import com.jcwhatever.nucleus.managed.commands.IRegisteredCommand;
import com.jcwhatever.nucleus.managed.commands.parameters.ICommandParameter;
import com.jcwhatever.nucleus.managed.commands.parameters.IFlagParameter;
import com.jcwhatever.nucleus.managed.commands.parameters.IParameterDescription;
import com.jcwhatever.nucleus.managed.commands.parameters.IParameterDescriptions;
import com.jcwhatever.nucleus.managed.commands.utils.ICommandUsageGenerator;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.text.components.IChatClickable.ClickAction;
import com.jcwhatever.nucleus.utils.text.components.IChatComponent;
import com.jcwhatever.nucleus.utils.text.components.IChatHoverable.HoverAction;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatClickable;
import com.jcwhatever.nucleus.utils.text.format.args.HoverableArgModifier;
import com.jcwhatever.nucleus.utils.text.format.args.TextArg;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Generates command usage text.
 */
class UsageGenerator implements ICommandUsageGenerator {

    private static final String COMMAND = "/{0: root command}{1: command path}{2: command}";

    private final String _defaultTemplate;

    /**
     * Constructor.
     */
    public UsageGenerator() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param defaultTemplate  The template to use if one is not explicitly specified.
     */
    public UsageGenerator(@Nullable String defaultTemplate) {
        _defaultTemplate = defaultTemplate;
    }

    @Override
    public IChatMessage generate(IRegisteredCommand command) {
        PreCon.notNull(command);

        if (_defaultTemplate != null) {
            return generate(command, _defaultTemplate);
        }

        String hardCodeUsage = command.getInfo().getUsage();
        if (!hardCodeUsage.isEmpty()) {
            return generate(command, hardCodeUsage);
        }

        return ((RegisteredCommand)command).getCommandCollection().size() == 0
                ? generate(command, ICommandUsageGenerator.HELP_USAGE)
                : generate(command, ICommandUsageGenerator.HELP_USAGE_HAS_SUB_COMMANDS);
    }

    @Override
    public IChatMessage generate(IRegisteredCommand command, String template) {
        PreCon.isValid(command instanceof RegisteredCommand);

        String rootCommandName = command.getInfo().getRootAliasName();
        if (rootCommandName == null) {
            rootCommandName = command.getInfo().getCurrentAlias();
        }

        Deque<RegisteredCommand> parentCommands = new ArrayDeque<>(5);
        StringBuilder commandPath = new StringBuilder(30);

        if (command.getParent() != null) {
            RegisteredCommand parent = (RegisteredCommand)command;

            while ((parent = parent.getParent()) != null && parent.getParent() != null) {
                if (command != parent) {
                    parentCommands.push(parent);
                }
            }

            while(!parentCommands.isEmpty()) {
                commandPath.append(parentCommands.pop().getInfo().getName());
                commandPath.append(' ');
            }
        }

        StringBuilder params = new StringBuilder(30);
        IParameterDescriptions descriptions = command.getInfo().getParamDescriptions();
        Deque<Object> paramArgs = new ArrayDeque<>(10);

        // static params
        List<ICommandParameter> staticParams = command.getInfo().getStaticParams();
        for (ICommandParameter parameter : staticParams) {

            boolean isRequired = !parameter.hasDefaultValue();
            IParameterDescription description = descriptions.get(parameter.getName());

            if (description == null) {
                params.append(isRequired ? '<' : '[')
                        .append(parameter.getName())
                        .append(isRequired ? '>' : ']')
                        .append(' ');
            }
            else {
                params.append('{')
                        .append(paramArgs.size())
                        .append('}');

                paramArgs.add(
                        new TextArg(new StringBuilder(20)
                                .append(isRequired ? '<' : '[')
                                .append(parameter.getName())
                                .append(isRequired ? '>' : ']')
                                .append(' '),
                                new HoverableArgModifier(HoverAction.SHOW_TEXT,
                                        description.getDescription())));
            }
        }

        // floating params
        List<ICommandParameter> floatingParams = command.getInfo().getFloatingParams();
        for (ICommandParameter parameter : floatingParams) {

            boolean isRequired = !parameter.hasDefaultValue();
            IParameterDescription description = descriptions.get(parameter.getName());

            if (description == null) {
                params.append(isRequired ? '<' : '[')
                        .append(ArgumentParser.FLOATING_PREFIX)
                        .append(parameter.getName())
                        .append(isRequired ? '>' : ']')
                        .append(' ');
            }
            else {
                params.append('{')
                        .append(paramArgs.size())
                        .append('}');

                paramArgs.add(
                        new TextArg(new StringBuilder(20)
                                .append(isRequired ? '<' : '[')
                                .append(ArgumentParser.FLOATING_PREFIX)
                                .append(parameter.getName())
                                .append(isRequired ? '>' : ']')
                                .append(' '),
                                new HoverableArgModifier(HoverAction.SHOW_TEXT,
                                        description.getDescription())));
            }
        }

        // flags
        List<IFlagParameter> flagParams = command.getInfo().getFlagParams();
        for (IFlagParameter parameter : flagParams) {

            IParameterDescription description = descriptions.get(parameter.getName());

            if (description == null) {
                params.append('[')
                        .append(ArgumentParser.FLAG_PREFIX)
                        .append(parameter.getName())
                        .append("] ");
            }
            else {
                params.append('{')
                        .append(paramArgs.size())
                        .append('}');

                paramArgs.add(
                        new TextArg(new StringBuilder(20)
                                .append('[')
                                .append(ArgumentParser.FLAG_PREFIX)
                                .append(parameter.getName())
                                .append("] "),
                                new HoverableArgModifier(HoverAction.SHOW_TEXT,
                                        description.getDescription())));
            }
        }

        IChatMessage paramMessage = TextUtils.format(params, paramArgs.toArray());
        String commandName = command.getParent() != null
                ? command.getInfo().getName() + ' '
                : "";

        IChatMessage message = NucLang.get(command.getPlugin(),
                template, rootCommandName + ' ', commandPath, commandName, paramMessage);

        String baseCommand = TextUtils.format(
                template.endsWith("?")
                        ? COMMAND + '?'
                        : COMMAND, rootCommandName + ' ', commandPath, commandName).toString();

        SimpleChatClickable clickable = new SimpleChatClickable(ClickAction.SUGGEST_COMMAND, baseCommand);

        for (IChatComponent component : message.getComponents()) {
            component.getModifier().setClickable(clickable);
        }

        return message;
    }
}
