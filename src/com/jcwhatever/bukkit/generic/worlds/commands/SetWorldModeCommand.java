package com.jcwhatever.bukkit.generic.worlds.commands;

import org.bukkit.command.CommandSender;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.worlds.WorldManager;
import com.jcwhatever.bukkit.generic.worlds.WorldMode;

/**
 * 
 * @ICommandInfo(
		parameters={"mode"},
		usage="/... setWorldMode <blacklist|whitelist>",
		description="Set world blacklisting or whitelisting mode.")
 * 
 * @author JC The Pants
 *
 */

public abstract class SetWorldModeCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {
		
		WorldMode mode = args.getEnum("mode", WorldMode.class);
				
		getWorldManager().setMode(mode);

		tellSuccess(sender, "World mode set to {0}.", mode.name());
	}
	
	protected abstract WorldManager getWorldManager();
	
}
