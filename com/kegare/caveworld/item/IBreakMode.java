/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.item;

import net.minecraft.entity.player.EntityPlayer;

import com.kegare.caveworld.util.breaker.MultiBreakExecutor;

public interface IBreakMode
{
	public MultiBreakExecutor getExecutor(EntityPlayer player);

	public boolean clear(EntityPlayer player);
}