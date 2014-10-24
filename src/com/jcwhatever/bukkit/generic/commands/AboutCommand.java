package com.jcwhatever.bukkit.generic.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.utils.TextUtils;

@ICommandInfo(
        command={"about"},
        usage="/{command} about",
        description="Get information about the plugin.",
        permissionDefault=PermissionDefault.TRUE,
        isHelpVisible=false)
public class AboutCommand extends AbstractCommand {

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {
        
        Messenger.tell(_plugin, sender, "----------------------------------------");
        
        String text = TextUtils.formatPluginInfo(_plugin, "{BOLD}{GREEN}{plugin-name} v{plugin-version}");
        Messenger.tell(_plugin, sender, text);
        
        String author = TextUtils.formatPluginInfo(_plugin, "Plugin by {plugin-author}");
        Messenger.tell(_plugin, sender, author);
        
        String list = TextUtils.formatPluginInfo(_plugin, "{AQUA}For a list of commands, type '/{plugin-command} help'\r");
        Messenger.tell(_plugin, sender, list);
        
        Messenger.tell(_plugin, sender, "----------------------------------------");
    }
}
