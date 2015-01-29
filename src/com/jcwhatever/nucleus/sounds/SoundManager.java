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


package com.jcwhatever.nucleus.sounds;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.Nucleus.NmsHandlers;
import com.jcwhatever.nucleus.collections.timed.TimedArrayList;
import com.jcwhatever.nucleus.events.sounds.PlayResourceSoundEvent;
import com.jcwhatever.nucleus.events.sounds.ResourceSoundEndEvent;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.messaging.IMessenger.LineWrapping;
import com.jcwhatever.nucleus.sounds.Playing.SoundFuture;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.Utils;
import com.jcwhatever.nucleus.utils.nms.INmsSoundEffectHandler;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Manages resource sounds globally.
 */
public final class SoundManager {

    private SoundManager() {}

    private static final Map<UUID, TimedArrayList<Playing>> _playing = new HashMap<>(100);
    private static Map<String, ResourceSound> _sounds;

    static {
        IDataNode dataNode = DataStorage.get(Nucleus.getPlugin(), new DataPath("resource-sounds"));
        dataNode.load();

        load(dataNode);
    }

    /**
     * Get a resource sound by name.
     *
     * @param name  The name of the sound.
     */
    @Nullable
    public static ResourceSound getSound(String name) {
        PreCon.notNull(name);

        return _sounds.get(name.toLowerCase());
    }

    /**
     * Get all resource sounds.
     */
    public static List<ResourceSound> getSounds() {
        return CollectionUtils.unmodifiableList(_sounds.values());
    }

    /**
     * Get resource sounds by type.
     *
     * @param type  The type to look for.
     */
    public static <T extends ResourceSound> List<T> getSounds(Class<T> type) {
        PreCon.notNull(type);

        List<T> results = new ArrayList<>(_sounds.size());

        for (ResourceSound sound : _sounds.values()) {

            if (type.isInstance(sound)) {

                @SuppressWarnings("unchecked")
                T result = (T) sound;

                results.add(result);
            }
        }

        return CollectionUtils.unmodifiableList(results);
    }

    /**
     * Get the resource sounds being played to the specified player.
     *
     * @param player  The player to check.
     */
    public static List<ResourceSound> getSounds(Player player) {
        PreCon.notNull(player);

        List<Playing> playing = _playing.get(player.getUniqueId());

        if (playing == null || playing.isEmpty())
            return CollectionUtils.unmodifiableList();

        List<ResourceSound> result = new ArrayList<>(playing.size());

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (playing) {
            for (Playing play : playing) {
                result.add(play.getResourceSound());
            }
        }

        return CollectionUtils.unmodifiableList(result);
    }

    /**
     * Get information about the resource sounds being played
     * for the specified player.
     *
     * @param player  The player to check.
     */
    public static List<Playing> getPlaying(Player player) {
        PreCon.notNull(player);

        List<Playing> playing = _playing.get(player.getUniqueId());

        if (playing == null || playing.isEmpty())
            return CollectionUtils.unmodifiableList();

        return CollectionUtils.unmodifiableList(playing);
    }

    /**
     * Play a resource sound to a player at the players location.
     *
     * @param plugin  The requesting plugin.
     * @param p       The player who will hear the sound.
     * @param sound   The resource sound to play.
     *
     * @return  A future used to run a callback when the sound is finished playing.
     */
    public static SoundFuture playSound(Plugin plugin, Player p, ResourceSound sound) {
        return playSound(plugin, p, sound, new SoundSettings(), null);
    }

    /**
     * Play a resource sound to a player.
     *
     * @param plugin    The requesting plugin.
     * @param p         The player who will hear the sound.
     * @param sound     The resource sound to play.
     * @param settings  The settings to use.
     *
     * @return  A future used to run a callback when the sound is finished playing.
     */
    public static SoundFuture playSound(Plugin plugin, final Player p, ResourceSound sound,
                                   SoundSettings settings) {
        return playSound(plugin, p, sound, settings, null);
    }

