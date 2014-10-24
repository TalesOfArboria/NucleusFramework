package com.jcwhatever.bukkit.generic.commands.response;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Data object holding information about a
 * player response request.
 */
public class ResponseRequest {

    private CommandSender _sender;
    private Plugin _plugin;
    private String _context;
    private ResponseHandler _responseHandler;
    private Set<ResponseType> _responseTypes = new HashSet<>(ResponseType.totalTypes());

    /**
     * Constructor.
     *
     * @param plugin        The owning plugin.
     * @param context       A context name the sender can use if more than one request is made.
     * @param sender        The command sender to make a request at.
     * @param handler       The {@code ResponseHandler} that handles the players response.
     * @param responseType  The requested responses.
     */
    public ResponseRequest(Plugin plugin, String context, CommandSender sender, ResponseHandler handler, ResponseType... responseType) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(context);
        PreCon.notNull(sender);
        PreCon.notNull(handler);
        PreCon.notNull(responseType);
        PreCon.greaterThanZero(responseType.length);

        _plugin = plugin;
        _context = context;
        _sender = sender;
        _responseHandler = handler;
        Collections.addAll(_responseTypes, responseType);
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the request context name.
     */
    public String getContext() {
       return _context;
    }

    /**
     * Get the command sender..
     */
    public CommandSender getCommandSender() {
        return _sender;
    }

    /**
     * Get the response handler.
     */
    public ResponseHandler getHandler() {
        return _responseHandler;
    }

    /**
     * Get the requested response types.
     */
    public Set<ResponseType> getResponseTypes() {
        return new HashSet<>(_responseTypes);
    }


    @Override
    public  int hashCode() {
        return _context.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResponseRequest && ((ResponseRequest) obj)._context.equals(_context);
    }


}
