package com.jcwhatever.nucleus.utils.reflection;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.BukkitTest;

import org.junit.Test;

import java.lang.reflect.Field;

public class ReflectedFieldTest {

    private Reflection reflection = new Reflection(BukkitTest.NMS_TEST_VERSION);
    private ReflectedType reflectedClass = reflection.type(ReflectableType.class);
    private ReflectedInstance instance = reflectedClass.newReflectedInstance();


    private ReflectedField getTestField() throws Exception {

        Field field = ReflectableType.class.getDeclaredField("field1");
        field.setAccessible(true);

        CachedReflectedType reflectedType = new CachedReflectedType(ReflectableType.class);

        return new ReflectedField(reflectedType, field);
    }

    @Test
    public void testGetName() throws Exception {

        ReflectedField field = getTestField();

        assertEquals("field1", field.getName());
    }

    @Test
    public void testGetModifiers() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(4, field.getModifiers());
    }

    @Test
    public void testGetCurrentModifiers() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(4, field.getCurrentModifiers());
    }

    @Test
    public void testIsStatic() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(false, field.isStatic());

    }

    @Test
    public void testIsFinal() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(false, field.isFinal());
    }

    @Test
    public void testIsPrivate() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(false, field.isPrivate());
    }

    @Test
    public void testIsNative() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(false, field.isNative());
    }

    @Test
    public void testIsProtected() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(true, field.isProtected());
    }

    @Test
    public void testIsPublic() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(false, field.isPublic());

    }

    @Test
    public void testIsStrict() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(false, field.isStrict());

    }

    @Test
    public void testIsSynchronized() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(false, field.isSynchronized());
    }

    @Test
    public void testIsTransient() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(false, field.isTransient());
    }

    @Test
    public void testIsVolatile() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(false, field.isVolatile());
    }

    @Test
    public void testGetOwnerType() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(ReflectableType.class, field.getOwnerType().getHandle());
    }

    @Test
    public void testGetType() throws Exception {

        ReflectedField field = getTestField();

        assertEquals(String.class, field.getType().getHandle());
    }

    @Test
    public void testGet() throws Exception {

        ReflectedField field = getTestField();
        ReflectableType instance = new ReflectableType();

        assertEquals("string", field.get(instance));
    }

    @Test
    public void testSet() throws Exception {

        ReflectedField field = getTestField();
        ReflectableType instance = new ReflectableType();

        field.set(instance, "test");

        assertEquals("test", field.get(instance));
    }

}