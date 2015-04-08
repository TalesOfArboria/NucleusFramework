package com.jcwhatever.nucleus.internal.managed.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;

import org.bukkit.command.CommandSender;

@CommandInfo(
        command="dummy",
        description="Dummy command")
public class DummyCommand implements IExecutableCommand {
    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

    }
}
