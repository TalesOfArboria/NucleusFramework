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
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.EnumUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.potion.PotionEffectType;

/**
 * Abstract implementation of an NPC Trait.
 *
 * <p>The name of the trait is the same as the parent {@link NpcTraitType}</p>
 *
 * <p>The {@link NpcTrait} can optionally implement the {@link java.lang.Runnable}
 * interface. If this is the case, the {@link java.lang.Runnable#run} method is called
 * every tick while the NPC is spawned so long as {@link #canRun} returns true.</p>
 */
public abstract class NpcTrait implements INamed, IDisposable {

    private final INpc _npc;
    private final NpcTraitType _type;

    private boolean _isEnabled = true;
    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param npc       The NPC the trait is for.
     * @param type      The parent type that instantiated the trait.
     */
    public NpcTrait(INpc npc, NpcTraitType type) {
        PreCon.notNull(npc);
        PreCon.notNull(type);

        _npc = npc;
        _type = type;
    }

    @Override
    public String getName() {
        return _type.getName();
    }

    /**
     * Get the traits lookup name.
     */
    public String getLookupName() {
        return _type.getLookupName();
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
    public NpcTraitType getType() {
        return _type;
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

    /**
     * Invoked when the trait is added to an {@link INpc}.
     *
     * <p>This is invoked by the external implementations of the
     * {@link com.jcwhatever.nucleus.providers.npc.INpcProvider}.</p>
     */
    public void onAdd() {}

    /**
     * Invoked when the trait is removed from an {@link INpc}.
     *
     * <p>This is invoked by the external implementations of the
     * {@link com.jcwhatever.nucleus.providers.npc.INpcProvider}.</p>
     */
    public void onRemove() {}

    /**
     * Invoked when traits NPC is spawned.
     *
     * <p>This is invoked by the external implementations of the
     * {@link com.jcwhatever.nucleus.providers.npc.INpcProvider}.</p>
     */
    public void onSpawn() {}

    /**
     * Invoked when the traits NPC is despawned.
     *
     * <p>This is invoked by the external implementations of the
     * {@link com.jcwhatever.nucleus.providers.npc.INpcProvider}.</p>
     */
    public void onDespawn() {}

    /**
     * Invoked when the trait is enabled.
     */
    protected void onEnable() {}

    /**
     * Invoked when the trait is disabled.
     */
    protected void onDisable() {}

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        getNpc().getTraits().remove(getName());

        _isDisposed = true;
    }


    /**
     * Get an enum from an object. The object must be an instance of the
     * enum or the name of the enum.
     *
     * @param name       The enum constant name.
     * @param enumClass  The enum class.
     *
     * @param <T>  The enum type.
     *
     * @return  The enum constant.
     */
    protected <T extends Enum> T getEnum(Object name, Class<T> enumClass) {

        if (name instanceof String) {

            String str = (String) name;

            if (str.equals(".random")) {
                T[] constants = enumClass.getEnumConstants();
                return Rand.get(constants);
            } else if (str.startsWith(".oneOf:")) {

                str = str.substring(7);

                String[] options = TextUtils.PATTERN_COMMA.split(str);
                str = Rand.get(options).trim();
            }

            T result = (T) EnumUtils.searchEnum(str, enumClass);
            if (result == null)
                throw new IllegalArgumentException("Invalid enum constant name for type: " +
                        enumClass.getName() +
                        "\nValid values are: " +
                        TextUtils.concat(enumClass.getEnumConstants(), ", "));

            return result;
        }
        else if (enumClass.isInstance(name)) {
            return enumClass.cast(name);
        }
        else {
            throw new IllegalArgumentException("Invalid type provided. Unable to convert to type: "
                    + enumClass.getName());
        }
    }

    /**
     * Get a {@link org.bukkit.potion.PotionEffectType} from an object. The object must be
     * an instance of {@link org.bukkit.potion.PotionEffectType} or the name of the type.
     *
     * @param object  The potion effect type or name.
     *
     * @return  The potion effect type.
     */
    protected PotionEffectType getPotionEffectType(Object object) {

        if (object instanceof String) {
            String name = ((String)object).toUpperCase();

            PotionEffectType type = PotionEffectType.getByName(name);
            if (type == null)
                throw new IllegalArgumentException(name + " is not a valid PotionEffectType.");

            return type;
        }
        else if (object instanceof PotionEffectType) {
            return (PotionEffectType)object;
        }
        else {
            throw new IllegalArgumentException("Expected PotionEffectType or name of type.");
        }
    }
}
