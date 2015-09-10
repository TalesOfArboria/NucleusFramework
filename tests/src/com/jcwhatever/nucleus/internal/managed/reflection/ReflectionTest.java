package com.jcwhatever.nucleus.internal.managed.reflection;

import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.v1_8_R3.BukkitTester;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class ReflectionTest {

    private static class ReflectableTestClass {}

    @BeforeClass
    public static void beforeClass() {
        NucleusTest.init();
    }

    @Test
    public void testType() throws Exception {

        ReflectionContext reflection = new ReflectionContext(BukkitTester.NMS_TEST_VERSION);

        ReflectedType reflected = reflection.type(
                "com.jcwhatever.nucleus.internal.managed.reflection.ReflectionTest$ReflectableTestClass");

        assertNotNull(reflected);
        assertEquals(ReflectableTestClass.class, reflected.getHandle());
    }

    @Test
    public void testType1() throws Exception {

        ReflectionContext reflection = new ReflectionContext(BukkitTester.NMS_TEST_VERSION);

        ReflectedType reflected = reflection.type(ReflectableTestClass.class);

        assertNotNull(reflected);
        assertEquals(ReflectableTestClass.class, reflected.getHandle());
    }

    @Test
    public void testNmsType() throws Exception {

        ReflectionContext reflection = new ReflectionContext(BukkitTester.NMS_TEST_VERSION);

        ReflectedType reflected = reflection.nmsType("EntityLiving");
        assertNotNull(reflected);
        assertEquals(EntityLiving.class, reflected.getHandle());
    }

    @Test
    public void testCraftType() throws Exception {

        ReflectionContext reflection = new ReflectionContext(BukkitTester.NMS_TEST_VERSION);

        ReflectedType reflected = reflection.craftType("entity.CraftPlayer");
        assertNotNull(reflected);
        assertEquals(CraftPlayer.class, reflected.getHandle());
    }
}