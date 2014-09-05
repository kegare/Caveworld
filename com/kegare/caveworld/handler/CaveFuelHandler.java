/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.handler;

import net.minecraft.item.ItemStack;

import com.kegare.caveworld.item.ItemRope;

import cpw.mods.fml.common.IFuelHandler;

public class CaveFuelHandler implements IFuelHandler
{
	@Override
	public int getBurnTime(ItemStack fuel)
	{
		if (fuel.getItem() instanceof ItemRope)
		{
			return 50;
		}

		return 0;
	}
}