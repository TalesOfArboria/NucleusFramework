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

package com.jcwhatever.nucleus.internal.commands.players;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.Result;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.UUID;

@CommandInfo(
        parent="players",
        command="search",
        staticParams={ "searchText", "page=1" },
        description="Search for recorded players whose name contains the search text. Max 50 results.",

        paramDescriptions = {
                "searchText= Text that the player name must contain.",
                "page= {PAGE}"})

class SearchSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Player Search '{0}'";

    @Override
    public void execute(final CommandSender sender, ICommandArguments args) throws CommandException {

        final int page = args.getInteger("page");
        String searchText = args.getString("searchText");

        final ChatPaginator pagin = new ChatPaginator(
                Nucleus.getPlugin(), 6, NucLang.get(_PAGINATOR_TITLE, searchText));

        PlayerUtils.searchNames(searchText, 50)
                .onSuccess(new FutureResultSubscriber<Collection<UUID>>() {
                    @Override
                    public void on(Result<Collection<UUID>> result) {

                        Collection<UUID> collection = result.getResult();
                        assert collection != null;

                        for (UUID playerId : collection) {

                            pagin.add(PlayerUtils.getPlayerName(playerId), playerId);
                        }

                        pagin.show(sender, page, TextUtils.FormatTemplate.LIST_ITEM_DESCRIPTION);
                    }
                })
                .onError(new FutureResultSubscriber<Collection<UUID>>() {
                    @Override
                    public void on(Result<Collection<UUID>> result) {
                        tellError(sender, result.getMessage());
                    }
                });
    }
}
