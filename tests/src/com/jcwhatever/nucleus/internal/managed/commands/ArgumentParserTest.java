package com.jcwhatever.nucleus.internal.managed.commands;

import com.jcwhatever.v1_8_R3.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.internal.managed.commands.CommandCollection.ICommandContainerFactory;
import com.jcwhatever.nucleus.internal.managed.commands.DummyRegisteredCommand.CommandInfoBuilder;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.ICommand;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidParameterException;
import com.jcwhatever.nucleus.managed.commands.exceptions.MissingArgumentException;
import com.jcwhatever.nucleus.managed.commands.exceptions.TooManyArgsException;

import org.bukkit.plugin.Plugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ArgumentParserTest {

    ArgumentParser parser = new ArgumentParser();

    private DummyRegisteredCommand getCommand() {

        CommandDispatcher dispatcher = new CommandDispatcher(
                BukkitTester.mockPlugin("dummy"),
                new ICommandContainerFactory() {
                    @Override
                    public RegisteredCommand create(Plugin plugin, ICommand command) {
                        return new DummyRegisteredCommand(plugin, command, this);
                    }
                });

        dispatcher.registerCommand(DummyCommand.class);

        return (DummyRegisteredCommand)dispatcher.getCommand("dummy");
    }

    @BeforeClass
    public static void testStartup() {

        NucleusTest.init();
    }


    /**
     * Test parsing; No Arguments expected.
     */
    @Test
    public void testParse() throws Exception {

        DummyRegisteredCommand command = getCommand();

        ArgumentParseResults results;

        results = parser.parse(command, new String[0]);

        Assert.assertEquals(0, results.getArgMap().size());


        // make sure providing too many args causes an exception
        try {
            parser.parse(command, new String[]{"staticParam"});
            throw new AssertionError("TooManyArgsException was expected to be thrown.");
        } catch (TooManyArgsException ignore) {}
    }

    /**
     * Test parsing; 1 static argument expected.
     */
    @Test
    public void testParse1() throws Exception {

        DummyRegisteredCommand command = getCommand();

        CommandInfo info = new CommandInfoBuilder("dummy").staticParams("name").build();
        command.setInfo(info);

        ArgumentParseResults results;

        results = parser.parse(command, new String[]{"nameArg"});

        Assert.assertEquals(1, results.getArgMap().size());

        // check for missing argument, no arguments provided where expected
        try {
            parser.parse(command, new String[]{});
            throw new AssertionError("MissingArgumentException was expected to be thrown.");
        }
        catch (MissingArgumentException ignore) {}

        // check for too many arguments
        try {
            parser.parse(command, new String[]{ "arg1", "arg2" });
            throw new AssertionError("TooManyArgsException was expected to be thrown.");
        }
        catch (TooManyArgsException ignore) {}
    }

    /**
     * Test parsing; 1 required floating argument expected.
     */
    @Test
    public void testParse2() throws Exception {

        DummyRegisteredCommand command = getCommand();

        CommandInfo info = new CommandInfoBuilder("dummy").floatingParams("param").build();
        command.setInfo(info);

        ArgumentParseResults results;

        results = parser.parse(command, new String[]{"-param", "\"arg", "with", "spaces\""});

        Assert.assertEquals(1, results.getArgMap().size());


        // check missing arg, floating parameter not included
        try {
            parser.parse(command, new String[0]);
            throw new AssertionError("MissingArgumentException was expected to be thrown.");
        } catch (MissingArgumentException ignore) {
        }


        // check missing arg, floating parameter missing its argument
        try {
            parser.parse(command, new String[]{"-param"});
            throw new AssertionError("MissingArgumentException was expected to be thrown.");
        } catch (MissingArgumentException ignore) {
        }

        // check invalid argument, invalid static argument
        try {
            parser.parse(command, new String[]{"staticArg", "-param", "test"});
            throw new AssertionError("InvalidArgumentException was expected to be thrown.");
        } catch (InvalidArgumentException ignore) {
        }
    }

    /**
     * Test parsing; 1 optional flag
     */
    @Test
    public void testParse3() throws Exception {

        DummyRegisteredCommand command = getCommand();

        CommandInfo info = new CommandInfoBuilder("dummy").flags("flag1", "flag2").build();
        command.setInfo(info);

        ArgumentParseResults results;

        results = parser.parse(command, new String[]{"--flag1", "--flag2"});

        Assert.assertEquals(0, results.getArgMap().size()); // flags are not stored in arg map
        Assert.assertEquals(true, results.getFlag("flag1"));
        Assert.assertEquals(true, results.getFlag("flag2"));


        results = parser.parse(command, new String[]{"--flag1"});

        Assert.assertEquals(0, results.getArgMap().size()); // flags are not stored in arg map
        Assert.assertEquals(true, results.getFlag("flag1"));
        Assert.assertEquals(false, results.getFlag("flag2"));


        results = parser.parse(command, new String[]{"--flag2"});

        Assert.assertEquals(0, results.getArgMap().size()); // flags are not stored in arg map
        Assert.assertEquals(false, results.getFlag("flag1"));
        Assert.assertEquals(true, results.getFlag("flag2"));


        results = parser.parse(command, new String[0]);

        Assert.assertEquals(0, results.getArgMap().size()); // flags are not stored in arg map
        Assert.assertEquals(false, results.getFlag("flag1"));
        Assert.assertEquals(false, results.getFlag("flag2"));


        // check for invalid argument, invalid static argument
        try {
            parser.parse(command, new String[]{"staticArg"});
            throw new AssertionError("InvalidArgumentException was expected to be thrown.");
        } catch (InvalidArgumentException ignore) {
        }

        // check for invalid parameter, invalid flag name
        try {
            parser.parse(command, new String[]{"--flag3"});
            throw new AssertionError("InvalidParameterException was expected to be thrown.");
        } catch (InvalidParameterException ignore) {
        }

        // check for invalid parameter, undefined floating parameter
        try {
            parser.parse(command, new String[]{"-floating", "arg"});
            throw new AssertionError("InvalidParameterException was expected to be thrown.");
        } catch (InvalidParameterException ignore) {
        }

        // check for invalid parameter, invalid flag name
        try {
            parser.parse(command, new String[]{"--flag1", "--flag3"});
            throw new AssertionError("InvalidParameterException was expected to be thrown.");
        } catch (InvalidParameterException ignore) {
        }
    }

    /**
     * Test parsing; 1 static param required, 1 required floating argument.
     */
    @Test
    public void testParse4() throws Exception {

        DummyRegisteredCommand command = getCommand();

        CommandInfo info = new CommandInfoBuilder("dummy")
                .staticParams("static")
                .floatingParams("param").build();
        command.setInfo(info);

        ArgumentParseResults results;

        results = parser.parse(command, new String[]{"\"static", "arg\"", "-param", "\"arg", "with", "spaces\""});

        Assert.assertEquals(2, results.getArgMap().size());
        Assert.assertEquals("static arg", results.getArgMap().get("static").getValue());
        Assert.assertEquals("arg with spaces", results.getArgMap().get("param").getValue());

        // check for missing argument, required floating arg missing
        try {
            parser.parse(command, new String[]{"\"static", "arg\""});
            throw new AssertionError("MissingArgumentException expected to be thrown.");
        } catch (MissingArgumentException ignore) {
        }

        // check for missing argument, required static arg missing
        try {
            parser.parse(command, new String[]{"-param", "\"arg", "with", "spaces\""});
            throw new AssertionError("MissingArgumentException expected to be thrown.");
        } catch (MissingArgumentException ignore) {
        }

        // check for invalid parameter, param marked as flag instead of floating param
        try {
            parser.parse(command, new String[]{"\"static", "arg\"", "--param", "\"arg", "with", "spaces\""});
            throw new AssertionError("InvalidParameterException expected to be thrown.");
        } catch (InvalidParameterException ignore) {
        }

        // check for missing argument, static param out of order
        try {
            parser.parse(command, new String[]{"-param", "\"arg", "with", "spaces\"", "\"static", "arg\""});
            throw new AssertionError("MissingArgumentException expected to be thrown.");
        } catch (MissingArgumentException ignore) {
        }
    }

    /**
     * Test parsing; 1 static param required, 1 optional floating argument.
     */
    @Test
    public void testParse5() throws Exception {

        DummyRegisteredCommand command = getCommand();

        CommandInfo info = new CommandInfoBuilder("dummy")
                .staticParams("static")
                .floatingParams("param=test").build();
        command.setInfo(info);

        ArgumentParseResults results;

        results = parser.parse(command, new String[]{"\"static", "arg\"", "-param", "\"arg", "with", "spaces\""});

        Assert.assertEquals(2, results.getArgMap().size());
        Assert.assertEquals("static arg", results.getArgMap().get("static").getValue());
        Assert.assertEquals("arg with spaces", results.getArgMap().get("param").getValue());

        results = parser.parse(command, new String[]{"\"static", "arg\"" });

        Assert.assertEquals(2, results.getArgMap().size());
        Assert.assertEquals("static arg", results.getArgMap().get("static").getValue());
        Assert.assertEquals("test", results.getArgMap().get("param").getValue());
    }


    /**
     * Test parsing; 1 optional static param, 1 optional floating argument.
     */
    @Test
    public void testParse6() throws Exception {

        DummyRegisteredCommand command = getCommand();

        CommandInfo info = new CommandInfoBuilder("dummy")
                .staticParams("static=test")
                .floatingParams("param=test").build();
        command.setInfo(info);

        ArgumentParseResults results;

        results = parser.parse(command, new String[]{"\"static", "arg\"", "-param", "\"arg", "with", "spaces\""});

        Assert.assertEquals(2, results.getArgMap().size());
        Assert.assertEquals("static arg", results.getArgMap().get("static").getValue());
        Assert.assertEquals("arg with spaces", results.getArgMap().get("param").getValue());

        results = parser.parse(command, new String[]{ });

        Assert.assertEquals(2, results.getArgMap().size());
        Assert.assertEquals("test", results.getArgMap().get("static").getValue());
        Assert.assertEquals("test", results.getArgMap().get("param").getValue());
    }

}