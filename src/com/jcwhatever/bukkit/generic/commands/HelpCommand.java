package com.jcwhatever.bukkit.generic.commands;

import java.util.ArrayList;
import java.util.List;

import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.permissions.Permissions;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator.PaginatorTemplate;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;

@ICommandInfo(
		command={"help", "?"},
		staticParams={"page=1"},
		usage="/{command} help [page]",
		description="Show commands.",
		permissionDefault=PermissionDefault.TRUE,
		isHelpVisible=false)

public class HelpCommand extends AbstractCommand {
	
	@Override
	public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

		int page = args.getInt("page");
		
		showHelp(sender, page);	
	}

	@Override
	public void showHelp(final CommandSender sender, int page) {
		
	    String paginTitle = Lang.get("Commands");
		final ChatPaginator pagin = new ChatPaginator(_plugin, 6, PaginatorTemplate.HEADER, PaginatorTemplate.FOOTER, paginTitle);

		final List<AbstractCommand> categories = new ArrayList<AbstractCommand>(_commandHandler.getCommands().size());
		
		Permissions.runBatchOperation(true, new Runnable() {

            @Override
            public void run () {

                for (AbstractCommand cmd : _commandHandler.getCommands()) {
                    
                    if (cmd.getSubCommands().size() > 0) {
                        categories.add(cmd);
                        continue;
                    }
                    
                    CommandInfoContainer info = cmd.getInfo();
                    
                    if (!info.isHelpVisible())
                        continue;
                    
                    if (!Permissions.has(sender, cmd.getPermission().getName())) 
                        continue;
                        
                    pagin.add(info.getUsage(), info.getDescription());
                }
                
                for (AbstractCommand cmd : categories) {
                    
                    CommandInfoContainer info = cmd.getInfo();
                    
                    if (!info.isHelpVisible())
                        continue;
                    
                    if (!Permissions.has(sender, cmd.getPermission().getName())) 
                        continue;
                    
                    // format colors and command name
                    String helpText = TextUtils.format("{GOLD}/{plugin-command} {GREEN}{0} {GOLD}?", info.getCommandName());
                    
                    // format plugin info
                    helpText = TextUtils.formatPluginInfo(_plugin, helpText);
                    
                    pagin.add(helpText, info.getDescription());
                }
                
            }
		    
		});
		
		pagin.show(sender, page, FormatTemplate.DEFINITION);
	}

	

	
}

