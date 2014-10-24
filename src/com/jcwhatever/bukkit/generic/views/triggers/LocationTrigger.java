package com.jcwhatever.bukkit.generic.views.triggers;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.ViewManager;

public class LocationTrigger extends AbstractViewTrigger {

	@Override
	public TriggerType getType() {
		return null;
	}

	@Override
	public void dispose() {
		// do nothing
		
	}

	@Override
	protected void onInit(IView view,IDataNode triggerNode, ViewManager viewManager) {
		// do nothing
		
	}

	@Override
	protected void onLoadSettings(IDataNode dataNode) {
		// do nothing
		
	}

	@Override
	protected SettingDefinitions getPossibleSettings() {
		return null;
	}

	
}
