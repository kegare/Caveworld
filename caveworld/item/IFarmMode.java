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
import caveworld.util.farmer.MultiFarmExecutor;
import net.minecraft.entity.player.EntityPlayer;

public interface IFarmMode
{
	public MultiBreakExecutor getExecutor(EntityPlayer player);

	public MultiFarmExecutor getFarmExecutor(EntityPlayer player);

	public void clear(EntityPlayer player);
}