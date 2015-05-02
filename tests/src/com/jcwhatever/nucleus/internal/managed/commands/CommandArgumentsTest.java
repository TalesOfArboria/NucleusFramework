package com.jcwhatever.nucleus.internal.managed.commands;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.v1_8_R2.BukkitTester;
import com.jcwhatever.v1_8_R2.MockPlayer;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.internal.managed.commands.CommandCollection.ICommandContainerFactory;
import com.jcwhatever.nucleus.internal.managed.commands.DummyRegisteredCommand.CommandInfoBuilder;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.ICommand;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArgument;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackBuilder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;

public class CommandArgumentsTest {

    private Arguments getArguments(CommandInfo info, String... args) throws CommandException {

        CommandDispatcher dispatcher = new CommandDispatcher(
                BukkitTester.mockPlugin("dummy"),
                new ICommandContainerFactory() {
                    @Override
                    public RegisteredCommand create(Plugin plugin, ICommand command) {
                        return new DummyRegisteredCommand(plugin, command, this);
                    }
                });

        dispatcher.registerCommand(DummyCommand.class);

        DummyRegisteredCommand command = (DummyRegisteredCommand) dispatcher.getCommand("dummy");
        assert command != null;

        command.setInfo(info);

        return new Arguments(command, args);
    }

