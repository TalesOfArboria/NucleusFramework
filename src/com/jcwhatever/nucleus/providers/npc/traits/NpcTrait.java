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
import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.INpcRegistry;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Abstract implementation of an NPC Trait.
 *
 * <p>The name of the trait is the same as the parent {@link INpcTraitType}</p>
 *
 * <p>The {@link NpcTrait} can optionally implement the {@link Runnable}
 * interface. If this is the case, the {@code Runnable#run} method is called
 * every tick while the NPC is spawned so long as {@code #canRun} returns true.</p>
 */
public abstract class NpcTrait implements INamedInsensitive, IDisposable {

    private final INpc _npc;
    private final INpcTraitType _type;
    private final IDataNode _dataNode;

    /**
     * Constructor.
     *
     * @param npc       The NPC the trait is for.
     * @param type      The parent type that instantiated the trait.
     * @param dataNode  The traits data storage node.
     */
    public NpcTrait(INpc npc, INpcTraitType type, IDataNode dataNode) {
        PreCon.notNull(npc);
        PreCon.notNull(type);
        PreCon.notNull(dataNode);

        _npc = npc;
        _type = type;
        _dataNode = dataNode;
    }

    @Override
    public String getName() {
        return _type.getName();
    }

    @Override
    public String getSearchName() {
        return _type.getSearchName();
    }

    /**
     * Get the NPC the trait was instantiated for.
     */
    public INpc getNpc() {
        return _npc;
    }

    /**
     * Get the registry of the NPC the trait was instantiated for.
     */
    public INpcRegistry getRegistry() {
        return _npc.getRegistry();
    }

    /**
     * Get the trait type.
     */
    public INpcTraitType getType() {
        return _type;
    }

    /**
     * Determine if the Trait should be run.
     *
     * <p>Used when the trait implements {@code Runnable} to
     * determine if the trait wants to be run. This is always checked
     * before {@code Runnable#run} is invoked.</p>
     *
     * <p>The default implementation always returns true if the trait implements
     * {@code Runnable}.</p>
     */
    public boolean canRun() {
        return this instanceof Runnable;
    }

    /**
     * Save the trait to its dedicated data node.
     */
    public void save() {
        onSave(_dataNode);
    }

    /**
     * Invoked when the trait is added to an {@code INpc}.
     *
     * <p>This is invoked by the external implementations of the
     * {@code INpcProvider}.</p>
     */
    public void onAdd() {}

    /**
     * Invoked when the trait is removed from an {@code INpc}.
     *
     * <p>This is invoked by the external implementations of the
     * {@code INpcProvider}.</p>
     */
    public void onRemove() {}

    /**
     * Invoked when traits NPC is spawned.
     *
     * <p>This is invoked by the external implementations of the
     * {@code INpcProvider}.</p>
     */
    public void onSpawn() {}

    /**
     * Invoked when the traits NPC is despawned.
     *
     * <p>This is invoked by the external implementations of the
     * {@code INpcProvider}.</p>
     */
    public void onDespawn() {}

    /**
     * Invoked when the trait needs to be saved.
     *
     * @param dataNode  A data node dedicated to storing trait data.
     */
    protected abstract void onSave(IDataNode dataNode);
}
