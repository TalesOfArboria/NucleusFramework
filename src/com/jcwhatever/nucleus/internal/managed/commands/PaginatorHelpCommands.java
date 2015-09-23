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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.commands.IRegisteredCommand;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.managed.messaging.IChatPaginatorCommands;

import javax.annotation.Nullable;

/**
 * Implementation of {@link IChatPaginatorCommands} for use with registered commands.
 */
class PaginatorHelpCommands implements IChatPaginatorCommands {

    private final IRegisteredCommand _command;

    /**
     * Constructor.
     *
     * @param command  The owning registered command.
     */
    PaginatorHelpCommands(IRegisteredCommand command) {
        _command = command;
    }

    @Nullable
    @Override
    public String getPrevCommand(ChatPaginator paginator, int currentPage, int totalPages) {
        if (currentPage == 1)
            return null;

        return Nucleus.getCommandManager().getUsageGenerator().generate(_command,
                "/{0: root command}{1: command path}{2: command}? ").toString() + (currentPage - 1);
    }

    @Nullable
    @Override
    public String getNextCommand(ChatPaginator paginator, int currentPage, int totalPages) {
        if (currentPage >= totalPages)
            return null;

        return Nucleus.getCommandManager().getUsageGenerator().generate(_command,
                "/{0: root command}{1: command path}{2: command}? ").toString() + (currentPage + 1);
    }
}
