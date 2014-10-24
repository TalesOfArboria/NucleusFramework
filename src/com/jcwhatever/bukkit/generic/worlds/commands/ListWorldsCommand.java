package com.jcwhatever.bukkit.generic.worlds.commands;

import java.util.List;

import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import org.bukkit.command.CommandSender;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.generic.worlds.WorldManager;

/**
 * 
 * @ICommandInfo(
		parameters={ "page=1" },
		usage="/... listWorlds [page]", 
		description="Get info about world settings.")
 * 
 * @author JC The Pants
 *
 */
public abstract class ListWorldsCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {
		
		int page = args.getInt("page");
		
		WorldManager worldManager = getWorldManager();
		
		String paginTitle = Lang.get("World Settings");
		ChatPaginator pagin = getPaginator(paginTitle);
		
		String modeLabel = Lang.get("Mode");
		pagin.addFormatted(FormatTemplate.DEFINITION, modeLabel, worldManager.getMode().name());
		
		List<String> worldNames = worldManager.getWorlds();
		
		for (String worldName : worldNames) {
			pagin.add(worldName);
		}
		
		pagin.show(sender, page, FormatTemplate.ITEM);
	}
	
	public abstract ChatPaginator getPaginator(String title);
	public abstract WorldManager getWorldManager();
	
}