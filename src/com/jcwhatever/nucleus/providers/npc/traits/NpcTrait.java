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

package com.jcwhatever.nucleus.providers.npc.traits;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.INpcRegistry;
import com.jcwhatever.nucleus.providers.npc.events.NpcDespawnEvent.NpcDespawnReason;
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent.NpcSpawnReason;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Abstract implementation of an NPC Trait.
 *
 * <p>The name of the trait is the same as the parent {@link NpcTraitType}</p>
 *
 * <p>The {@link NpcTrait} can optionally implement the {@link java.lang.Runnable}
 * interface. If this is the case, the {@link java.lang.Runnable#run} method is called
 * every tick while the NPC is spawned so long as {@link #canRun} returns true.</p>
 *
 * <p>The trait may also be reused for a different Npc. The trait is only ever used for
 * one Npc at a time, but can be reused for a different NPC once its current NPC is disposed.
 * If the trait cannot be reused, override the {@link #isReusable} method and return false.</p>
 *
 * <p>Whenever the trait is assigned to an {@link INpc}, the {@link #onAttach} method is invoked. This
 * method is intended for optional override and passes the new {@link INpc} as argument.</p>
 */
public abstract class NpcTrait implements INamed, IDisposable {

    private final NpcTraitType _type;

    private INpc _npc;
    private boolean _isEnabled = true;
    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param type  The parent type that instantiated the trait.
     */
    public NpcTrait(NpcTraitType type) {
        PreCon.notNull(type, "type");

        _type = type;
    }

    @Override
    public final String getName() {
        return _type.getName();
    }

    /**
     * Get the traits lookup name.
     */
    public final String getLookupName() {
        return _type.getLookupName();
    }

    /**
     * Get the NPC the trait was instantiated for.
     */
    public final INpc getNpc() {
        return _npc;
    }

    /**
     * Get the registry of the NPC the trait was instantiated for.
     */
    public final INpcRegistry getRegistry() {
        return _npc.getRegistry();
    }

    /**
     * Get the trait type.
     */
    public final NpcTraitType getType() {
        return _type;
    }

    /**
     * Determine if the trait can be reused for a different NPC after its current
     * NPC is disposed.
     *
     * @return  Returns true. Override to change to false.
     */
    public boolean isReusable() {
        return true;
    }

    /**
     * Determine if the trait is enabled.
     */
    public boolean isEnabled() {
        return _isEnabled;
    }

    /**
     * Set the traits enabled state.
     *
     * @param isEnabled  True to enable, false to disable.
     *
     * @return  Self for chaining.
     */
    public NpcTrait setEnabled(boolean isEnabled) {

        if (isEnabled)
            enable();
        else
            disable();

        return this;
    }

    /**
     * Enable the trait.
     *
     * @return  Self for chaining.
     */
    public NpcTrait enable() {
        _isEnabled = true;
        onEnable();
        return this;
    }

    /**
     * Disable the trait.
     *
     * @return  Self for chaining.
     */
    public NpcTrait disable() {
        _isEnabled = false;
        onDisable();
        return this;
    }

    /**
     * Determine if the Trait should be run.
     *
     * <p>Used when the trait implements {@link java.lang.Runnable} to
     * determine if the trait wants to be run. This is always checked
     * before {@link java.lang.Runnable#run} is invoked.</p>
     *
     * <p>The default implementation always returns true if the trait implements
     * {@link java.lang.Runnable}.</p>
     */
    public boolean canRun() {
        return this instanceof Runnable;
    }

    /**
     * Save the trait settings to an {@link IDataNode}.
     *
     * <p>Default implementation does nothing. Intended for optional
     * override.</p>
     *
     * @param dataNode  The data node to save to.
     */
    public void save(IDataNode dataNode) {
        PreCon.notNull(dataNode);
    }

    /**
     * Load trait settings from an {@link IDataNode}.
     *
     * <p>Default implementation does nothing. Intended for optional
     * override.</p>
     *
     * @param dataNode  The data node to load from.
     */
    public void load(IDataNode dataNode) {
        PreCon.notNull(dataNode);
    }

    @Override
    public final boolean isDisposed() {
        return _isDisposed;
    }

    /**
     * @inheritDoc
     *
     * <p>Note that the trait may be un-disposed if the provider reuses it.
     * This can be detected by overriding {@link #onAttach}.</p>
     */
    @Override
    public final void dispose() {

        if (_isDisposed)
            return;

        INpc npc = getNpc();
        if (npc != null) {
            getNpc().getTraits().remove(getName());
        }

        _isDisposed = true;
    }

    /**
     * Invoked when the trait is attached to an {@link INpc}.
     *
     * <p>Intended for optional override.</p>
     *
     * @param npc  The npc that trait is being attached to.
     */
    protected void onAttach(INpc npc) {}

    /**
     * Invoked when the trait is detached from an {@link INpc} and/or
     * disposed.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onDetach() {}

    /**
     * Invoked when traits NPC is spawned.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onSpawn(NpcSpawnReason reason) {}

    /**
     * Invoked when the traits NPC is despawned.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onDespawn(NpcDespawnReason reason) {}

    /**
     * Invoked when the trait is enabled.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onEnable() {}

    /**
     * Invoked when the trait is disabled.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onDisable() {}

    /**
     * Initialize the trait or re-init.
     *
     * @param npc  The NPC the trait is added to.
     */
    void init(INpc npc) {

        if (!isReusable() && _npc != null)
            throw new IllegalStateException("Trait  is not reusable.");

        _isDisposed = false;
        _npc = npc;
        onAttach(npc);
    }
}
