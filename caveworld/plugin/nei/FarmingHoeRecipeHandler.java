/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.nei;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import caveworld.item.CaveItems;
import caveworld.item.ItemFarmingHoe;
import codechicken.nei.recipe.ShapedRecipeHandler;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class FarmingHoeRecipeHandler extends ShapedRecipeHandler
{
	private final List<CachedShapedRecipe> recipes = Lists.newArrayList();

	public List<CachedShapedRecipe> getCachedRecipes()
	{
		if (recipes.isEmpty())
		{
			Object[] items = null;

			for (Item item : CaveItems.farming_hoe.getBaseableItems())
			{
				String name = GameData.getItemRegistry().getNameForObject(item);

				if (Strings.isNullOrEmpty(name))
				{
					continue;
				}

				items = new Object[10];

				for (int slot = 0; slot < items.length; ++slot)
				{
					if (slot == 1 || slot == 3 || slot == 5 || slot == 7)
					{
						items[slot] = new ItemStack(CaveItems.cavenium, 1, 0);
					}
					else if (slot == 4)
					{
						if (item instanceof ItemFarmingHoe)
						{
							items[slot] = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
						}
						else
						{
							items[slot] = new ItemStack(item);
						}
					}
					else if (slot == 9)
					{
						ItemStack result = new ItemStack(CaveItems.farming_hoe);

						if (result.getTagCompound() == null)
						{
							result.setTagCompound(new NBTTagCompound());
						}

						result.getTagCompound().setString("BaseName", name);

						items[slot] = result;
					}
				}

				recipes.add(new CachedShapedRecipe(3, 3, items, (ItemStack)items[9]));
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
		if (outputId.equals("crafting") && getClass() == FarmingHoeRecipeHandler.class)
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
		if (result != null && result.getItem() instanceof ItemFarmingHoe)
		{
			for (CachedShapedRecipe recipe : getCachedRecipes())
			{
				recipe.computeVisuals();
				arecipes.add(recipe);
			}
		}
	}
}