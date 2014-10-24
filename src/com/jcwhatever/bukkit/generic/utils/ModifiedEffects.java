package com.jcwhatever.bukkit.generic.utils;

import com.jcwhatever.bukkit.generic.GenericsLib;
import org.bukkit.Location;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class ModifiedEffects {


	private static Object _sync = new Object();
	private static boolean _suppressExplosionSound = true;

	static {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(GenericsLib.getInstance(), PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				String soundName = event.getPacket().getStrings().read(0);

				if (_suppressExplosionSound && "random.explode".equals(soundName)) {
					event.setCancelled(true);
				}

			}
		});
		
	}

	public static void createExplosion(Location location, float magnitude, boolean setFire, boolean playSound) {
		synchronized(_sync) {
			_suppressExplosionSound=!playSound;
			location.getWorld().createExplosion(location, magnitude, setFire);
			_suppressExplosionSound=false;
		}

	}
	
}
