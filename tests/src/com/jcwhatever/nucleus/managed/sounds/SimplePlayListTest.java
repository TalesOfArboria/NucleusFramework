package com.jcwhatever.nucleus.managed.sounds;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.managed.resourcepacks.ResourcePacks;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.playlist.PlayList.PlayerSoundQueue;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.playlist.SimplePlayList;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IResourceSound;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.file.FileUtils;
import com.jcwhatever.v1_8_R3.BukkitTester;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.Plugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class SimplePlayListTest {

    private Plugin _plugin = BukkitTester.mockPlugin("dummy");
    private Player _player = BukkitTester.login("dummy");

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();

        Plugin plugin = BukkitTester.mockPlugin("dummy");
        String yml = FileUtils.scanTextFile(SoundManagerTest.class, "/resource-sounds.yml", StandardCharsets.UTF_8);

        YamlDataNode dataNode = new YamlDataNode(plugin, yml);
        dataNode.load();

        Player player = BukkitTester.login("dummy");

        PlayerResourcePackStatusEvent event1 = new PlayerResourcePackStatusEvent(
                player, PlayerResourcePackStatusEvent.Status.ACCEPTED);
        Bukkit.getPluginManager().callEvent(event1);

        BukkitTester.pause(20);

        PlayerResourcePackStatusEvent event2 = new PlayerResourcePackStatusEvent(
                player, PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED);
        Bukkit.getPluginManager().callEvent(event2);

        BukkitTester.pause(20);
    }

    @Test
    public void testAddSound() throws Exception {

        IResourceSound sound1 = Nucleus.getSoundManager().get("music1");
        IResourceSound sound2 = Nucleus.getSoundManager().get("music2");

        SimplePlayList playList = new SimplePlayList(_plugin);
        assertEquals(0, playList.size());

        playList.addSound(sound1);
        assertEquals(1, playList.size());

        playList.addSound(sound2);
        assertEquals(2, playList.size());
    }

    @Test
    public void testRemoveSound() throws Exception {

        IResourceSound sound1 = Nucleus.getSoundManager().get("music1");
        IResourceSound sound2 = Nucleus.getSoundManager().get("music2");

        SimplePlayList playList = new SimplePlayList(_plugin);
        assertEquals(0, playList.size());

        playList.addSound(sound1);
        playList.addSound(sound2);
        assertEquals(2, playList.size());

        playList.removeSound(sound1);
        assertEquals(1, playList.size());

        playList.removeSound(sound1);
        assertEquals(1, playList.size());

        playList.removeSound(sound2);
        assertEquals(0, playList.size());
    }

    @Test
    public void testAddSounds() throws Exception {

        IResourcePack pack = ResourcePacks.getDefault();
        assert pack != null;

        Collection<IResourceSound> sounds = pack.getSounds().getAll();

        SimplePlayList playList = new SimplePlayList(_plugin);

        playList.addSounds(sounds);
        assertEquals(6, playList.size());
    }

    @Test
    public void testClearSounds() throws Exception {
        IResourcePack pack = ResourcePacks.getDefault();
        assert pack != null;

        Collection<IResourceSound> sounds = pack.getSounds().getAll();

        SimplePlayList playList = new SimplePlayList(_plugin);

        playList.addSounds(sounds);
        assertEquals(6, playList.size());

        playList.clearSounds();
        assertEquals(0, playList.size());
    }

    @Test
    public void testGetSounds() throws Exception {
        IResourcePack pack = ResourcePacks.getDefault();
        assert pack != null;

        Collection<IResourceSound> sounds = pack.getSounds().getAll();

        SimplePlayList playList = new SimplePlayList(_plugin);

        playList.addSounds(sounds);
        assertEquals(6, playList.size());

        sounds = playList.getSounds();
        assertEquals(6, sounds.size());
    }

    @Test
    public void testAddPlayer() throws Exception {

        SoundSettings settings = new SoundSettings();

        IResourcePack pack = ResourcePacks.getDefault();
        assert pack != null;

        Collection<IResourceSound> sounds = pack.getSounds().getAll();

        SimplePlayList playList = new SimplePlayList(_plugin, sounds);
        assertEquals(null, playList.getSoundQueue(_player));

        // add player
        assertNotEquals(null, playList.addPlayer(_player, settings));
        assertNotEquals(null, playList.getSoundQueue(_player));
    }

    @Test
    public void testRemovePlayer() throws Exception {

        SoundSettings settings = new SoundSettings();

        IResourcePack pack = ResourcePacks.getDefault();
        assert pack != null;

        Collection<IResourceSound> sounds = pack.getSounds().getAll();

        SimplePlayList playList = new SimplePlayList(_plugin, sounds);
        assertEquals(null, playList.getSoundQueue(_player));

        // add player
        playList.addPlayer(_player, settings);
        assertNotEquals(null, playList.getSoundQueue(_player));

        // remove player
        assertEquals(true, playList.removePlayer(_player));

        // Still returns sound queue until the audio is finished
        assertNotEquals(null, playList.getSoundQueue(_player));

        // wait for audio to end
        BukkitTester.pause(70);

        // sound queue should no longer be available
        assertEquals(null, playList.getSoundQueue(_player));
    }

    @Test
    public void testSoundQueueNoLoop() throws Exception {

        SoundSettings settings = new SoundSettings();

        List<IResourceSound> sounds = ArrayUtils.asList(
                Nucleus.getSoundManager().get("music1"),
                Nucleus.getSoundManager().get("music2"),
                Nucleus.getSoundManager().get("voice1")
        );

        SimplePlayList playList = new SimplePlayList(_plugin, sounds);
        playList.setLoop(false);

        assertEquals(null, playList.getSoundQueue(_player));

        PlayerSoundQueue queue  = playList.addPlayer(_player, settings);

        assertTrue(queue != null);
        assertEquals(_player, queue.getPlayer());

        BukkitTester.pause(2);

        assertEquals(sounds.get(0), queue.getCurrent());

        BukkitTester.pause(30);

        assertEquals(sounds.get(1), queue.getCurrent());

        BukkitTester.pause(22);

        assertEquals(sounds.get(2), queue.getCurrent());

        BukkitTester.pause(22);

        assertEquals(null, queue.getCurrent());

        BukkitTester.pause(30);

        playList.removePlayer(_player);
    }

    @Test
    public void testSoundQueueLoop() throws Exception {

        List<IResourceSound> sounds = ArrayUtils.asList(
                Nucleus.getSoundManager().get("music1"),
                Nucleus.getSoundManager().get("music2"),
                Nucleus.getSoundManager().get("voice1")
        );

        SimplePlayList playList = new SimplePlayList(_plugin, sounds);
        playList.setLoop(true);

        assertEquals(null, playList.getSoundQueue(_player));

        PlayerSoundQueue queue  = playList.addPlayer(_player, new SoundSettings());

        assertTrue(queue != null);
        assertEquals(_player, queue.getPlayer());

        BukkitTester.pause(2);

        assertEquals(sounds.get(0), queue.getCurrent());

        BukkitTester.pause(30);

        assertEquals(sounds.get(1), queue.getCurrent());

        BukkitTester.pause(22);

        assertEquals(sounds.get(2), queue.getCurrent());

        BukkitTester.pause(22);

        assertEquals(sounds.get(0), queue.getCurrent());

        BukkitTester.pause(22);

        assertEquals(sounds.get(1), queue.getCurrent());

        playList.removePlayer(_player);

        BukkitTester.pause(30);
    }

    @Test
    public void testSoundQueueNoLoopNoSounds() throws Exception {

        SoundSettings settings = new SoundSettings();

        SimplePlayList playList = new SimplePlayList(_plugin);
        playList.setLoop(false);

        assertEquals(null, playList.getSoundQueue(_player));

        // add player
        PlayerSoundQueue queue  = playList.addPlayer(_player, settings);

        assertTrue(queue != null);

        BukkitTester.pause(2);

        assertEquals(true, queue.isRemoved());
        assertEquals(null, playList.getSoundQueue(_player));

        playList.removePlayer(_player);

        BukkitTester.pause(30);
    }

    @Test
    public void testSoundQueueLoopNoSounds() throws Exception {

        SoundSettings settings = new SoundSettings();

        SimplePlayList playList = new SimplePlayList(_plugin);
        playList.setLoop(true);

        assertEquals(null, playList.getSoundQueue(_player));

        // add player
        PlayerSoundQueue queue  = playList.addPlayer(_player, settings);
        assertTrue(queue != null);

        BukkitTester.pause(2);

        assertEquals(true, queue.isRemoved());
        assertEquals(null, playList.getSoundQueue(_player));

        playList.removePlayer(_player);

        BukkitTester.pause(30);
    }
}