package com.jcwhatever.bukkit.generic.jail;

import com.jcwhatever.bukkit.generic.regions.Region;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.jcwhatever.bukkit.generic.storage.IDataNode;

/**
 * A region that represents the boundaries of a jail.
 */
public class JailBounds extends Region {

    private JailManager _jailManager;

    /**
     * Constructor.
     *
     * @param plugin       The owning plugin.
     * @param jailManager  The owning jail manager.
     * @param name         The name of the region.
     * @param settings     The region data node.
     */
    JailBounds(Plugin plugin, JailManager jailManager, String name, IDataNode settings) {
        super(plugin, name, settings);

        PreCon.notNull(jailManager);

        _jailManager = jailManager;
    }

    /**
     * Determine if the {@code onPlayerLeave} method can be called.
     *
     * @param p  The player leaving the region.
     */
    @Override
    protected boolean canDoPlayerLeave(Player p) {
        PreCon.notNull(p);

        return _jailManager.isPrisoner(p);
    }

    /**
     * Prevent imprisoned players from leaving the jail region.
     * @param p  The player leaving the region.
     */
    @Override
    protected void onPlayerLeave (Player p) {
        PreCon.notNull(p);

        // prevent player from leaving jail
        Location tpLocation = _jailManager.getRandomTeleport();
        
        if (tpLocation == null)
            tpLocation = this.getCenter();
        
        p.teleport(tpLocation);
    }
    
    

}
