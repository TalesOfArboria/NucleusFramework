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


package com.jcwhatever.bukkit.generic.commands.response;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Data object holding information about a
 * player response request.
 */
public class ResponseRequest {

    private CommandSender _sender;
    private Plugin _plugin;
    private String _context;
    private IResponseHandler _responseHandler;
    private Set<ResponseType> _responseTypes = new HashSet<>(ResponseType.totalTypes());
    private int _lifespan = 30;

    /**
     * Constructor.
     *
     * @param plugin        The owning plugin.
     * @param context       A context name the sender can use if more than one request is made.
     * @param sender        The command sender to make a request at.
     * @param handler       The {@code ResponseHandler} that handles the players response.
     * @param responseType  The requested responses.
     */
    public ResponseRequest(Plugin plugin, String context,
                           CommandSender sender, IResponseHandler handler,
                           ResponseType... responseType) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(context);
        PreCon.notNull(sender);
        PreCon.notNull(handler);
        PreCon.notNull(responseType);
        PreCon.greaterThanZero(responseType.length);

        _plugin = plugin;
        _context = context;
        _sender = sender;
        _responseHandler = handler;
        Collections.addAll(_responseTypes, responseType);
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the request context name.
     */
    public String getContext() {
        return _context;
    }

    /**
     * Get the command sender..
     */
    public CommandSender getCommandSender() {
        return _sender;
    }

    /**
     * Get the response handler.
     */
    public IResponseHandler getHandler() {
        return _responseHandler;
    }

    /**
     * Get the requested response types.
     */
    public Set<ResponseType> getResponseTypes() {
        return new HashSet<>(_responseTypes);
    }

    /**
     * Get the lifespan of the request in seconds.
     */
    public int getLifespan() {
        return _lifespan;
    }

    /**
     * Set the lifespan of the request in seconds.
     *
     * @param lifespan  The lifespan.
     */
    public void setLifespan(int lifespan) {
        _lifespan = lifespan;
    }

    @Override
    public  int hashCode() {
        return _context.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResponseRequest && ((ResponseRequest) obj)._context.equals(_context);
    }


}
