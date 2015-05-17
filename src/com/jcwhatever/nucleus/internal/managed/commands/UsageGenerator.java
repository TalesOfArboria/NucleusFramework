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
import com.jcwhatever.nucleus.managed.commands.utils.ICommandUsageGenerator;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Generates command usage text.
 */
class UsageGenerator implements ICommandUsageGenerator {

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
    public String generate(IRegisteredCommand command) {

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
    public String generate(IRegisteredCommand command, String template) {
        PreCon.isValid(command instanceof RegisteredCommand);

        String rootCommandName = command.getInfo().getRootAliasName();
        if (rootCommandName == null) {
            rootCommandName = command.getInfo().getCurrentAlias();
        }

        LinkedList<RegisteredCommand> parentCommands = new LinkedList<>();
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

        List<ICommandParameter> staticParams = command.getInfo().getStaticParams();
        for (ICommandParameter parameter : staticParams) {

            boolean isRequired = !parameter.hasDefaultValue();

            params.append(isRequired ? '<' : '[')
                  .append(parameter.getName())
                  .append(isRequired ? '>' : ']')
                  .append(' ');
        }

        List<ICommandParameter> floatingParams = command.getInfo().getFloatingParams();
        for (ICommandParameter parameter : floatingParams) {

            boolean isRequired = !parameter.hasDefaultValue();

            params.append(isRequired ? '<' : '[')
                  .append(ArgumentParser.FLOATING_PREFIX)
                  .append(parameter.getName())
                  .append(isRequired ? '>' : ']')
                  .append(' ');
        }

        List<IFlagParameter> flagParams = command.getInfo().getFlagParams();
        for (IFlagParameter parameter : flagParams) {
            params.append('[')
                  .append(ArgumentParser.FLAG_PREFIX)
                  .append(parameter.getName())
                  .append("] ");
        }

        String commandName = command.getParent() != null
                ? command.getInfo().getName() + ' '
                : "";
        return NucLang.get(command.getPlugin(),
                template, rootCommandName + ' ', commandPath, commandName, params);
    }
}
