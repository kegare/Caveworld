package caveworld.item;

import net.minecraft.item.ItemStack;

public class ItemInfititeAxe extends ItemCaveAxe
{
	public ItemInfititeAxe(String name)
	{
		super(name, "infitite_axe", CaveItems.INFITITE);
	}

	@Override
	public int getDamage(ItemStack itemstack)
	{
		return 0;
	}

	@Override
	public void setDamage(ItemStack itemstack, int damage)
	{
		super.setDamage(itemstack, 0);
	}

	@Override
	public boolean isDamaged(ItemStack itemstack)
	{
		return false;
	}
}