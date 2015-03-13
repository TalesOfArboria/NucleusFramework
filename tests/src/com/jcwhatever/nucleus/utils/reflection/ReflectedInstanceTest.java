package com.jcwhatever.nucleus.utils.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.bukkit.v1_8_R2.BukkitTester;

import org.junit.Before;
import org.junit.Test;

public class ReflectedInstanceTest {

    private Reflection reflection = new Reflection(BukkitTester.NMS_TEST_VERSION);
    private ReflectedType reflectedClass = reflection.type(ReflectableType.class);


    @Before
    public void resetStaticField() {
        ReflectableType.staticField = true;
    }

    @Test
    public void testGet() throws Exception {

        ReflectedInstance instance = reflectedClass.newReflectedInstance();

        Object obj = instance.get("field1");

        assertEquals("string", obj);
    }

    @Test
    public void testSet() throws Exception {

        ReflectableType mock = new ReflectableType();

        ReflectedInstance instance = reflectedClass.reflect(mock);

        instance.set("field1", "test");

        assertEquals("test", mock.field1);
    }

    @Test
    public void testGetFields() throws Exception {

        ReflectedInstance instance = reflectedClass.newReflectedInstance();

        Fields fields = instance.getFields();
        assertTrue(fields != null);
    }
}