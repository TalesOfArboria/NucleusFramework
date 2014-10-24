package com.jcwhatever.bukkit.generic.views.triggers;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.ISettingsManager;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.SettingsManager;
import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.ViewManager;

public abstract class AbstractViewTrigger implements IViewTrigger {

	protected IView _view;
	protected IDataNode _triggerNode;
	protected ViewManager _viewManager;
	private SettingsManager _settingsManager;
	
	@Override
	public final void init(IView view, IDataNode triggerNode, ViewManager viewManager) {
		_view = view;
		_triggerNode = triggerNode;
		_viewManager = viewManager;
		
		_settingsManager = new SettingsManager(triggerNode, getPossibleSettings());
		_settingsManager.addOnSettingsChanged(new Runnable() {

			@Override
			public void run() {
				onLoadSettings(_triggerNode);
			}
			
		}, true);
		
		onInit(view, triggerNode, viewManager);
	}

	@Override
	public final ISettingsManager getSettingsManager() {
		return _settingsManager;
	}

	@Override
	public abstract void dispose();
	
	protected abstract void onInit(IView view, IDataNode triggerNode, ViewManager viewManager);
	protected abstract void onLoadSettings(IDataNode dataNode);
	protected abstract SettingDefinitions getPossibleSettings();

}
