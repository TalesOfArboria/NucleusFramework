package com.jcwhatever.nucleus.internal.managed.commands;

import com.jcwhatever.bukkit.v1_8_R2.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.internal.managed.commands.CommandCollection.ICommandContainerFactory;
import com.jcwhatever.nucleus.managed.commands.ICommand;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.plugin.Plugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UsageGeneratorTest {

    @BeforeClass
    public static void testStartup() {

        NucleusTest.init();
    }

    @Test
    public void testGenerate() throws Exception {

        CommandDispatcher dispatcher = new CommandDispatcher(
                BukkitTester.mockPlugin("dummy"),
                new ICommandContainerFactory() {
                    @Override
                    public RegisteredCommand create(Plugin plugin, ICommand command) {
                        return new DummyRegisteredCommand(plugin, command, this);
                    }
                }
        );

        dispatcher.registerCommand(DummyCommand.class);

        DummyRegisteredCommand command = (DummyRegisteredCommand)dispatcher.getCommand("dummy");
        assert command != null;

        command.setExecutable(null);

        UsageGenerator generator = new UsageGenerator();

        String usage = generator.generate(command, "root");

        Assert.assertEquals(
                TextUtils.format(UsageGenerator.HELP_USAGE, "root ", "", command.getInfo().getName() + ' ', ""),
                usage);
    }
}