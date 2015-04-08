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

import com.jcwhatever.nucleus.managed.commands.response.IRequestContext;
import com.jcwhatever.nucleus.managed.commands.response.IRequestContextBuilder;
import com.jcwhatever.nucleus.managed.commands.response.ResponseType;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Internal implementation of {@link IRequestContextBuilder}
 */
class RequestContextBuilder implements IRequestContextBuilder {

    private final InternalResponseRequestor _requestor;
    private final Plugin _plugin;
    private final Set<ResponseType> _responseTypes = new HashSet<>(10);

    private String _name;
    private int _timeout;

    /**
     * Constructor.
     *
     * @param requestor  The parent requestor.
     * @param plugin     The contexts owning plugin.
     */
    public RequestContextBuilder(InternalResponseRequestor requestor, Plugin plugin) {
        PreCon.notNull(plugin);

        _requestor = requestor;
        _plugin = plugin;
    }

    @Override
    public RequestContextBuilder2 name(String contextName) {
        PreCon.notNullOrEmpty(contextName);

        _name = contextName;

        return new RequestContextBuilder2();
    }

    public class RequestContextBuilder2 implements IRequestContextBuilder2 {

        @Override
        public RequestContextBuilder3 timeout(int seconds) {
            PreCon.greaterThanZero(seconds);
            _timeout = seconds;

            return new RequestContextBuilder3();
        }
    }

    public class RequestContextBuilder3 implements IRequestContextBuilder3 {

        @Override
        public RequestContextBuilder4 response(ResponseType acceptableResponse) {
            PreCon.notNull(acceptableResponse);

            _responseTypes.add(acceptableResponse);

            if (this instanceof RequestContextBuilder4)
                return (RequestContextBuilder4)this;

            return new RequestContextBuilder4();
        }
    }

    public class RequestContextBuilder4 extends RequestContextBuilder3 implements IRequestContextBuilder4 {

        @Override
        public IRequestContext build(CommandSender sender) {
            return new RequestContext(
                    _requestor, _plugin, sender, _name, _timeout, _responseTypes);
        }
    }
}