    // Get arguments for a command that expects 1 static parameter "param1"
    private Arguments getParseArguments(String argument) throws CommandException {
        return getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1").build(),
                argument != null ? new String[] { argument } : new String[0]);
    }

    // Get arguments for a command that has 1 optional static parameter "param1"
    private Arguments getOptionalArguments(String argument) throws CommandException {
        return getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1=optional").build(),
                argument != null ? new String[] { argument } : new String[0]);
    }

    // Get arguments for a command that has 1 required static parameter "param1",
    // 1 optional static param "param2" and 1 flag "flag".
    private Arguments getMixedOptionalArguments(String argument1, String argument2, boolean flag)
            throws CommandException {

        return getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1", "param2=optional")
                        .flags("flag").build(),
                ArrayUtils.removeNull(new String[]{argument1, argument2, flag ? "--flag" : null}));
    }

    // Get arguments for a command that has 1 flag argument
    private Arguments getFlagArguments(boolean hasFlag) throws CommandException {
        return getArguments(new CommandInfoBuilder("dummy")
                        .flags("flag").build(),
                hasFlag ? new String[]{"--flag"} : new String[0]);
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

        Arguments args = getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1", "param2").build(),
                "arg1", "arg2");

        Assert.assertArrayEquals(new String[]{"arg1", "arg2"}, args.getRawArguments());
    }

    @Test
    public void testStaticSize() throws Exception {

        Arguments args = getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1", "param2").build(),
                "arg1", "arg2");

        // returns the number of static parameters, not arguments
        assertEquals(2, args.staticSize());


        args = getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1", "param2=optional").build(),
                "arg1");

        // returns the number of static parameters, not arguments
        assertEquals(2, args.staticSize());
    }

    @Test
    public void testFloatingSize() throws Exception {

        Arguments args = getArguments(new CommandInfoBuilder("dummy")
                        .floatingParams("param1", "param2").build(),
                "-param1", "arg1", "-param2", "arg2");

        // returns the number of floating parameters, not arguments
        assertEquals(2, args.floatingSize());


        args = getArguments(new CommandInfoBuilder("dummy")
                        .floatingParams("param1", "param2=optional").build(),
                "-param1", "arg1");

        // returns the number of floating parameters, not arguments
        assertEquals(2, args.floatingSize());
    }

    @Test
    public void testGet() throws Exception {

        Arguments args = getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1")
                        .floatingParams("param2").build(),
                "arg1", "-param2", "arg2");

        assertEquals("arg1", args.get("param1").getValue());
        assertEquals("arg2", args.get("param2").getValue());
    }

    @Test
    public void testIterator() throws Exception {
        Arguments args = getArguments(new CommandInfoBuilder("dummy")
                        .staticParams("param1")
                        .floatingParams("param2").build(),
                "arg1", "-param2", "arg2");

        Iterator<ICommandArgument> iterator = args.iterator();

        assertEquals("arg1", iterator.next().getValue());
        assertEquals("arg2", iterator.next().getValue());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testGetName() throws Exception {

        Arguments args = getParseArguments("validName");

        // should not throw any exceptions
        assertEquals("validName", args.getName("param1"));

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
        Arguments args = getParseArguments("string");

        assertEquals("string", args.getString("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.getString("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testGetBoolean() throws Exception {
        Arguments args;

        // should not throw any exceptions
        args = getParseArguments("true");
        assertEquals(true, args.getBoolean("param1"));

        args = getParseArguments("yes");
        assertEquals(true, args.getBoolean("param1"));

        args = getParseArguments("allow");
        assertEquals(true, args.getBoolean("param1"));

        args = getParseArguments("on");
        assertEquals(true, args.getBoolean("param1"));

        args = getParseArguments("1");
        assertEquals(true, args.getBoolean("param1"));


        args = getParseArguments("false");
        assertEquals(false, args.getBoolean("param1"));

        args = getParseArguments("no");
        assertEquals(false, args.getBoolean("param1"));

        args = getParseArguments("deny");
        assertEquals(false, args.getBoolean("param1"));

        args = getParseArguments("off");
        assertEquals(false, args.getBoolean("param1"));

        args = getParseArguments("0");
        assertEquals(false, args.getBoolean("param1"));

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

        // test flags
        args = getFlagArguments(true);
        assertEquals(true, args.getBoolean("flag"));

        args = getFlagArguments(false);
        assertEquals(false, args.getBoolean("flag"));
    }

    @Test
    public void testGetChar() throws Exception {

        Arguments args = getParseArguments("c");

        // should not throw any exceptions
        assertEquals('c', args.getChar("param1"));

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

        Arguments args = getParseArguments("10");

        // should not throw any exceptions
        assertEquals((byte) 10, args.getByte("param1"));

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

        Arguments args = getParseArguments("10");

        // should not throw any exceptions
        assertEquals(10, args.getShort("param1"));

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

        Arguments args = getParseArguments("10");

        // should not throw any exceptions
        assertEquals(10, args.getInteger("param1"));

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

        Arguments args = getParseArguments("10");

        // should not throw any exceptions
        assertEquals(10, args.getLong("param1"));

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

        Arguments args = getParseArguments("10.0");

        // should not throw any exceptions
        assertEquals(10.0D, args.getFloat("param1"), 0.0D);

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
        Arguments args = getParseArguments("10.0");

        // should not throw any exceptions
        assertEquals(10.0D, args.getFloat("param1"), 0.0D);

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
        Arguments args = getParseArguments("10.0%");

        // should not throw any exceptions
        assertEquals(10.0D, args.getPercent("param1"), 0.0D);

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
        Arguments args = getParseArguments("1 2 3");

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

        Arguments args;

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

        Arguments args = getParseArguments("constant");

        // should not throw any exceptions
        assertEquals(TestEnum.CONSTANT, args.getEnum("param1", TestEnum.class));

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
        Arguments args;

        // should not throw any exceptions
        args = getOptionalArguments("value");
        assertEquals(false, args.isDefaultValue("param1"));

        args = getOptionalArguments(null);
        assertEquals(true, args.isDefaultValue("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.isDefaultValue("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasBoolean() throws Exception {

        Arguments args;

        // should not throw any exceptions
        args = getParseArguments("true");
        assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("allow");
        assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("yes");
        assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("on");
        assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("false");
        assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("deny");
        assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("0");
        assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("no");
        assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("off");
        assertEquals(true, args.hasBoolean("param1"));

        args = getParseArguments("notABoolean");
        assertEquals(false, args.hasBoolean("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasBoolean("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }


    @Test
    public void testHasChar() throws Exception {

        Arguments args;

        // should not throw any exceptions
        args = getParseArguments("s");
        assertEquals(true, args.hasChar("param1"));

        args = getParseArguments("notAChar");
        assertEquals(false, args.hasChar("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasChar("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasByte() throws Exception {

        Arguments args;

        // should not throw any exceptions
        args = getParseArguments("10");
        assertEquals(true, args.hasByte("param1"));

        args = getParseArguments("notAByte");
        assertEquals(false, args.hasByte("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasByte("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasShort() throws Exception {

        Arguments args;

        // should not throw any exceptions
        args = getParseArguments("10");
        assertEquals(true, args.hasShort("param1"));

        args = getParseArguments("notAShort");
        assertEquals(false, args.hasShort("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasShort("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasInt() throws Exception {

        Arguments args;

        // should not throw any exceptions
        args = getParseArguments("10");
        assertEquals(true, args.hasInteger("param1"));

        args = getParseArguments("notAnInt");
        assertEquals(false, args.hasInteger("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasInteger("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasFloat() throws Exception {

        Arguments args;

        // should not throw any exceptions
        args = getParseArguments("10.0");
        assertEquals(true, args.hasFloat("param1"));

        args = getParseArguments("10");
        assertEquals(true, args.hasFloat("param1"));

        args = getParseArguments("notAFloat");
        assertEquals(false, args.hasFloat("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasFloat("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

    }

    @Test
    public void testHasDouble() throws Exception {

        Arguments args;

        // should not throw any exceptions
        args = getParseArguments("10.0");
        assertEquals(true, args.hasDouble("param1"));

        args = getParseArguments("10");
        assertEquals(true, args.hasDouble("param1"));

        args = getParseArguments("notADouble");
        assertEquals(false, args.hasDouble("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasDouble("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasItemStack() throws Exception {

        Arguments args;

        args = getParseArguments("wood");
        assertEquals(true, args.hasItemStack("param1"));

        args = getParseArguments("wood,wood");
        assertEquals(true, args.hasItemStack("param1"));

        args = getParseArguments("wood,wood;5");
        assertEquals(true, args.hasItemStack("param1"));

        args = getParseArguments("notAnItemStack");
        assertEquals(false, args.hasItemStack("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasItemStack("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasPercent() throws Exception {

        Arguments args;

        // should not throw any exceptions
        args = getParseArguments("10.0%");
        assertEquals(true, args.hasPercent("param1"));

        args = getParseArguments("10%");
        assertEquals(true, args.hasPercent("param1"));

        args = getParseArguments("10.0");
        assertEquals(true, args.hasPercent("param1"));

        args = getParseArguments("10");
        assertEquals(true, args.hasPercent("param1"));

        args = getParseArguments("notAPercent");
        assertEquals(false, args.hasPercent("param1"));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasPercent("param2");
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testHasEnum() throws Exception {

        Arguments args;

        // should not throw any exceptions
        args = getParseArguments("constant");
        assertEquals(true, args.hasEnum("param1", TestEnum.class));

        args = getParseArguments("notAValidConstant");
        assertEquals(false, args.hasEnum("param1", TestEnum.class));

        // check for runtime exception, invalid parameter name specified
        try {
            args.hasEnum("param2", TestEnum.class);
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

    @Test
    public void testMixedArgTypes() throws Exception {

        Arguments args;

        args = getMixedOptionalArguments("arg1", "arg2", true);
        assertEquals("arg1", args.getString("param1"));
        assertEquals("arg2", args.getString("param2"));
        assertEquals(true, args.getBoolean("flag"));

        args = getMixedOptionalArguments("arg1", "arg2", false);
        assertEquals("arg1", args.getString("param1"));
        assertEquals("arg2", args.getString("param2"));
        assertEquals(false, args.getBoolean("flag"));

        args = getMixedOptionalArguments("arg1", null, false);
        assertEquals("arg1", args.getString("param1"));
        assertEquals("optional", args.getString("param2"));
        assertEquals(false, args.getBoolean("flag"));

        args = getMixedOptionalArguments("arg1", null, true);
        assertEquals("arg1", args.getString("param1"));
        assertEquals("optional", args.getString("param2"));
        assertEquals(true, args.getBoolean("flag"));
    }
}