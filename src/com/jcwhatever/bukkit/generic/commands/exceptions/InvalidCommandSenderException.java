package com.jcwhatever.bukkit.generic.commands.exceptions;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Thrown if the {@CommandSender} cannot run the command.
 * 
 * @author JC The Pants
 *
 */
public class InvalidCommandSenderException extends Exception {

    private static final long serialVersionUID = 1L;
    private CommandSenderType _senderType;
    private CommandSenderType _expectedType;
    private String _reason;
    
    /**
     * Check the command sender and throw exception if the 
     * sender is not the expected type.
     * 
     * @param sender    The command sender
     * @param expected  The expected sender type
     * 
     * @throws InvalidCommandSenderException
     */
    public static void check(CommandSender sender, CommandSenderType expected)
            throws InvalidCommandSenderException {
        
        check(sender, expected, null);
    } 
    
    /**
     * Check the command sender and throw exception if the
     * sender is not the expected type.
     * 
     * @param sender    The command sender
     * @param expected  The expected sender type
     * @param reason    The reason the command sender type is invalid
     * 
     * @throws InvalidCommandSenderException
     */
    public static void check(CommandSender sender, CommandSenderType expected, String reason) 
            throws InvalidCommandSenderException {
        
        switch (expected) {
            case CONSOLE:
                if (!(sender instanceof Player))
                    return;
                throw new InvalidCommandSenderException(CommandSenderType.PLAYER, CommandSenderType.CONSOLE, reason);
                                                
            case PLAYER:
                if (sender instanceof Player)
                    return;
                throw new InvalidCommandSenderException(CommandSenderType.CONSOLE, CommandSenderType.PLAYER, reason);
        }
    }
    
    /**
     * Defines command sender types
     * @author JC The Pants
     *
     */
    public enum CommandSenderType {
        CONSOLE ("Console"),
        PLAYER  ("Player");
        
        private final String _displayName;
        
        CommandSenderType(String displayName) {
            _displayName = displayName;            
        }
        
        public String getDisplayName() {
            return _displayName;
        }
    }
    
    /**
     * Constructor.
     * 
     * @param commandSenderType  The type of command sender
     * @param expectedType       The expected type of the command sender
     */
    public InvalidCommandSenderException(CommandSenderType commandSenderType, 
                                         CommandSenderType expectedType) {
        this(commandSenderType, expectedType, null);
    }
    
    /**
     * Constructor.
     * 
     * @param commandSenderType  The type of command sender
     * @param expectedType       The expected type of the command sender
     * @param reason             The reason the command sender cannot be used
     */
    public InvalidCommandSenderException(CommandSenderType commandSenderType, 
                                         CommandSenderType expectedType, @Nullable String reason) {
        PreCon.notNull(commandSenderType);
        PreCon.notNull(commandSenderType);
        
        _senderType = commandSenderType;
        _expectedType = expectedType;
        _reason = reason;
    }
    
    /**
     * Get the type of the command sender.
     * @return
     */
    public CommandSenderType getSenderType() {
        return _senderType;
    }
    
    /**
     * Get the expected command sender type
     * @return
     */
    public CommandSenderType getExpectedType() {
        return _expectedType;
    }
     
    /**
     * Get the reason the command sender cannot
     * be accepted.
     * 
     * @return
     */
    @Nullable
    public String getReason() {
        return _reason;
    }

}
