/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin.tconstruct;

import net.minecraft.item.Item;

import com.kegare.caveworld.recipe.RecipeMiningPickaxe;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;

public class TinkersConstructPlugin
{
	public static final String MODID = "TConstruct";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		Item item = GameRegistry.findItem(MODID, "pickaxe");

		if (item != null)
		{
			RecipeMiningPickaxe.pickaxeWhitelist.add(item);
		}
	}
}