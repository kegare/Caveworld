/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.ic2;

import caveworld.api.BlockEntry;
import caveworld.api.CaverAPI;
import caveworld.api.CaveworldAPI;
import caveworld.block.CaveBlocks;
import caveworld.core.CaveVeinManager.CaveVein;
import caveworld.core.Config;
import caveworld.item.CaveItems;
import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import ic2.api.item.IC2Items;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class IC2Plugin implements ICavePlugin
{
	public static final String MODID = "IC2";

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
		Recipes.macerator.addRecipe(new RecipeInputItemStack(new ItemStack(CaveBlocks.gem_ore, 1, 3)), null, new ItemStack(CaveItems.gem, 2, 2));

		ItemStack itemstack = IC2Items.getItem("copperOre");
		BlockEntry block;

		if (itemstack != null)
		{
			block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCavesVein(new CaveVein(block, 16, 20, 100, 0, 255));
			}

			CaverAPI.setMiningPointAmount(block.getBlock(), block.getMetadata(), 1);
		}

		itemstack = IC2Items.getItem("tinOre");

		if (itemstack != null)
		{
			block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCavesVein(new CaveVein(block, 14, 24, 100, 0, 255));
			}

			CaverAPI.setMiningPointAmount(block.getBlock(), block.getMetadata(), 1);
		}

		itemstack = IC2Items.getItem("uraniumOre");

		if (itemstack != null)
		{
			block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCavesVein(new CaveVein(block, 10, 7, 100, 0, 50));
			}

			CaverAPI.setMiningPointAmount(block.getBlock(), block.getMetadata(), 2);
		}

		itemstack = IC2Items.getItem("leadOre");

		if (itemstack != null)
		{
			block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCavesVein(new CaveVein(block, 15, 20, 100, 0, 255));
			}

			CaverAPI.setMiningPointAmount(block.getBlock(), block.getMetadata(), 1);
		}
	}
}