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

import com.jcwhatever.nucleus.commands.arguments.ArgumentParser;
import com.jcwhatever.nucleus.commands.parameters.CommandParameter;
import com.jcwhatever.nucleus.commands.parameters.FlagParameter;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.utils.language.Localizable;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Generates command usage text.
 */
public class UsageGenerator {

    @Localizable public static final String HELP_USAGE =
            "{GOLD}/{0: root command}{1: command path}{GREEN}{2: command}{DARK_AQUA}{3: parameters}";

    @Localizable public static final String HELP_USAGE_HAS_SUB_COMMANDS =
            "{GOLD}/{0: root command}{1: command path}{GREEN}{2: command}?";

    @Localizable public static final String PARAMETER_HELP =
            "{GRAY}/{0: root command}{1: command path}{2: command}{GOLD}{3: parameters}";

    @Localizable public static final String INLINE_HELP =
            "{GREEN}/{0: root command}{1: command path}{2: command}?";

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

    /**
     * Generate default command usage.
     *
     * @param command  The command to generate usage text for.
     */
    public String generate(AbstractCommand command) {

        String rootCommandName = command.getInfo().getRootSessionName();
        if (rootCommandName == null) {
            rootCommandName = command.getInfo().getSessionName();
        }

        return generate(command, rootCommandName);
    }

    /**
     * Generate command usage using the specified root command name.
     *
     * @param command          The command to generate usage text for.
     * @param rootCommandName  The root command name.
     */
    public String generate(AbstractCommand command, String rootCommandName) {

        if (_defaultTemplate != null) {
            return generate(command, rootCommandName, _defaultTemplate);
        }

        String hardCodeUsage = command.getInfo().getUsage();
        if (!hardCodeUsage.isEmpty()) {
            return generate(command, rootCommandName, hardCodeUsage);
        }

        return command.getCommandCollection().size() == 0
                ? generate(command, rootCommandName, HELP_USAGE)
                : generate(command, rootCommandName, HELP_USAGE_HAS_SUB_COMMANDS);
    }

    /**
     * Generate command usage using the specified root command name and
     * a formatting template that uses the following parameters:
     *
     * <ul>
     *     <li>{0} - root command name</li>
     *     <li>{1} - command path excluding the root command and the command.</li>
     *     <li>{2} - the command name</li>
     *     <li>{3} - command parameters</li>
     * </ul>
     *
     * <p>Note that a space is added after values inserted for the above parameters
     * except when the parameter value is empty.</p>
     *
     * @param command          The command to generate usage text for.
     * @param rootCommandName  The name of the root command.
     * @param template         The format template for the generated text.
     */
    public String generate(AbstractCommand command, String rootCommandName, String template) {

        LinkedList<AbstractCommand> parentCommands = new LinkedList<>();
        StringBuilder commandPath = new StringBuilder(30);

        if (command.getParent() != null) {
            AbstractCommand parent = command;

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

        List<CommandParameter> staticParams = command.getInfo().getStaticParams();
        for (CommandParameter parameter : staticParams) {

            boolean isRequired = !parameter.hasDefaultValue();

            params.append(isRequired ? '<' : '[')
                  .append(parameter.getName())
                  .append(isRequired ? '>' : ']')
                  .append(' ');
        }

        List<CommandParameter> floatingParams = command.getInfo().getFloatingParams();
        for (CommandParameter parameter : floatingParams) {

            boolean isRequired = !parameter.hasDefaultValue();

            params.append(isRequired ? '<' : '[')
                  .append(ArgumentParser.FLOATING_PREFIX)
                  .append(parameter.getName())
                  .append(isRequired ? '>' : ']')
                  .append(' ');
        }

        List<FlagParameter> flagParams = command.getInfo().getFlagParams();
        for (FlagParameter parameter : flagParams) {
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