    /**
     * Play a resource sound to a player.
     *
     * @param plugin             The requesting plugin.
     * @param p                  The player who will hear the sound.
     * @param sound              The resource sound to play.
     * @param settings           The settings to use to play the sound. The settings are cloned
     *                           within the method. If the setting has no locations, the players
     *                           location is used.
     * @param transcriptViewers  Players who will see the sound transcript, if any.
     *
     * @return  A future used to run a callback when the sound is finished playing.
     */
    public static SoundFuture playSound(final Plugin plugin, Player p, ResourceSound sound,
                                   SoundSettings settings,
                                   final @Nullable Collection<Player> transcriptViewers) {
        PreCon.notNull(plugin);
        PreCon.notNull(sound);
        PreCon.notNull(settings);

        settings = new SoundSettings(settings);

        // substitute players location if no locations are provided.
        if (!settings.hasLocations())
            settings.addLocations(p.getLocation());

        Playing playing = new Playing(p, sound, settings);

        // run event
        PlayResourceSoundEvent event = new PlayResourceSoundEvent(p, sound, settings);
        Nucleus.getEventManager().callBukkit(null, event);

        // see if the event was cancelled
        if (event.isCancelled())
            return playing.setFinished();

        INmsSoundEffectHandler nmsHandler = Nucleus.getNmsManager()
                .getNmsHandler(NmsHandlers.SOUND_EFFECT.name());

        // run play sound command
        for (Location location : settings.getLocations()) {

            if (location.getWorld() == null || !location.getWorld().equals(p.getWorld()))
                continue;

            if (nmsHandler != null) {
                // send sound packet to player
                nmsHandler.send(p, sound.getName(),
                        location.getX(), location.getY(), location.getZ(),
                        settings.getVolume(), settings.getPitch());
            }
            else {
                // fallback to using console commands if NMS is unavailable
                String cmd = getPlaySoundCommand(event.getResourceSound().getName(), event.getPlayer(),
                        location, settings.getVolume(), settings.getPitch());

                Utils.executeAsConsole(cmd);
            }
        }

        // get timed list to store playing object in.
        TimedArrayList<Playing> currentPlaying = _playing.get(p.getUniqueId());

        if (currentPlaying == null) {

            // create timed list for player and add a subscriber to handle sounds ending.
            currentPlaying = new TimedArrayList<Playing>(Nucleus.getPlugin(), 3)
            .onLifespanEnd(new UpdateSubscriber<Playing>() {
                @Override
                public void on(Playing item) {

                    item.setFinished();

                    ResourceSoundEndEvent event = new ResourceSoundEndEvent(item.getPlayer(),
                            item.getResourceSound(), item.getSettings());

                    Nucleus.getEventManager().callBukkit(this, event);
                }
            });

            _playing.put(p.getUniqueId(), currentPlaying);
        }

        // add playing sound to timed list, will expire when the song ends.
        currentPlaying.add(playing, sound.getDurationTicks(), TimeScale.TICKS);

        // display transcript to players if the sound is a voice
        // sound and transcript viewers are provided.
        if (sound instanceof VoiceSound &&
                transcriptViewers != null &&
                !transcriptViewers.isEmpty()) {

            Transcript transcript = ((VoiceSound) sound).getTranscript();
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

        return playing.getFuture();
    }

    /*
     * Get the command to run to make a player hear a sound.
     */
    private static String getPlaySoundCommand(String soundName, Player p, Location loc, float volume, float pitch) {

        return "playsound " + soundName + ' ' + p.getName() + ' ' +
                loc.getBlockX() + ' ' + loc.getBlockY() + ' ' + loc.getBlockZ() + ' '
                + volume + ' ' + pitch;
    }

    /*
     * Load resource sounds
     */
    static void load(IDataNode dataNode) {

        _sounds = new HashMap<>(dataNode.size());

        for (IDataNode soundNode : dataNode) {

            ResourceSound sound = getNewSound(soundNode);

            _sounds.put(sound.getName().toLowerCase(), sound);
        }
    }

    /*
     * Create a new resource sound instance
     */
    private static ResourceSound getNewSound(IDataNode node) {

        String type = node.getString("type");

        if (type == null || type.isEmpty()) {
            int diskId = node.getInteger("disk-id", -1);
            type = diskId == -1
                    ? "music"
                    : "disc";
        }

        switch(type.toLowerCase()) {

            case "music":
                return new MusicSound(node);

            case "disc":
                return new MusicDiskSound(node);

            case "effect":
                return new EffectSound(node);

            case "voice":
                return new VoiceSound(node);

            default:
                throw new RuntimeException("Invalid sound type in resource sounds: " + type);
        }
    }

}
