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

package com.jcwhatever.nucleus.internal.managed.sounds;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.timed.TimedArrayList;
import com.jcwhatever.nucleus.events.sounds.PlayResourceSoundEvent;
import com.jcwhatever.nucleus.events.sounds.ResourceSoundEndEvent;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.managed.messaging.IMessenger.LineWrapping;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.managed.resourcepacks.ResourcePacks;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IResourceSound;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IVoiceSound;
import com.jcwhatever.nucleus.managed.sounds.ISoundContext;
import com.jcwhatever.nucleus.managed.sounds.ISoundManager;
import com.jcwhatever.nucleus.managed.sounds.SoundSettings;
import com.jcwhatever.nucleus.managed.sounds.Transcript;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.ThreadSingletons;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.Utils;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.nms.INmsSoundEffectHandler;
import com.jcwhatever.nucleus.utils.nms.NmsUtils;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Nucleus implementation of {@link ISoundManager}.
 */
public final class InternalSoundManager implements ISoundManager {

    private static final ThreadSingletons<Location> EFFECT_LOCATION = LocationUtils.createThreadSingleton();

    private final Map<UUID, TimedArrayList<ISoundContext>> _playing = new HashMap<>(100);

    @Nullable
    @Override
    public IResourceSound get(String soundPath) {
        PreCon.notNull(soundPath);

        soundPath = soundPath.toLowerCase();

        String packName;
        String soundName;

        String[] components = TextUtils.PATTERN_DOT.split(soundPath);
        if (components.length == 1) {
            packName = "_default";
            soundName = soundPath;
        }
        else if (components.length == 2) {
            packName = components[0];
            soundName = components[1];
        }
        else {
            return null;
        }

        IResourcePack pack = ResourcePacks.get(packName);
        if (pack == null)
            return null;

        return pack.getSounds().get(soundName);
    }

    @Override
    public Collection<IResourceSound> getPlaying(Player player) {
        return getPlaying(player, new ArrayList<IResourceSound>(10));
    }

    @Override
    public <T extends Collection<IResourceSound>> T getPlaying(Player player, T output) {
        PreCon.notNull(player);
        PreCon.notNull(output);

        List<ISoundContext> playing = _playing.get(player.getUniqueId());

        if (playing == null || playing.isEmpty())
            return output;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (playing) {
            for (ISoundContext play : playing) {
                output.add(play.getResourceSound());
            }
        }

        return output;
    }

    @Override
    public Collection<ISoundContext> getContexts(Player player) {
        return getContexts(player, new ArrayList<ISoundContext>(7));
    }

    @Override
    public <T extends Collection<ISoundContext>> T getContexts(Player player, T output) {
        PreCon.notNull(player);
        PreCon.notNull(output);

        List<ISoundContext> playing = _playing.get(player.getUniqueId());

        if (playing == null || playing.isEmpty())
            return output;

        output.addAll(playing);
        return output;
    }

    @Override
    public IFutureResult<ISoundContext> playSound(Plugin plugin, Player player, IResourceSound sound) {
        return playSound(plugin, player, sound, new SoundSettings(), null);
    }

    @Override
    public IFutureResult<ISoundContext> playSound(Plugin plugin, final Player player, IResourceSound sound,
                                        SoundSettings settings) {
        return playSound(plugin, player, sound, settings, null);
    }

    @Override
    public IFutureResult<ISoundContext> playSound(final Plugin plugin, Player player, IResourceSound sound,
                                        SoundSettings settings,
                                        final @Nullable Collection<Player> transcriptViewers) {
        PreCon.notNull(plugin);
        PreCon.notNull(sound);
        PreCon.notNull(settings);

        settings = new SoundSettings(settings);

        // substitute players location if no locations are provided.
        if (!settings.hasLocations())
            settings.addLocations(player.getLocation());

        SoundContext context = new SoundContext(player, sound, settings);

        // run event
        PlayResourceSoundEvent event = new PlayResourceSoundEvent(player, sound, settings);
        Nucleus.getEventManager().callBukkit(null, event);

        // see if the event was cancelled
        if (event.isCancelled())
            return context.setFinished();

        Collection<Player> target = ArrayUtils.asList(player);

        for (Location location : settings.getLocations()) {

            if (location.getWorld() == null || !location.getWorld().equals(player.getWorld()))
                continue;

            sendSound(target, location, event.getResourceSound().getClientName(),
                    settings.getVolume(), settings.getPitch());
        }

        // get timed list to store playing object in.
        TimedArrayList<ISoundContext> currentPlaying = _playing.get(player.getUniqueId());

        if (currentPlaying == null) {

            // create timed list for player and add a subscriber to handle sounds ending.
            currentPlaying = new TimedArrayList<ISoundContext>(Nucleus.getPlugin(), 3)
                    .onLifespanEnd(new UpdateSubscriber<ISoundContext>() {
                        @Override
                        public void on(ISoundContext item) {

                            ((SoundContext)item).setFinished();

                            ResourceSoundEndEvent event = new ResourceSoundEndEvent(item.getPlayer(),
                                    item.getResourceSound(), item.getSettings());

                            Nucleus.getEventManager().callBukkit(this, event);
                        }
                    });

            _playing.put(player.getUniqueId(), currentPlaying);
        }

        // add playing sound to timed list, will expire when the song ends.
        currentPlaying.add(context, sound.getDurationTicks(), TimeScale.TICKS);

        // display transcript to players if the sound is a voice
        // sound and transcript viewers are provided.
        if (sound instanceof IVoiceSound &&
                transcriptViewers != null &&
                !transcriptViewers.isEmpty()) {

            Transcript transcript = ((IVoiceSound) sound).getTranscript();
            if (transcript != null) {

                transcript.run(plugin, new UpdateSubscriber<String>() {
                    @Override
                    public void on(String text) {

                        for (Player viewer : transcriptViewers) {
                            NucMsg.tell(plugin, viewer, LineWrapping.DISABLED, text);
                        }

                    }
                });
            }
        }

        return context.getFuture();
    }

