package com.jcwhatever.bukkit.generic.views.triggers;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.ISettingsManager;
import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.ViewManager;

public interface IViewTrigger {

	void init(IView view, IDataNode triggerNode, ViewManager viewManager);
	
	TriggerType getType();
	
	ISettingsManager getSettingsManager();
	
	void dispose();
	
}
