/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.craftguide;

import caveworld.item.CaveItems;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import net.minecraft.item.ItemStack;

public class CraftGuidePlugin
{
	public static final String MODID = "craftguide";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		new CaveniumToolRecipeProvider(new ItemStack(CaveItems.mining_pickaxe));
		new CaveniumToolRecipeProvider(new ItemStack(CaveItems.lumbering_axe));
		new CaveniumToolRecipeProvider(new ItemStack(CaveItems.digging_shovel));
	}
}