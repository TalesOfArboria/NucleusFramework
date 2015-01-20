package com.jcwhatever.nucleus.commands;

import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.utils.text.TextUtils;

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

        DummyDispatcher dispatcher = new DummyDispatcher();

        DummyCommand command = (DummyCommand)dispatcher.getCommand("dummy");
        assert command != null;

        command.setExecutable(null);

        UsageGenerator generator = new UsageGenerator();

        String usage = generator.generate(command, "root");

        Assert.assertEquals(
                TextUtils.format(UsageGenerator.HELP_USAGE, "root ", "", command.getInfo().getName() + ' ', ""),
                usage);
    }
}