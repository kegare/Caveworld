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
import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AppleMilkTeaPlugin implements ICavePlugin
{
	public static final String MODID = "DCsAppleMilk";

	public static boolean pluginState = true;

	public static boolean enabled()
	{
		return pluginState && Loader.isModLoaded(MODID);
	}

	@Override
	public String getModId()
	{
		return MODID;
	}

	@Override
	public boolean getPluginState()
	{
		return pluginState;
	}

	@Override
	public boolean setPluginState(boolean state)
	{
		return pluginState = state;
	}

	@Method(modid = MODID)
	@Override
	public void invoke()
	{
		Item strangeSlag = GameRegistry.findItem(MODID, "defeatedcrow.strangeSlag");

		RecipeRegisterManager.processorRecipe.addRecipe(new ItemStack(CaveItems.cavenium, 2, 0), false, 3, false, new ItemStack(strangeSlag, 1, 0), 0.5F, new ItemStack(CaveBlocks.cavenium_ore, 1, 0));
		RecipeRegisterManager.processorRecipe.addRecipe(new ItemStack(CaveItems.cavenium, 1, 1), false, 3, false, new ItemStack(CaveItems.cavenium, 1, 1), 0.5F, new ItemStack(CaveBlocks.cavenium_ore, 1, 1));
		RecipeRegisterManager.processorRecipe.addRecipe(new ItemStack(CaveItems.gem, 2, 0), false, 3, false, new ItemStack(strangeSlag, 1, 0), 0.5F, new ItemStack(CaveBlocks.gem_ore, 1, 0));
		RecipeRegisterManager.processorRecipe.addRecipe(new ItemStack(CaveItems.gem, 2, 2), false, 2, false, new ItemStack(strangeSlag, 1, 0), 0.5F, new ItemStack(CaveBlocks.gem_ore, 1, 3));
		RecipeRegisterManager.processorRecipe.addRecipe(new ItemStack(CaveItems.gem, 1, 5), false, 3, false, new ItemStack(CaveItems.gem, 1, 5), 0.35F, new ItemStack(CaveBlocks.gem_ore, 1, 7));

		RecipeRegisterManager.slagLoot.addLoot(new ItemStack(CaveItems.cavenium, 1, 0), 3);
		RecipeRegisterManager.slagLoot.addLoot(new ItemStack(CaveItems.cavenium, 1, 1), 4);
		RecipeRegisterManager.slagLoot.addLoot(new ItemStack(CaveItems.gem, 1, 2), 2);
		RecipeRegisterManager.slagLoot.addLoot(new ItemStack(CaveItems.gem, 1, 3), 4);
		RecipeRegisterManager.slagLoot.addLoot(new ItemStack(CaveItems.gem, 1, 5), 5);
	}
}