package com.jcwhatever.nucleus.managed.sounds;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.internal.managed.resourcepacks.ResourcePackSounds;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IEffectSound;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IMusicSound;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IResourceSound;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IVoiceSound;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import com.jcwhatever.nucleus.utils.file.FileUtils;
import com.jcwhatever.v1_8_R3.BukkitTester;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link ISoundManager}.
 */
public class SoundManagerTest {

    private Plugin _plugin = BukkitTester.mockPlugin("dummy");
    private Player _player = BukkitTester.login("dummy");
    private SoundSettings _settings = new SoundSettings();

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

        IResourcePack pack = Nucleus.getResourcePacks().getDefault();
        assert pack != null;

        ((ResourcePackSounds)pack.getSounds()).load(dataNode);
    }

    /**
     * Make sure {@link ISoundManager#get} works correctly.
     */
    @Test
    public void testGet() throws Exception {

        IResourceSound sound = Nucleus.getSoundManager().get("music1");
        assertNotNull(sound);

        assertTrue(sound instanceof IMusicSound);
        assertEquals("music1", sound.getName());

        sound = Nucleus.getSoundManager().get("voice2");
        assertNotNull(sound);

        assertTrue(sound instanceof IVoiceSound);
        assertEquals("voice2", sound.getName());

        sound = Nucleus.getSoundManager().get("effect1");
        assertNotNull(sound);

        assertTrue(sound instanceof IEffectSound);
        assertEquals("effect1", sound.getName());
    }

    /**
     * Make sure {@link ISoundManager#getPlaying(Player)} works correctly.
     * Also tests {@link ISoundManager#playSound} and {@link ISoundManager#getPlaying}.
     */
    @Test
    public void testGetPlaying1() throws Exception {

        // establish that there are no sounds playing to the player
        Collection<IResourceSound> sounds = Nucleus.getSoundManager().getPlaying(_player);
        assertEquals(0, sounds.size());

        IResourceSound sound = Nucleus.getSoundManager().get("voice1");
        assertNotNull(sound);

        // play sound and make sure it is returned
        Nucleus.getSoundManager().playSound(_plugin, _player, sound, _settings);

        sounds = Nucleus.getSoundManager().getPlaying(_player);
        assertEquals(1, sounds.size());

        // wait for sound to end
        BukkitTester.pause(25);

        // make sure the sound ended
        sounds = Nucleus.getSoundManager().getPlaying(_player);
        assertEquals(0, sounds.size());
    }

    /**
     * Make sure {@link ISoundManager#getContexts} works correctly.
     * Also tests {@link ISoundManager#get} and {@link ISoundManager#getContexts}.
     */
    @Test
    public void testGetContexts() throws Exception {

        Collection<ISoundContext> playing = Nucleus.getSoundManager().getContexts(_player);
        assertEquals(0, playing.size());

        IResourceSound sound = Nucleus.getSoundManager().get("effect1");
        assertNotNull(sound);

        Nucleus.getSoundManager().playSound(_plugin, _player, sound, _settings);

        // play sound and check if it is returned
        playing = Nucleus.getSoundManager().getContexts(_player);
        assertEquals(1, playing.size());

        BukkitTester.pause(25); // wait for sound to end

        // make sure sound ended
        playing = Nucleus.getSoundManager().getContexts(_player);
        assertEquals(0, playing.size());
    }
}