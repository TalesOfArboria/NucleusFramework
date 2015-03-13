package com.jcwhatever.nucleus.utils.reflection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import com.jcwhatever.bukkit.v1_8_R2.BukkitTester;

import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.junit.Test;

import net.minecraft.server.v1_8_R2.EntityLiving;

public class ReflectionTest {


    private static class ReflectableTestClass {}


    @Test
    public void testType() throws Exception {

        Reflection reflection = new Reflection(BukkitTester.NMS_TEST_VERSION);

        ReflectedType reflected = reflection.type(
                "com.jcwhatever.nucleus.utils.reflection.ReflectionTest$ReflectableTestClass");

        assertNotNull(reflected);
        assertEquals(ReflectableTestClass.class, reflected.getHandle());
    }

    @Test
    public void testType1() throws Exception {

        Reflection reflection = new Reflection(BukkitTester.NMS_TEST_VERSION);

        ReflectedType reflected = reflection.type(ReflectableTestClass.class);

        assertNotNull(reflected);
        assertEquals(ReflectableTestClass.class, reflected.getHandle());
    }

    @Test
    public void testNmsType() throws Exception {

        Reflection reflection = new Reflection(BukkitTester.NMS_TEST_VERSION);

        ReflectedType reflected = reflection.nmsType("EntityLiving");
        assertNotNull(reflected);
        assertEquals(EntityLiving.class, reflected.getHandle());
    }

    @Test
    public void testCraftType() throws Exception {

        Reflection reflection = new Reflection(BukkitTester.NMS_TEST_VERSION);

        ReflectedType reflected = reflection.craftType("entity.CraftPlayer");
        assertNotNull(reflected);
        assertEquals(CraftPlayer.class, reflected.getHandle());
    }
}