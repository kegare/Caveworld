/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.proxy;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.Level;

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.config.Config;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommonProxy
{
	public void registerRenderers() {}

	public int getUniqueRenderType()
	{
		return -1;
	}

	public void registerRecipes()
	{
		if (CaveBlocks.rope != null)
		{
			addShapelessRecipe(new ItemStack(CaveBlocks.rope), Items.string, Items.string, Items.string, Items.leather);
		}

		if (Config.portalCraftRecipe)
		{
			addShapedRecipe(new ItemStack(CaveBlocks.caveworld_portal),
				" E ", "EPE", " D ",
				'E', Items.emerald,
				'P', Items.ender_pearl,
				'D', Items.diamond
			);
		}

		if (Config.mossStoneCraftRecipe)
		{
			addShapedRecipe(new ItemStack(Blocks.mossy_cobblestone),
				" V ", "VCV", " V ",
				'V', Blocks.vine,
				'C', Blocks.cobblestone
			);
		}
	}

	public void addShapedRecipe(ItemStack result, Object... recipe)
	{
		try
		{
			CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(result, recipe));
		}
		catch (Exception e)
		{
			CaveLog.log(Level.WARN, e, "Failed to register a shaped recipe: %s", result.getUnlocalizedName());
		}
	}

	public void addShapelessRecipe(ItemStack result, Object... recipe)
	{
		try
		{
			CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(result, recipe));
		}
		catch (Exception e)
		{
			CaveLog.log(Level.WARN, e, "Failed to register a shapeless recipe: %s", result.getUnlocalizedName());
		}
	}

	public MinecraftServer getServer()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}
}