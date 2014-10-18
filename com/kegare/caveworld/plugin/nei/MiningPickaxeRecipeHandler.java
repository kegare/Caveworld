/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin.nei;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import codechicken.nei.recipe.ShapedRecipeHandler;

import com.google.common.collect.Lists;
import com.kegare.caveworld.item.CaveItems;
import com.kegare.caveworld.recipe.RecipeMiningPickaxe;

public class MiningPickaxeRecipeHandler extends ShapedRecipeHandler
{
	private final List<CachedShapedRecipe> recipes = Lists.newArrayList();

	public List<CachedShapedRecipe> getCachedRecipes()
	{
		if (recipes.isEmpty())
		{
			Object[] items = null;

			for (ItemStack item : RecipeMiningPickaxe.instance.getCenterItems())
			{
				for (int i = 0; i <= 4; ++i)
				{
					items = new Object[10];
					int count = i;

					for (int slot = 0; slot < items.length; ++slot)
					{
						if (slot == 1 || slot == 3 || slot == 5 || slot == 7)
						{
							if (--count >= 0)
							{
								items[slot] = new ItemStack(CaveItems.cavenium, 1, 1);
							}
							else
							{
								items[slot] = new ItemStack(CaveItems.cavenium, 1, 0);
							}
						}
						else if (slot == 4)
						{
							items[slot] = item;
						}
						else if (slot == 9)
						{
							ItemStack result = RecipeMiningPickaxe.instance.getRecipeOutput();

							if (result.getTagCompound() == null)
							{
								result.setTagCompound(new NBTTagCompound());
							}

							result.getTagCompound().setInteger("Refined", i);

							items[slot] = result;
						}
					}

					recipes.add(new CachedShapedRecipe(3, 3, items, RecipeMiningPickaxe.instance.getRecipeOutput()));
				}
			}
		}

		return recipes;
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient)
	{
		for (CachedShapedRecipe recipe : getCachedRecipes())
		{
			if (!recipe.contains(recipe.ingredients, ingredient.getItem()))
			{
				continue;
			}

			recipe.computeVisuals();

			if (recipe.contains(recipe.ingredients, ingredient))
			{
				recipe.setIngredientPermutation(recipe.ingredients, ingredient);
				arecipes.add(recipe);
			}
		}
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results)
	{
		if (outputId.equals("crafting") && getClass() == MiningPickaxeRecipeHandler.class)
		{
			for (CachedShapedRecipe recipe : getCachedRecipes())
			{
				recipe.computeVisuals();
				arecipes.add(recipe);
			}
		}
		else super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result)
	{
		if (result != null && result.getItem() == RecipeMiningPickaxe.instance.getRecipeOutput().getItem())
		{
			for (CachedShapedRecipe recipe : getCachedRecipes())
			{
				recipe.computeVisuals();
				arecipes.add(recipe);
			}
		}
	}
}