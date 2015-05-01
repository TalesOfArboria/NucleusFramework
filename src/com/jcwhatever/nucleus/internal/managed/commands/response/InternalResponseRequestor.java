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

package com.jcwhatever.nucleus.internal.managed.commands.response;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.timed.TimedMultimap;
import com.jcwhatever.nucleus.collections.timed.TimedSetMultimap;
import com.jcwhatever.nucleus.managed.commands.response.IRequestContext;
import com.jcwhatever.nucleus.managed.commands.response.IRequestContextBuilder;
import com.jcwhatever.nucleus.managed.commands.response.IResponseRequestor;
import com.jcwhatever.nucleus.managed.commands.response.ResponseType;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;

/**
 * Internal implementation of {@link IResponseRequestor}.
 */
public final class InternalResponseRequestor implements IResponseRequestor, Listener {

    @Localizable static final String _MULTIPLE_REQUESTS =
            "{YELLOW}Multiple requests for response found. Please be more specific:";

    private TimedMultimap<CommandSender, IRequestContext>
            _requests = new TimedSetMultimap<CommandSender, IRequestContext>(
                Nucleus.getPlugin(), 30, TimeScale.SECONDS)
            /* on lifespan end*/
            .onLifespanEnd(new UpdateSubscriber<Entry<CommandSender, Collection<IRequestContext>>>() {

                // notify subscribers that the requests is expired/cancelled.
                @Override
                public void on(Entry<CommandSender, Collection<IRequestContext>> entry) {

                    Collection<IRequestContext> requests = entry.getValue();

                    for (IRequestContext request : requests) {

                        if (request instanceof RequestContext) {
                            ((RequestContext) request).timeout();
                        }
                    }
                }
            });

    /**
     * Constructor
     */
    public InternalResponseRequestor() {
        Bukkit.getPluginManager().registerEvents(this, Nucleus.getPlugin());
    }

    @Override
    @Nullable
    public List<IRequestContext> getRequests(CommandSender sender) {
        PreCon.notNull(sender);

        Collection<IRequestContext> requests = _requests.get(sender);
        if (requests == null)
            return null;

        return new ArrayList<>(requests);
    }

    @Override
    public IRequestContextBuilder getContextBuilder(Plugin plugin) {
        PreCon.notNull(plugin);

        return new RequestContextBuilder(this, plugin);
    }

    public void request(IRequestContext context) {
        PreCon.notNull(context);

        _requests.put(context.getCommandSender(), context, context.getTimeout(), TimeScale.SECONDS);
    }

    public void cancel(IRequestContext context) {
        _requests.remove(context.getCommandSender(), context);
    }

    /*
     * Invoked to check for and process a potential response.
     */
    private boolean onResponse(CommandSender sender, String message) {

        String[] messageComp = TextUtils.PATTERN_SPACE.split(message);

        if (messageComp.length > 2 || messageComp[0].length() == 0)
            return false; // finished

        String commandName = messageComp[0].substring(1);
        String contextName = messageComp.length > 1 ? messageComp[1].toLowerCase() : null;

        if (commandName.isEmpty())
            return false;

        ResponseType type = ResponseType.from(commandName);

        List<IRequestContext> allRequests = getRequests(sender);
        if (allRequests == null || allRequests.isEmpty())
            return false; // finished

        List<IRequestContext> contexts = new ArrayList<>(allRequests.size());

        for (IRequestContext request : allRequests) {
            if (request.contains(type) &&
                    (contextName == null || request.getSearchName().equals(contextName))) {
                contexts.add(request);
            }
        }

        if (contexts.isEmpty())
            return false; // finished

        if (contexts.size() == 1) {
            IRequestContext context = contexts.get(0);
            context.respond(type);
        }
        else if (contextName == null) {
            tellMultipleRequests(sender, type, contexts);
        }
        else {
            return false;
        }

        return true;
    }

    private void tellMultipleRequests(CommandSender sender, ResponseType type,
                                             Collection<IRequestContext> requests) {

        NucMsg.tellAnon(sender, NucLang.get(_MULTIPLE_REQUESTS));

        for (IRequestContext request : requests) {
            NucMsg.tellAnon(sender, '/' + type.getCommandName() + ' ' + request.getName());
        }
    }

    @EventHandler(priority= EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        if (onResponse(event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
        }
    }
}
