package com.jcwhatever.bukkit.generic.signs;

import com.jcwhatever.bukkit.generic.events.bukkit.SignInteractEvent;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.util.List;

public class BukkitSignEventListener implements Listener {

    @EventHandler(priority=EventPriority.NORMAL)
    private void onSignChange(SignChangeEvent event) {

        BlockState state = event.getBlock().getState();
        Sign sign = (Sign)state;

        List<SignManager> managers = SignManager.getManagers();

        for (SignManager manager : managers) {
            if (manager.signChange(sign, event))
                break;
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    private void onBlockBreak(BlockBreakEvent event) {

        // Signs
        Material material = event.getBlock().getType();
        if (material != Material.SIGN_POST && material != Material.WALL_SIGN)
            return;

        if (event.getBlock().getState() instanceof Sign) {

            BlockState state = event.getBlock().getState();
            Sign sign = (Sign)state;

            List<SignManager> managers = SignManager.getManagers();

            for (SignManager manager : managers) {
                if (manager.signBreak(sign, event))
                    break;
            }
        }

    }

    @EventHandler(priority=EventPriority.NORMAL)
    private void onSignInteract(SignInteractEvent event) {

        List<SignManager> managers = SignManager.getManagers();

        for (SignManager manager : managers) {
            if (manager.signClick(event))
                break;
        }

    }
}
