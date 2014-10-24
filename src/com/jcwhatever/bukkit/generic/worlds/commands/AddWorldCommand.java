package com.jcwhatever.bukkit.generic.worlds.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.worlds.WorldManager;

/**
 * @ICommandInfo(
		parameters={"worldName=$current"},
		usage="/... addWorld [worldName]",
		description="Add the current world or specified world to the list of valid/invalid worlds.")
 
 * @author JC The Pants
 *
 */

public abstract class AddWorldCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException, InvalidCommandSenderException {
		
		World world = null;
		String worldName = args.getString("worldName");
		if (worldName.equals("$current")) {
		    
		    InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER, "Console has no location.");
			
			Player p = (Player)sender;
			
			world = p.getWorld();
			
			if (world == null)
				return; // finish
		}
		else {
			world = Bukkit.getWorld(worldName);
			
			if (world == null) {
				tellError(sender, "A world with the name '{0}' was not found.", worldName);
				return; // finish
			}
			
		}
		
		if (!getWorldManager().addWorld(world)) {
			tellError(sender, "Failed to add world '{0}'.", world.getName());
			return; // finish
		}
								
		tellSuccess(sender, "World '{0}' added.", world.getName());
	}
	
	protected abstract WorldManager getWorldManager();
	
}
