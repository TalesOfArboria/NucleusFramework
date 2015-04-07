package com.jcwhatever.nucleus.internal.reflection;

/*
 * 
 */
public class ReflectableType {

    static boolean staticField = true;

    protected String field1 = "string";
    private int field2 = 1;

    private ReflectableTestEnum field3 = ReflectableTestEnum.CONSTANT2;

    protected ReflectableType() {}

    private ReflectableType(String str) {
        field1 = str;
    }

    private String method1() {
        return field1;
    }

    private void method2(String str) {
        field1 = str;
    }

    private static boolean staticMethod1() {
        return staticField;
    }

    private static void staticMethod2(boolean b) {
        staticField = b;
    }

    // enum used for reflection tests
    protected enum ReflectableTestEnum {
        CONSTANT1,
        CONSTANT2
    }

}
