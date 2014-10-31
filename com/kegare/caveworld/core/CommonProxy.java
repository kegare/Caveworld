/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.core;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.Level;

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.item.CaveItems;
import com.kegare.caveworld.recipe.RecipeMiningPickaxe;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy
{
	public void initializeConfigClasses() {}

	public void registerRenderers() {}

	public int getUniqueRenderType()
	{
		return -1;
	}

	public void displayClientGuiScreen(Object obj) {}

	public void destoryClientBlock(int x, int y, int z) {}

	public void registerRecipes()
	{
		if (Config.rope)
		{
			addShapelessRecipe(new ItemStack(CaveBlocks.rope), Items.string, Items.string, Items.string, Items.leather);
		}

		if (Config.oreCavenium)
		{
			addShapelessRecipe(new ItemStack(CaveItems.cavenium, 9, 0), new ItemStack(CaveBlocks.cavenium_ore, 1, 2));
			addShapelessRecipe(new ItemStack(CaveItems.cavenium, 9, 1), new ItemStack(CaveBlocks.cavenium_ore, 1, 3));
		}

		if (Config.universalChest)
		{
			addShapedRecipe(new ItemStack(CaveBlocks.universal_chest),
				"CCC", "CEC", "CCC",
				'C', new ItemStack(CaveBlocks.cavenium_ore, 1, 3),
				'E', Items.ender_eye
			);
		}

		if (Config.cavenium)
		{
			FurnaceRecipes.smelting().func_151394_a(new ItemStack(CaveBlocks.cavenium_ore, 1, 0), new ItemStack(CaveItems.cavenium, 1, 0), 0.5F);
			FurnaceRecipes.smelting().func_151394_a(new ItemStack(CaveBlocks.cavenium_ore, 1, 1), new ItemStack(CaveItems.cavenium, 1, 1), 0.75F);

			addShapedRecipe(new ItemStack(CaveBlocks.cavenium_ore, 1, 2),
				"CCC", "CCC", "CCC",
				'C', new ItemStack(CaveItems.cavenium, 1, 0)
			);
			addShapedRecipe(new ItemStack(CaveBlocks.cavenium_ore, 1, 3),
				"CCC", "CCC", "CCC",
				'C', new ItemStack(CaveItems.cavenium, 1, 1)
			);
		}

		if (Config.pickaxeMining)
		{
			GameRegistry.addRecipe(RecipeMiningPickaxe.instance);
		}

		if (Config.oreCompass)
		{
			addShapedRecipe(new ItemStack(CaveItems.ore_compass),
				" C ", "CXC", " C ",
				'C', new ItemStack(CaveItems.cavenium, 1, 1),
				'X', Items.compass
			);
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
}