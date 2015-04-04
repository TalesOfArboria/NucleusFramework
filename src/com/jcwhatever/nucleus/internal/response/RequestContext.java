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

package com.jcwhatever.nucleus.internal.response;

import com.jcwhatever.nucleus.commands.response.IRequestContext;
import com.jcwhatever.nucleus.commands.response.ResponseType;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Internal implementation of {@link IRequestContext}.
 */
class RequestContext implements IRequestContext {

    private final InternalResponseRequestor _requestor;
    private final Plugin _plugin;
    private final CommandSender _sender;
    private final String _name;
    private final String _searchName;
    private final int _timeout;
    private final Set<ResponseType> _responseTypes;
    private final NamedUpdateAgents _agents = new NamedUpdateAgents();

    private boolean _isCancelled;
    private boolean _isTimeout;
    private ResponseType _response;
    private boolean _hasRequested;

    RequestContext(InternalResponseRequestor requestor, Plugin plugin,
                   CommandSender sender, String name,
                   int timeout, Set<ResponseType> responseTypes) {

        _requestor = requestor;
        _plugin = plugin;
        _sender = sender;
        _name = name;
        _searchName = name.toLowerCase();
        _timeout = timeout;
        _responseTypes = responseTypes;
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    @Override
    public int getTimeout() {
        return _timeout;
    }

    @Override
    public CommandSender getCommandSender() {
        return _sender;
    }

    @Override
    public boolean sendRequest() {

        if (_hasRequested)
            return false;

        _requestor.request(this);
        _hasRequested = true;

        return true;
    }

    @Override
    public boolean contains(ResponseType responseType) {
        PreCon.notNull( responseType);

        return _responseTypes.contains(responseType);
    }

    @Override
    public Collection<ResponseType> getResponseTypes() {
        return new ArrayList<>(_responseTypes);
    }

    @Override
    public boolean isCancelled() {
        return _isCancelled;
    }

    @Override
    public boolean isTimeout() {
        return _isTimeout;
    }

    @Nullable
    @Override
    public ResponseType getResponse() {
        return _response;
    }

    @Override
    public void cancel() {
        _isCancelled = true;
        _requestor.cancel(this);
        _agents.update("onCancel", this);
        _agents.update("onResult", this);
        _agents.disposeAgents();
    }

    @Override
    public void respond(ResponseType responseType) {
        PreCon.notNull(responseType);
        PreCon.isValid(_response == null, "A response has already been set.");

        _response = responseType;
        _agents.update("onRespond", this);
        _agents.update("onResult", this);
        _agents.disposeAgents();
    }

    public void timeout() {
        _isTimeout = true;
        _agents.update("onTimeout", this);
        _agents.update("onResult", this);
        _agents.disposeAgents();
    }

    @Override
    public RequestContext onResult(IUpdateSubscriber<IRequestContext> subscriber) {
        _agents.getAgent("onResult").addSubscriber(subscriber);
        return this;
    }

    @Override
    public RequestContext onRespond(IUpdateSubscriber<IRequestContext> subscriber) {
        _agents.getAgent("onRespond").addSubscriber(subscriber);
        return this;
    }

    @Override
    public RequestContext onCancel(IUpdateSubscriber<IRequestContext> subscriber) {
        _agents.getAgent("onCancel").addSubscriber(subscriber);
        return this;
    }

    @Override
    public RequestContext onTimeout(IUpdateSubscriber<IRequestContext> subscriber) {
        _agents.getAgent("onTimeout").addSubscriber(subscriber);
        return this;
    }
}
