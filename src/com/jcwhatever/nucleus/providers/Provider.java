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

package com.jcwhatever.nucleus.providers;

import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Abstract implementation of a NucleusFramework service provider.
 */
public abstract class Provider implements IProvider {

    private IProviderInfo _info;
    private boolean _isTypesRegistered;
    private boolean _isEnabled;
    private boolean _isDisabled;

    @Override
    public final IProviderInfo getInfo() {
        if (_info == null)
            throw new IllegalStateException("Provider info not set yet.");

        return _info;
    }

    @Override
    public final void registerTypes() {
        if (_isTypesRegistered)
            throw new IllegalStateException("Types can only be registered once.");

        _isTypesRegistered = true;

        onRegister();
    }

    @Override
    public void enable() {
        if (_isEnabled)
            throw new IllegalStateException("Provider can only be enabled once.");

        _isEnabled = true;

        onEnable();
    }

    @Override
    public void disable() {
        if (!_isEnabled)
            throw new IllegalStateException("Provider cannot be disabled until it is enabled.");

        if (_isDisabled)
            throw new IllegalStateException("Provider can only be disabled once.");

        _isDisabled = true;

        onDisable();
    }

    @Override
    public final void setInfo(IProviderInfo info) {
        PreCon.notNull(info);

        if (_info != null)
            throw new IllegalStateException("Provider info can only be set once.");

        _info = info;
    }

    /**
     * Invoked to register provider supplied types.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onRegister() {}

    /**
     * Invoked when the provider is enabled.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onEnable() {}

    /**
     * Invoked when the provider is disabled.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onDisable() {}
}
