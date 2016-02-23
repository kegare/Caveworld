/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.recipe;

import com.google.common.base.Predicate;

import caveworld.item.ItemCavenium;
import caveworld.item.ItemFarmingHoe;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeFarmingHoe implements IRecipe
{
	private final ItemStack output;
	private final Predicate<ItemStack> filter;

	public RecipeFarmingHoe(ItemStack output, Predicate<ItemStack> filter)
	{
		this.output = output;
		this.filter = filter;
	}

	@Override
	public boolean matches(InventoryCrafting crafting, World world)
	{
		int i = 0;
		boolean flag = false;

		for (int row = 0; row < 3; ++row)
		{
			for (int column = 0; column < 3; ++column)
			{
				if (row != 1 && column == 1 || row == 1 && column != 1)
				{
					ItemStack itemstack = crafting.getStackInRowAndColumn(row, column);

					if (itemstack != null && itemstack.getItem() != null && itemstack.getItem() instanceof ItemCavenium)
					{
						++i;
					}
				}
				else if (row == 1 && column == 1)
				{
					ItemStack itemstack = crafting.getStackInRowAndColumn(row, column);

					if (itemstack != null && itemstack.getItem() != null && filter.apply(itemstack))
					{
						if (itemstack.getItem() instanceof ItemFarmingHoe)
						{
							flag = true;
						}
						else if (!itemstack.isItemStackDamageable() || itemstack.getItemDamage() == 0)
						{
							flag = true;
						}
					}
				}
			}
		}

		return i == 4 && flag;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting)
	{
		ItemStack result = getRecipeOutput().copy();
		ItemStack center = crafting.getStackInRowAndColumn(1, 1);

		if (center.hasTagCompound())
		{
			result.setTagCompound((NBTTagCompound)center.getTagCompound().copy());
		}

		NBTTagCompound data = result.getTagCompound();

		if (data == null)
		{
			data = new NBTTagCompound();

			result.setTagCompound(data);
		}

		if (!(center.getItem() instanceof ItemFarmingHoe))
		{
			data.setString("BaseName", GameData.getItemRegistry().getNameForObject(center.getItem()));
		}

		return result;
	}

	@Override
	public int getRecipeSize()
	{
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return output;
	}
}