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

package com.jcwhatever.nucleus.managed.commands.utils;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.commands.IRegisteredCommand;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArgument;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.managed.commands.parameters.ICommandParameter;
import com.jcwhatever.nucleus.managed.commands.parameters.IFlagParameter;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.managed.messaging.IChatPaginatorCommands;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;

import javax.annotation.Nullable;

/**
 * Command implementation of {@link IChatPaginatorCommands}.
 */
public class PaginatorCommands implements IChatPaginatorCommands {

    private final String _pageName;
    private final ICommandArguments _arguments;
    private final IRegisteredCommand _command;

    /**
     * Constructor.
     *
     * <p>Assumes the page parameter is named "page".</p>
     *
     * @param arguments  The command arguments.
     */
    public PaginatorCommands(ICommandArguments arguments) {
        this(arguments, "page");
    }

    /**
     * Constructor.
     *
     * @param arguments      The command arguments.
     * @param pageParamName  The name of the page parameter.
     */
    public PaginatorCommands(ICommandArguments arguments, String pageParamName) {
        PreCon.notNull(arguments);
        PreCon.notNullOrEmpty(pageParamName);

        _arguments = arguments;
        _command = arguments.getCommand();
        _pageName = pageParamName;
    }

    @Nullable
    @Override
    public String getPrevCommand(ChatPaginator paginator, int currentPage, int totalPages) {
        if (currentPage <= 1)
            return null;

        try {
            return getCommand(currentPage - 1);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    @Override
    public String getNextCommand(ChatPaginator paginator, int currentPage, int totalPages) {
        if (currentPage >= totalPages)
            return null;

        try {
            return getCommand(currentPage + 1);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getCommand(int newPage) throws InvalidArgumentException {
        IChatMessage cmdText = Nucleus.getCommandManager().getUsageGenerator().generate(_command,
                "/{0: root command}{1: command path}{2: command}");

        StringBuilder output = new StringBuilder(cmdText);

        for (ICommandParameter parameter : _command.getInfo().getStaticParams()) {

            output.append(' ');
            if (parameter.getName().equalsIgnoreCase(_pageName)) {
                output.append(newPage);
            }
            else {
                try {
                    output.append(_arguments.getString(parameter.getName()));
                }
                catch (CommandException ignore) {}
            }
        }

        for (ICommandParameter parameter : _command.getInfo().getFloatingParams()) {
            if (parameter.getName().equalsIgnoreCase(_pageName)) {
                output.append(" -");
                output.append(parameter.getName());
                output.append(' ');
                output.append(newPage);
            }
            else {
                ICommandArgument arg = _arguments.get(parameter.getName());
                if (arg == null || arg.isDefaultValue() || arg.getValue() == null) {
                    continue;
                }
                output.append(" -");
                output.append(parameter.getName());
                output.append(' ');
                output.append(arg.getValue());
            }
        }

        for (IFlagParameter parameter : _command.getInfo().getFlagParams()) {
            if (_arguments.getBoolean(parameter.getName())) {
                output.append(" --");
                output.append(parameter.getName());
            }
        }

        return output.toString();
    }
}
