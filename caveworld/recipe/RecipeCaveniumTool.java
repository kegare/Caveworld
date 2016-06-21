package caveworld.recipe;

import com.google.common.base.Predicate;

import caveworld.item.ICaveniumTool;
import caveworld.item.ItemCavenium;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class RecipeCaveniumTool implements IRecipe
{
	private final ItemStack output;
	private final Predicate<ItemStack> filter;

	public RecipeCaveniumTool(ItemStack output, Predicate<ItemStack> filter)
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
						if (itemstack.getItem() instanceof ICaveniumTool)
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
		int rare = 0;

		for (int row = 0; row < 3; ++row)
		{
			for (int column = 0; column < 3; ++column)
			{
				if (row == 1 && column == 1)
				{
					continue;
				}

				ItemStack itemstack = crafting.getStackInRowAndColumn(row, column);

				if (itemstack != null && itemstack.getItem() != null && itemstack.getItem() instanceof ItemCavenium)
				{
					if (itemstack.getItemDamage() == 1)
					{
						++rare;
					}
				}
			}
		}

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

		if (center.getItem() instanceof ICaveniumTool)
		{
			int refined = ((ICaveniumTool)center.getItem()).getRefined(center);

			data.setInteger("Refined", MathHelper.clamp_int(refined + rare, 0, 4));
		}
		else
		{
			data.setString("BaseName", GameData.getItemRegistry().getNameForObject(center.getItem()));
			data.setInteger("Refined", MathHelper.clamp_int(rare, 0, 4));
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