/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.util;

import net.minecraft.entity.player.EntityPlayer;

import java.util.regex.Pattern;

public class CaveUtils
{
	private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

	public static String stripControlCodes(String str)
	{
		return patternControlCode.matcher(str).replaceAll("");
	}

	public static int getMiningLevel(EntityPlayer player)
	{
		return player.getEntityData().getInteger("Caveworld:MiningCount") / 1728;
	}
}