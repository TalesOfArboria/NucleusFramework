/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.generic.utils;

import org.bukkit.entity.Player;

public class ExpHelper {

	
	public static int getTotalExp(Player p) {
		//int level = p.getLevel();
		return p.getTotalExperience();// + getExpFromLevel(level);
	}
	
	public static int getExpFromLevel(int level) {
		if (level >= 30) {
	        return 62 + (level - 30) * 7;
	    } else if (level >= 15) {
	        return 17 + (level - 15) * 3;
	    } else {
	        return 17;
	    }
	}
	
	public static void setExp(Player p, int exp) {
		p.setTotalExperience(0);
		p.setLevel(0);
		p.setExp(0);
		p.giveExp(exp);
	}
	
	public static void incrementExp(Player p, int amount) {
		p.giveExp(amount);
	}
	
}
