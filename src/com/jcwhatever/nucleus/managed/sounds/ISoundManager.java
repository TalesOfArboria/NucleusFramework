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

package com.jcwhatever.nucleus.managed.sounds;

import com.jcwhatever.nucleus.managed.sounds.types.ResourceSound;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Interface for the global resource sound manager.
 */
public interface ISoundManager {

    /**
     * Get a resource sound by name.
     *
     * @param name  The name of the sound.
     */
    @Nullable
    ResourceSound getSound(String name);

    /**
     * Get all resource sounds.
     */
    Collection<ResourceSound> getSounds();

    /**
     * Get resource sounds by type.
     *
     * @param type  The type to look for.
     */
    <T extends ResourceSound> Collection<T> getSounds(Class<T> type);

    /**
     * Get the resource sounds being played to the specified player.
     *
     * @param player  The player to check.
     */
    Collection<ResourceSound> getSounds(Player player);

    /**
     * Get information about the resource sounds being played
     * for the specified player.
     *
     * @param player  The player to check.
     */
    Collection<ISoundContext> getContexts(Player player);

    /**
     * Play a resource sound to a player at the players location.
     *
     * @param plugin  The requesting plugin.
     * @param player  The player who will hear the sound.
     * @param sound   The resource sound to play.
     *
     * @return  A future used to run a success callback when the sound is finished playing.
     */
    IFutureResult<ISoundContext> playSound(Plugin plugin, Player player, ResourceSound sound);

    /**
     * Play a resource sound to a player.
     *
     * @param plugin    The requesting plugin.
     * @param player    The player who will hear the sound.
     * @param sound     The resource sound to play.
     * @param settings  The settings to use.
     *
     * @return  A future used to run a success callback when the sound is finished playing.
     */
    IFutureResult<ISoundContext> playSound(Plugin plugin, Player player,
                                    ResourceSound sound, SoundSettings settings);

    /**
     * Play a resource sound to a player.
     *
     * @param plugin             The requesting plugin.
     * @param player             The player who will hear the sound.
     * @param sound              The resource sound to play.
     * @param settings           The settings to use to play the sound. The settings are cloned
     *                           within the method. If the setting has no locations, the players
     *                           location is used.
     * @param transcriptViewers  Players who will see the sound transcript, if any.
     *
     * @return  A future used to run a callback when the sound is finished playing.
     */
    IFutureResult<ISoundContext> playSound(Plugin plugin, Player player,
                                    ResourceSound sound, SoundSettings settings,
                                    @Nullable Collection<Player> transcriptViewers);
}
