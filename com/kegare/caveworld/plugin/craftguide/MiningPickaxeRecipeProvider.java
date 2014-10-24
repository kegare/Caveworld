/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin.craftguide;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import uristqwerty.CraftGuide.api.CraftGuideAPIObject;
import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.SlotType;

import com.kegare.caveworld.item.CaveItems;
import com.kegare.caveworld.recipe.RecipeMiningPickaxe;

import cpw.mods.fml.common.registry.GameData;

public class MiningPickaxeRecipeProvider extends CraftGuideAPIObject implements RecipeProvider
{
	private final Slot[] slots;

	public MiningPickaxeRecipeProvider()
	{
		super();
		this.slots = new ItemSlot[]
		{
			new ItemSlot(3, 3, 16, 16),
			new ItemSlot(21, 3, 16, 16),
			new ItemSlot(39, 3, 16, 16),
			new ItemSlot(3, 21, 16, 16),
			new ItemSlot(21, 21, 16, 16),
			new ItemSlot(39, 21, 16, 16),
			new ItemSlot(3, 39, 16, 16),
			new ItemSlot(21, 39, 16, 16),
			new ItemSlot(39, 39, 16, 16),
			new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT),
		};
	}

	@Override
	public void generateRecipes(RecipeGenerator generator)
	{
		RecipeTemplate template = generator.createRecipeTemplate(slots, null, "/gui/CraftGuideRecipe.png", 1, 1, 82, 1);
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

						result.getTagCompound().setString("BaseName", GameData.getItemRegistry().getNameForObject(item.getItem()));
						result.getTagCompound().setInteger("Refined", i);

						items[slot] = result;
					}
				}

				generator.addRecipe(template, items);
			}
		}
	}
}