/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.craftguide;

import com.google.common.base.Strings;

import caveworld.item.CaveItems;
import caveworld.item.ItemFarmingHoe;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import uristqwerty.CraftGuide.api.CraftGuideAPIObject;
import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.SlotType;

public class FarmingHoeRecipeProvider extends CraftGuideAPIObject implements RecipeProvider
{
	private final Slot[] slots;

	private ItemStack output;
	private ItemFarmingHoe hoe;

	public FarmingHoeRecipeProvider()
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

	public FarmingHoeRecipeProvider(ItemStack output)
	{
		this();
		this.output = output;
		this.hoe = (ItemFarmingHoe)output.getItem();
	}

	@Override
	public void generateRecipes(RecipeGenerator generator)
	{
		RecipeTemplate template = generator.createRecipeTemplate(slots, null, "/gui/CraftGuideRecipe.png", 1, 1, 82, 1);
		Object[] items = null;

		for (Item item : hoe.getBaseableItems())
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
					if (item == output.getItem())
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
					ItemStack result = output.copy();

					if (result.getTagCompound() == null)
					{
						result.setTagCompound(new NBTTagCompound());
					}

					result.getTagCompound().setString("BaseName", name);

					items[slot] = result;
				}
			}

			generator.addRecipe(template, items);
		}
	}
}