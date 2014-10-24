package com.jcwhatever.bukkit.generic.storage.settings;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.utils.TextUtils;

public class ValidationResults {
	
	private boolean _isValid;
	private String _message;
	private Object[] _messageParams;
	private String _formattedMessage;
	
	public static final ValidationResults TRUE = new ValidationResults(true);
	public static final ValidationResults FALSE = new ValidationResults(false);
	
	public ValidationResults(boolean isValid) {
		_isValid = isValid;
	}

	public ValidationResults(boolean isValid, String message, Object...messageParams) {
		_isValid = isValid;
	}
	
	public boolean isValid() {
		return _isValid;
	}
	
	public boolean hasMessage() {
		return _message != null;
	}
	
	public String getMessage() {
		if (!hasMessage())
			return null;
		
		if (_formattedMessage == null) {
			_formattedMessage = TextUtils.format(_message, _messageParams);
		}
		return _formattedMessage;
	}

	public boolean tellMessage(Plugin plugin, CommandSender sender) {
		if (!hasMessage())
			return false;
		
		return Messenger.tell(plugin, sender, _message, _messageParams);
	}

}
