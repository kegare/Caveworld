/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.item;

import caveworld.util.breaker.MultiBreakExecutor;
import net.minecraft.entity.player.EntityPlayer;

public interface IBreakMode
{
	public MultiBreakExecutor getExecutor(EntityPlayer player);

	public boolean clear(EntityPlayer player);
}