package com.jcwhatever.bukkit.generic.views.triggers;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.ViewManager;

public enum TriggerType {
	NONE (null),
	BLOCK_TYPE (BlockTypeTrigger.class),
	LOCATION (LocationTrigger.class);
	
	private Class<? extends IViewTrigger> _triggerClass;
	
	TriggerType(Class<? extends IViewTrigger> triggerClass) {
		_triggerClass = triggerClass;
	}
	
	public IViewTrigger getNewTrigger(IView view, IDataNode triggerNode, ViewManager viewManager) {
		PreCon.notNull(triggerNode);
		
		if (_triggerClass == null)
			return null;
		
		IViewTrigger trigger;
		
		try {
			trigger = _triggerClass.newInstance();
		}
        catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
				
		trigger.init(view, triggerNode, viewManager);
		
		return trigger;
	}

}
