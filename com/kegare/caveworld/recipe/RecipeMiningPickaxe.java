/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.recipe;

import java.util.Set;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.google.common.collect.Sets;
import com.kegare.caveworld.item.CaveItems;
import com.kegare.caveworld.item.ItemCavenium;
import com.kegare.caveworld.item.ItemMiningPickaxe;

import cpw.mods.fml.common.registry.GameData;

public class RecipeMiningPickaxe implements IRecipe
{
	public static final RecipeMiningPickaxe instance = new RecipeMiningPickaxe();

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

					if (itemstack != null && itemstack.getItem() != null && itemstack.getItem() instanceof ItemPickaxe)
					{
						ItemPickaxe pickaxe = (ItemPickaxe)itemstack.getItem();

						if (pickaxe instanceof ItemMiningPickaxe)
						{
							flag = true;
						}
						else if (itemstack.getItemDamage() == 0 && (pickaxe.func_150913_i().getHarvestLevel() >= 2 || pickaxe.getHarvestLevel(itemstack, "pickaxe") >= 2))
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
		ItemStack result = getRecipeOutput();
		ItemStack center = crafting.getStackInRowAndColumn(1, 1);
		int rare = 0;

		for (int row = 0; row < 3; ++row)
		{
			for (int column = 0; column < 3; ++column)
			{
				if (row != 1 && column == 1 || row == 1 && column != 1)
				{
					if (crafting.getStackInRowAndColumn(row, column).getRarity() == EnumRarity.rare)
					{
						++rare;
					}
				}
			}
		}

		if (center.getItem() instanceof ItemMiningPickaxe)
		{
			result.setTagCompound(center.getTagCompound());
		}
		else
		{
			if (result.getTagCompound() == null)
			{
				result.setTagCompound(new NBTTagCompound());
			}

			NBTTagCompound data = result.getTagCompound();
			ToolMaterial material = ((ItemPickaxe)center.getItem()).func_150913_i();

			data.setInteger("MaxUses", material.getMaxUses());
			data.setFloat("Efficiency", material.getEfficiencyOnProperMaterial());
		}

		result.getTagCompound().setInteger("Refined", rare);

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
		return new ItemStack(CaveItems.mining_pickaxe);
	}

	public Set<ItemStack> getCenterItems()
	{
		Set<String> set = GameData.getItemRegistry().getKeys();
		Set<ItemStack> result = Sets.newHashSet();

		for (String key : set)
		{
			Item item = GameData.getItemRegistry().getObject(key);

			if (item == null)
			{
				continue;
			}
			else if (item instanceof ItemPickaxe && ((ItemPickaxe)item).func_150913_i().getHarvestLevel() >= 2)
			{
				result.add(new ItemStack(item));
			}
		}

		return result;
	}
}