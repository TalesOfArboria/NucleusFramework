package com.jcwhatever.nucleus.commands.arguments;

import com.jcwhatever.bukkit.MockPlayer;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.DummyCommand;
import com.jcwhatever.nucleus.commands.DummyCommand.CommandInfoBuilder;
import com.jcwhatever.nucleus.commands.DummyDispatcher;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.utils.items.ItemStackBuilder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;

public class CommandArgumentsTest {

    private CommandArguments getArguments(CommandInfo info, String... args) throws CommandException {

        DummyDispatcher dispatcher = new DummyDispatcher();

        DummyCommand command = (DummyCommand) dispatcher.getCommand("dummy");
        assert command != null;

        command.setInfo(info);

        return new CommandArguments(command, args);
    }

    // Get arguments for a command that expects 1 static parameter "param1"
    private CommandArguments getParseArguments(String argument) throws CommandException {
        return getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1").build(),
                argument != null ? new String[] { argument } : new String[0]);
    }

    // Get arguments for a command that has 1 optional static parameter "param1"
    private CommandArguments getOptionalArguments(String argument) throws CommandException {
        return getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1=optional").build(),
                argument != null ? new String[] { argument } : new String[0]);
    }

    public enum TestEnum {
        CONSTANT
    }

    @BeforeClass
    public static void testStartup() {

        NucleusTest.init();
    }

    @Test
    public void testGetRawArguments() throws Exception {

        CommandArguments args = getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1", "param2").build(),
                "arg1", "arg2");

        Assert.assertArrayEquals(new String[]{"arg1", "arg2"}, args.getRawArguments());
    }

    @Test
    public void testStaticSize() throws Exception {

        CommandArguments args = getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1", "param2").build(),
                "arg1", "arg2");

        // returns the number of static parameters, not arguments
        Assert.assertEquals(2, args.staticSize());


        args = getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1", "param2=optional").build(),
                "arg1");

        // returns the number of static parameters, not arguments
        Assert.assertEquals(2, args.staticSize());
    }

    @Test
    public void testFloatingSize() throws Exception {

        CommandArguments args = getArguments(new CommandInfoBuilder("dummy")
                        .floatingParams("param1", "param2").build(),
                "-param1", "arg1", "-param2", "arg2");

        // returns the number of floating parameters, not arguments
        Assert.assertEquals(2, args.floatingSize());


        args = getArguments(new CommandInfoBuilder("dummy")
                        .floatingParams("param1", "param2=optional").build(),
                "-param1", "arg1");

        // returns the number of floating parameters, not arguments
        Assert.assertEquals(2, args.floatingSize());
    }

    @Test
    public void testGet() throws Exception {

        CommandArguments args = getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1")
                        .floatingParams("param2").build(),
                "arg1", "-param2", "arg2");

        Assert.assertEquals("arg1", args.get("param1").getValue());
        Assert.assertEquals("arg2", args.get("param2").getValue());
    }

    @Test
    public void testIterator() throws Exception {
        CommandArguments args = getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1")
                        .floatingParams("param2").build(),
                "arg1", "-param2", "arg2");

        Iterator<CommandArgument> iterator = args.iterator();

        Assert.assertEquals("arg1", iterator.next().getValue());
        Assert.assertEquals("arg2", iterator.next().getValue());
        Assert.assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testGetName() throws Exception {

        CommandArguments args = getParseArguments("validName");

        // should not throw any exceptions
        Assert.assertEquals("validName", args.getName("param1"));

        // ------------

        args = getParseArguments("#invalidName");

        // check for invalid argument, name has invalid character at beginning
        try {
            args.getName("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}


        // ------------

        args = getParseArguments("NameLongerThan16Characters");

        // check for invalid argument, name is too long
        try {
            args.getName("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}


        // check for invalid argument, name is too long
        try {
            args.getName("param1", 20); // name cannot be longer than 20 characters
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}


        // ------------

        args = getParseArguments("shortName");

        // check for invalid argument, name is too long
        try {
            args.getName("param1", 8); // name cannot be longer than 8 characters
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}

    }

    @Test
    public void testGetString() throws Exception {
        CommandArguments args = getParseArguments("string");

        Assert.assertEquals("string", args.getString("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.getString("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testGetBoolean() throws Exception {
        CommandArguments args;

        // should not throw any exceptions
        args = getParseArguments("true");
        Assert.assertEquals(true, args.getBoolean("param1"));

        args = getParseArguments("yes");
        Assert.assertEquals(true, args.getBoolean("param1"));

        args = getParseArguments("allow");
        Assert.assertEquals(true, args.getBoolean("param1"));

        args = getParseArguments("on");
        Assert.assertEquals(true, args.getBoolean("param1"));

        args = getParseArguments("1");
        Assert.assertEquals(true, args.getBoolean("param1"));


        args = getParseArguments("false");
        Assert.assertEquals(false, args.getBoolean("param1"));

        args = getParseArguments("no");
        Assert.assertEquals(false, args.getBoolean("param1"));

        args = getParseArguments("deny");
        Assert.assertEquals(false, args.getBoolean("param1"));

        args = getParseArguments("off");
        Assert.assertEquals(false, args.getBoolean("param1"));

        args = getParseArguments("0");
        Assert.assertEquals(false, args.getBoolean("param1"));

        // ------------

        // check for runtime exception, invalid parameter name specified
        try {
            args.getBoolean("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

        // ------------

        // check for invalid argument
        args = getParseArguments("notABoolean");

        try {
            args.getBoolean("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}
    }

    @Test
    public void testGetChar() throws Exception {

        CommandArguments args = getParseArguments("c");

        // should not throw any exceptions
        Assert.assertEquals('c', args.getChar("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.getChar("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

        // ------------

        // check for invalid argument
        args = getParseArguments("invalidChar");

        try {
            args.getChar("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}
    }

    @Test
    public void testGetByte() throws Exception {

        CommandArguments args = getParseArguments("10");

        // should not throw any exceptions
        Assert.assertEquals((byte)10, args.getByte("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.getByte("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

        // ------------

        // check for invalid argument
        args = getParseArguments("invalidByte");

        try {
            args.getByte("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}

        // ------------

        // check for invalid argument, number out of range
        args = getParseArguments(String.valueOf(Byte.MAX_VALUE + 1));

        try {
            args.getByte("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}
    }

    @Test
    public void testGetShort() throws Exception {

        CommandArguments args = getParseArguments("10");

        // should not throw any exceptions
        Assert.assertEquals(10, args.getShort("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.getShort("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

        // ------------

        // check for invalid argument
        args = getParseArguments("invalidShort");

        try {
            args.getShort("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}

        // ------------

        // check for invalid argument, number out of range
        args = getParseArguments(String.valueOf(Short.MAX_VALUE + 1));

        try {
            args.getShort("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}
    }


    @Test
    public void testGetInteger() throws Exception {

        CommandArguments args = getParseArguments("10");

        // should not throw any exceptions
        Assert.assertEquals(10, args.getInteger("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.getInteger("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

        // ------------

        // check for invalid argument
        args = getParseArguments("invalidInteger");

        try {
            args.getInteger("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}

        // ------------

        // check for invalid argument, number out of range
        args = getParseArguments(String.valueOf(Integer.MAX_VALUE + 1L));

        try {
            args.getInteger("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}
    }

    @Test
    public void testGetLong() throws Exception {

        CommandArguments args = getParseArguments("10");

        // should not throw any exceptions
        Assert.assertEquals(10, args.getLong("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.getLong("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

        // ------------

        // check for invalid argument
        args = getParseArguments("invalidLong");

        try {
            args.getLong("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}

        // ------------

        // check for invalid argument, number out of range
        args = getParseArguments("99999999999999999999999999999999999999999999999999999999999");

        try {
            args.getLong("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}
    }

    @Test
    public void testGetFloat() throws Exception {

        CommandArguments args = getParseArguments("10.0");

        // should not throw any exceptions
        Assert.assertEquals(10.0D, args.getFloat("param1"), 0.0D);

        // check for runtime exception, invalid parameter name specified
        try {
            args.getFloat("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

        // ------------

        // check for invalid argument
        args = getParseArguments("invalidFloat");

        try {
            args.getFloat("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}

    }

    @Test
    public void testGetDouble() throws Exception {
        CommandArguments args = getParseArguments("10.0");

        // should not throw any exceptions
        Assert.assertEquals(10.0D, args.getFloat("param1"), 0.0D);

        // check for runtime exception, invalid parameter name specified
        try {
            args.getDouble("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

        // ------------

        // check for invalid argument
        args = getParseArguments("invalidDouble");

        try {
            args.getDouble("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}
    }

    @Test
    public void testGetPercent() throws Exception {
        CommandArguments args = getParseArguments("10.0%");

        // should not throw any exceptions
        Assert.assertEquals(10.0D, args.getPercent("param1"), 0.0D);

        // check for runtime exception, invalid parameter name specified
        try {
            args.getPercent("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

        // ------------

        // check for invalid argument
        args = getParseArguments("invalidPercent");

        try {
            args.getPercent("param1");
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}
    }

    @Test
    public void testGetParams() throws Exception {
        CommandArguments args = getParseArguments("1 2 3");

        // should not throw any exceptions
        Assert.assertArrayEquals(new String[] { "1", "2", "3" }, args.getParams("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.getParams("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testGetItemStack() throws Exception {

        ItemStack wood = new ItemStackBuilder(Material.WOOD).build();
        ItemStack woodAmount = new ItemStackBuilder(Material.WOOD).amount(5).build();

        CommandArguments args;

        args = getParseArguments("wood");
        Assert.assertArrayEquals(new ItemStack[] { wood }, args.getItemStack(new MockPlayer("dummy"), "param1"));

        args = getParseArguments("wood,wood");
        Assert.assertArrayEquals(new ItemStack[] { wood, wood }, args.getItemStack(new MockPlayer("dummy"), "param1"));

        args = getParseArguments("wood,wood;5");
        Assert.assertArrayEquals(new ItemStack[] { wood, woodAmount }, args.getItemStack(new MockPlayer("dummy"), "param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.getParams("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testGetLocation() throws Exception {
        // TODO: requires player to select a location
    }

    @Test
    public void testGetEnum() throws Exception {

        CommandArguments args = getParseArguments("constant");

        // should not throw any exceptions
        Assert.assertEquals(TestEnum.CONSTANT, args.getEnum("param1", TestEnum.class));

        // check for runtime exception, invalid parameter name specified
        try {
            args.getEnum("param2", TestEnum.class);
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

        // ------------

        // check for invalid argument
        args = getParseArguments("invalidConstant");

        try {
            args.getEnum("param1", TestEnum.class);
            throw new AssertionError("InvalidArgumentException expected.");
        }
        catch (InvalidArgumentException ignore) {}
    }

    @Test
    public void testIsDefaultValue() throws Exception {
        CommandArguments args;

        // should not throw any exceptions
        args = getOptionalArguments("value");
        Assert.assertEquals(false, args.isDefaultValue("param1"));

        args = getOptionalArguments(null);
        Assert.assertEquals(true, args.isDefaultValue("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.isDefaultValue("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasBoolean() throws Exception {

        CommandArguments args;

        // should not throw any exceptions
        args = getParseArguments("true");
        Assert.assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("allow");
        Assert.assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("yes");
        Assert.assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("on");
        Assert.assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("false");
        Assert.assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("deny");
        Assert.assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("0");
        Assert.assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("no");
        Assert.assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("off");
        Assert.assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("notABoolean");
        Assert.assertEquals(false, args.hasBoolean("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasBoolean("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }


    @Test
    public void testHasChar() throws Exception {

        CommandArguments args;

        // should not throw any exceptions
        args = getParseArguments("s");
        Assert.assertEquals(true, args.hasChar("param1"));

        args = getParseArguments("notAChar");
        Assert.assertEquals(false, args.hasChar("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasChar("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasByte() throws Exception {

        CommandArguments args;

        // should not throw any exceptions
        args = getParseArguments("10");
        Assert.assertEquals(true, args.hasByte("param1"));

        args = getParseArguments("notAByte");
        Assert.assertEquals(false, args.hasByte("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasByte("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasShort() throws Exception {

        CommandArguments args;

        // should not throw any exceptions
        args = getParseArguments("10");
        Assert.assertEquals(true, args.hasShort("param1"));

        args = getParseArguments("notAShort");
        Assert.assertEquals(false, args.hasShort("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasShort("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasInt() throws Exception {

        CommandArguments args;

        // should not throw any exceptions
        args = getParseArguments("10");
        Assert.assertEquals(true, args.hasInteger("param1"));

        args = getParseArguments("notAnInt");
        Assert.assertEquals(false, args.hasInteger("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasInteger("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasFloat() throws Exception {

        CommandArguments args;

        // should not throw any exceptions
        args = getParseArguments("10.0");
        Assert.assertEquals(true, args.hasFloat("param1"));

        args = getParseArguments("10");
        Assert.assertEquals(true, args.hasFloat("param1"));

        args = getParseArguments("notAFloat");
        Assert.assertEquals(false, args.hasFloat("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasFloat("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

    }

    @Test
    public void testHasDouble() throws Exception {

        CommandArguments args;

        // should not throw any exceptions
        args = getParseArguments("10.0");
        Assert.assertEquals(true, args.hasDouble("param1"));

        args = getParseArguments("10");
        Assert.assertEquals(true, args.hasDouble("param1"));

        args = getParseArguments("notADouble");
        Assert.assertEquals(false, args.hasDouble("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasDouble("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasItemStack() throws Exception {

        CommandArguments args;

        args = getParseArguments("wood");
        Assert.assertEquals(true, args.hasItemStack("param1"));

        args = getParseArguments("wood,wood");
        Assert.assertEquals(true, args.hasItemStack("param1"));

        args = getParseArguments("wood,wood;5");
        Assert.assertEquals(true, args.hasItemStack("param1"));

        args = getParseArguments("notAnItemStack");
        Assert.assertEquals(false, args.hasItemStack("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasItemStack("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasPercent() throws Exception {

        CommandArguments args;

        // should not throw any exceptions
        args = getParseArguments("10.0%");
        Assert.assertEquals(true, args.hasPercent("param1"));

        args = getParseArguments("10%");
        Assert.assertEquals(true, args.hasPercent("param1"));

        args = getParseArguments("10.0");
        Assert.assertEquals(true, args.hasPercent("param1"));

        args = getParseArguments("10");
        Assert.assertEquals(true, args.hasPercent("param1"));

        args = getParseArguments("notAPercent");
        Assert.assertEquals(false, args.hasPercent("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasPercent("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasEnum() throws Exception {

        CommandArguments args;

        // should not throw any exceptions
        args = getParseArguments("constant");
        Assert.assertEquals(true, args.hasEnum("param1", TestEnum.class));

        args = getParseArguments("notAValidConstant");
        Assert.assertEquals(false, args.hasEnum("param1", TestEnum.class));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasEnum("param2", TestEnum.class);
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }
}