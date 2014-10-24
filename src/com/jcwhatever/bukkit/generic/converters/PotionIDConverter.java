package com.jcwhatever.bukkit.generic.converters;

import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
/**
 * Converts between a Potion and potion id.
 */
public class PotionIDConverter extends ValueConverter<Potion, Short> {
	
	PotionIDConverter() {}

    /**
     * Convert a number value representing the potion meta id into a new Potion instance.
     */
	@Override
	protected Potion onConvert(Object value) {
		if (value instanceof String) {
			try {
				value = Short.parseShort((String)value);
			}
			catch (NumberFormatException nfe) {
				return null;
			}
		} 
		else if (value instanceof Byte) {
			Byte b = (Byte)value;
            value = b.shortValue();
		}
		else if (value instanceof Integer) {
			Integer i = (Integer)value;
            value = i.shortValue();
		}

		if (value instanceof Short) {
			short potionId = (Short)value;
			Potion potion;
			
			switch (potionId) {
			
			default:
				try {
					return Potion.fromDamage(potionId);
				}
				catch (IllegalArgumentException iae) {
					return new Potion(PotionType.WATER);
				}
				
			case 8193:
				return new Potion(PotionType.REGEN);
				
			case 8194:
				return new Potion(PotionType.SPEED);
				
			case 8195:
				return new Potion(PotionType.FIRE_RESISTANCE);
				
			case 8196:
				return new Potion(PotionType.POISON);
				
			case 8197:
				return new Potion(PotionType.INSTANT_HEAL);
				
			case 8198:
				return new Potion(PotionType.NIGHT_VISION);
				
			case 8200:
				return new Potion(PotionType.WEAKNESS);
				
			case 8201:
				return new Potion(PotionType.STRENGTH);
				
			case 8202:
				return new Potion(PotionType.SLOWNESS);
				
			case 8204:
				return new Potion(PotionType.INSTANT_DAMAGE);
				
			case 8205:
				return new Potion(PotionType.WATER_BREATHING);
				
			case 8206:
				return new Potion(PotionType.INVISIBILITY);
				
			case 8225:
				potion = new Potion(PotionType.REGEN);
				potion.setLevel(2);
				return potion;
				
			case 8226:
				potion = new Potion(PotionType.SPEED);
				potion.setLevel(2);
				return potion;
				
			case 8228:
				potion = new Potion(PotionType.POISON);
				potion.setLevel(2);
				return potion;
				
			case 8229:
				potion = new Potion(PotionType.INSTANT_HEAL);
				potion.setLevel(2);
				return potion;
				
			case 8233:
				potion = new Potion(PotionType.STRENGTH);
				potion.setLevel(2);
				return potion;
				
			case 8236:
				potion = new Potion(PotionType.INSTANT_DAMAGE);
				potion.setLevel(2);
				return potion;
				
			case 8257:
				potion = new Potion(PotionType.REGEN);
				potion.setHasExtendedDuration(true);
				return potion;

			case 8258:
				potion = new Potion(PotionType.SPEED);
				potion.setHasExtendedDuration(true);
				return potion;
				
			case 8259:
				potion = new Potion(PotionType.FIRE_RESISTANCE);
				potion.setHasExtendedDuration(true);
				return potion;

			case 8260:
				potion = new Potion(PotionType.POISON);
				potion.setHasExtendedDuration(true);
				return potion;
				
			case 8262:
				potion = new Potion(PotionType.NIGHT_VISION);
				potion.setHasExtendedDuration(true);
				return potion;

			case 8264:
				potion = new Potion(PotionType.WEAKNESS);
				potion.setHasExtendedDuration(true);
				return potion;
				
			case 8265:
				potion = new Potion(PotionType.STRENGTH);
				potion.setHasExtendedDuration(true);
				return potion;
				
			case 8266:
				potion = new Potion(PotionType.SLOWNESS);
				potion.setHasExtendedDuration(true);
				return potion;
				
			case 8269:
				potion = new Potion(PotionType.WATER_BREATHING);
				potion.setHasExtendedDuration(true);
				return potion;
				
			case 8270:
				potion = new Potion(PotionType.INVISIBILITY);
				potion.setHasExtendedDuration(true);
				return potion;

			case 8289:
				potion = new Potion(PotionType.REGEN);
				potion.setHasExtendedDuration(true);
				potion.setLevel(2);
				return potion;

			case 8290:
				potion = new Potion(PotionType.SPEED);
				potion.setHasExtendedDuration(true);
				potion.setLevel(2);
				return potion;
				
			case 8292:
				potion = new Potion(PotionType.POISON);
				potion.setHasExtendedDuration(true);
				potion.setLevel(2);
				return potion;
				
			case 8297:
				potion = new Potion(PotionType.STRENGTH);
				potion.setHasExtendedDuration(true);
				potion.setLevel(2);
				return potion;
				
			
				// Splash Potions
				
				
			case 16385:
				potion = new Potion(PotionType.REGEN);
				potion.setSplash(true);
				return potion;
				
			case 16386:
				potion = new Potion(PotionType.SPEED);
				potion.setSplash(true);
				return potion;
				
			case 16387:
				potion = new Potion(PotionType.FIRE_RESISTANCE);
				potion.setSplash(true);
				return potion;
				
			case 16388:
				potion = new Potion(PotionType.POISON);
				potion.setSplash(true);
				return potion;
				
			case 16389:
				potion = new Potion(PotionType.INSTANT_HEAL);
				potion.setSplash(true);
				return potion;
				
			case 16390:
				potion = new Potion(PotionType.NIGHT_VISION);
				potion.setSplash(true);
				return potion;
				
			case 16392:
				potion = new Potion(PotionType.WEAKNESS);
				potion.setSplash(true);
				return potion;
				
			case 16393:
				potion = new Potion(PotionType.STRENGTH);
				potion.setSplash(true);
				return potion;
				
			case 16394:
				potion = new Potion(PotionType.SLOWNESS);
				potion.setSplash(true);
				return potion;
				
			case 16396:
				potion = new Potion(PotionType.INSTANT_DAMAGE);
				potion.setSplash(true);
				return potion;
				
			case 16397:
				potion = new Potion(PotionType.WATER_BREATHING);
				potion.setSplash(true);
				return potion;
				
			case 16398:
				potion = new Potion(PotionType.INVISIBILITY);
				potion.setSplash(true);
				return potion;
				
			case 16417:
				potion = new Potion(PotionType.REGEN);
				potion.setLevel(2);
				potion.setSplash(true);
				return potion;
				
			case 16418:
				potion = new Potion(PotionType.SPEED);
				potion.setLevel(2);
				potion.setSplash(true);
				return potion;
				
			case 16420:
				potion = new Potion(PotionType.POISON);
				potion.setLevel(2);
				potion.setSplash(true);
				return potion;
				
			case 16421:
				potion = new Potion(PotionType.INSTANT_HEAL);
				potion.setLevel(2);
				potion.setSplash(true);
				return potion;
				
			case 16425:
				potion = new Potion(PotionType.STRENGTH);
				potion.setLevel(2);
				potion.setSplash(true);
				return potion;
				
			case 16428:
				potion = new Potion(PotionType.INSTANT_DAMAGE);
				potion.setLevel(2);
				potion.setSplash(true);
				return potion;
				
			case 16449:
				potion = new Potion(PotionType.REGEN);
				potion.setHasExtendedDuration(true);
				potion.setSplash(true);
				return potion;

			case 16450:
				potion = new Potion(PotionType.SPEED);
				potion.setHasExtendedDuration(true);
				potion.setSplash(true);
				return potion;
				
			case 16451:
				potion = new Potion(PotionType.FIRE_RESISTANCE);
				potion.setHasExtendedDuration(true);
				potion.setSplash(true);
				return potion;

			case 16452:
				potion = new Potion(PotionType.POISON);
				potion.setHasExtendedDuration(true);
				potion.setSplash(true);
				return potion;
				
			case 16454:
				potion = new Potion(PotionType.NIGHT_VISION);
				potion.setHasExtendedDuration(true);
				potion.setSplash(true);
				return potion;

			case 16456:
				potion = new Potion(PotionType.WEAKNESS);
				potion.setHasExtendedDuration(true);
				potion.setSplash(true);
				return potion;
				
			case 16457:
				potion = new Potion(PotionType.STRENGTH);
				potion.setHasExtendedDuration(true);
				potion.setSplash(true);
				return potion;
				
			case 16458:
				potion = new Potion(PotionType.SLOWNESS);
				potion.setHasExtendedDuration(true);
				potion.setSplash(true);
				return potion;
				
			case 16461:
				potion = new Potion(PotionType.WATER_BREATHING);
				potion.setHasExtendedDuration(true);
				potion.setSplash(true);
				return potion;
				
			case 16462:
				potion = new Potion(PotionType.INVISIBILITY);
				potion.setHasExtendedDuration(true);
				potion.setSplash(true);
				return potion;

			case 16481:
				potion = new Potion(PotionType.REGEN);
				potion.setHasExtendedDuration(true);
				potion.setLevel(2);
				potion.setSplash(true);
				return potion;

			case 16482:
				potion = new Potion(PotionType.SPEED);
				potion.setHasExtendedDuration(true);
				potion.setLevel(2);
				potion.setSplash(true);
				return potion;
				
			case 16484:
				potion = new Potion(PotionType.POISON);
				potion.setHasExtendedDuration(true);
				potion.setLevel(2);
				potion.setSplash(true);
				return potion;
				
			case 16489:
				potion = new Potion(PotionType.STRENGTH);
				potion.setHasExtendedDuration(true);
				potion.setLevel(2);
				potion.setSplash(true);
				return potion;
			}
		}
		
		return null;
	}

    /**
     * Convert a Potion object to its meta id.
     */
	@Override
	protected Short onUnconvert(Object value) {
		if (value instanceof Potion) {
			Potion potion = (Potion)value;
			return potion.toDamageValue();
		}
		return 8192;
	}

}