    @Override
    public void playEffect(String clientSoundName, Player player) {
        PreCon.notNullOrEmpty(clientSoundName);
        PreCon.notNull(player);

        sendSound(ArrayUtils.asList(player),
                player.getLocation(EFFECT_LOCATION.get()), clientSoundName, 1.0f, 1.0f);
    }

    @Override
    public void playEffect(String clientSoundName, Collection<Player> players) {
        PreCon.notNullOrEmpty(clientSoundName);
        PreCon.notNull(players);

        for (Player player : players) {
            playEffect(clientSoundName, player);
        }
    }

    @Override
    public void playEffect(String clientSoundName, Player player, Location location) {
        PreCon.notNullOrEmpty(clientSoundName);
        PreCon.notNull(player);
        PreCon.notNull(location);

        sendSound(ArrayUtils.asList(player),
                location, clientSoundName, 1.0f, 1.0f);
    }

    @Override
    public void playEffect(String clientSoundName, Collection<Player> players, Location location) {
        PreCon.notNullOrEmpty(clientSoundName);
        PreCon.notNull(players);
        PreCon.notNull(location);

        sendSound(players,
                location, clientSoundName, 1.0f, 1.0f);
    }

    @Override
    public void playEffect(String clientSoundName, Player player, float volume, float pitch) {
        PreCon.notNullOrEmpty(clientSoundName);
        PreCon.notNull(player);

        sendSound(ArrayUtils.asList(player),
                player.getLocation(EFFECT_LOCATION.get()), clientSoundName, volume, pitch);
    }

    @Override
    public void playEffect(String clientSoundName, Collection<Player> players, float volume, float pitch) {
        PreCon.notNullOrEmpty(clientSoundName);
        PreCon.notNull(players);

        for (Player player : players) {
            playEffect(clientSoundName, player, volume, pitch);
        }
    }

    @Override
    public void playEffect(String clientSoundName, Player player, Location location,
                           float volume, float pitch) {
        PreCon.notNullOrEmpty(clientSoundName);
        PreCon.notNull(player);
        PreCon.notNull(location);

        sendSound(ArrayUtils.asList(player),
                location, clientSoundName, volume, pitch);
    }

    @Override
    public void playEffect(String clientSoundName, Collection<Player> players, Location location,
                           float volume, float pitch) {
        PreCon.notNullOrEmpty(clientSoundName);
        PreCon.notNull(players);
        PreCon.notNull(location);

        sendSound(players, location, clientSoundName, volume, pitch);
    }

    private static void sendSound(Collection<Player> players, Location location, String soundName,
                                  float volume, float pitch) {

        INmsSoundEffectHandler nmsHandler = NmsUtils.getSoundEffectHandler();

        if (nmsHandler != null) {
            // send sound packet to player
            nmsHandler.send(players, soundName,
                    location.getX(), location.getY(), location.getZ(),
                    volume, pitch);
        }
        else {
            // fallback to using console commands if NMS is unavailable

            for (Player player : players) {
                String cmd = getPlaySoundCommand(soundName, player,
                        location, volume, pitch);

                Utils.executeAsConsole(cmd);
            }
        }
    }

    /*
     * Get the command to run to make a player hear a sound.
     */
    private static String getPlaySoundCommand(String soundName, Player p, Location loc,
                                              float volume, float pitch) {

        return "playsound " + soundName + ' ' + p.getName() + ' ' +
                loc.getBlockX() + ' ' + loc.getBlockY() + ' ' + loc.getBlockZ() + ' '
                + volume + ' ' + pitch;
    }
}
