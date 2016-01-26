/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.applemilktea;

import caveworld.block.CaveBlocks;
import caveworld.item.CaveItems;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AppleMilkTeaPlugin
{
	public static final String MODID = "DCsAppleMilk";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		Item strangeSlag = GameRegistry.findItem(MODID, "defeatedcrow.strangeSlag");

		RecipeRegisterManager.processorRecipe.addRecipe(new ItemStack(CaveItems.cavenium, 2, 0), false, 3, false, new ItemStack(strangeSlag, 1, 0), 0.5F, new ItemStack(CaveBlocks.cavenium_ore, 1, 0));
		RecipeRegisterManager.processorRecipe.addRecipe(new ItemStack(CaveItems.cavenium, 1, 1), false, 3, false, new ItemStack(CaveItems.cavenium, 1, 1), 0.5F, new ItemStack(CaveBlocks.cavenium_ore, 1, 1));
		RecipeRegisterManager.processorRecipe.addRecipe(new ItemStack(CaveItems.gem, 2, 0), false, 3, false, new ItemStack(strangeSlag, 1, 0), 0.5F, new ItemStack(CaveBlocks.gem_ore, 1, 0));
	}
}