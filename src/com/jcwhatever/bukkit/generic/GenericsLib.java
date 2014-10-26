package com.jcwhatever.bukkit.generic;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.jcwhatever.bukkit.generic.internal.commands.CommandHandler;
import com.jcwhatever.bukkit.generic.internal.events.JCGEventListener;
import com.jcwhatever.bukkit.generic.jail.JailManager;
import com.jcwhatever.bukkit.generic.player.PlayerBlockView;
import com.jcwhatever.bukkit.generic.regions.RegionManager;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;

/**
 * GenericsLib Bukkit plugin.
 */
public class GenericsLib extends GenericsPlugin {

    private static GenericsLib _instance;
    private static RegionManager _regionManager;

	private ProtocolManager _protocolManager;
    private JailManager _jailManager;

    /**
     * Get the {@code GenericsLib} plugin instance.
     * @return
     */
	public static GenericsLib getInstance() {
		return _instance;
	}

    /**
     * Get the global {@code RegionManager}.
     */
    public static RegionManager getRegionManager() {
        return _regionManager;
    }

    /**
     * Constructor.
     */
	public GenericsLib() {
		super();

		_instance = this;
	}

    /**
     * Get the default Jail Manager.
     */
    public JailManager getJailManager() {
        return _jailManager;
    }

    /**
     * Get Protocol Manager.
     */
    public ProtocolManager getProtocolManager() {
        return _protocolManager;
    }


    /**
     * Get the chat prefix.
     */
    @Override
	public String getChatPrefix() {
		return "[GenericsLib] ";
	}

    /**
     * Get the console prefix.
     */
	@Override
	public String getConsolePrefix() {
		return getChatPrefix();
	}

    @Override
    protected void onEnablePlugin() {
        _protocolManager = ProtocolLibrary.getProtocolManager();
        _regionManager = new RegionManager();
        _jailManager = new JailManager(this, "default", getSettings().getNode("jail"));

        // remove world guard message
        _protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                WrappedChatComponent chat = event.getPacket().getChatComponents().read(0);

                if (chat.getJson().contains("You don't have permission for this area.")) {
                    event.setCancelled(true);
                }
            }
        });


        // handle block change packets
        _protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.BLOCK_CHANGE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                World world = event.getPlayer().getWorld();
                StructureModifier<Integer> ints = packet.getIntegers();
                int x = ints.read(0);
                int y = ints.read(1);
                int z = ints.read(2);

                if (PlayerBlockView.isAlternateViewed(event.getPlayer(), world, x, y, z))
                    event.setCancelled(true);

            }
        });

        registerListeners();

        registerCommands(new CommandHandler());
    }

    @Override
    protected void onDisablePlugin() {

    }

	private void registerListeners() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new JCGEventListener(), this);
	}


}
