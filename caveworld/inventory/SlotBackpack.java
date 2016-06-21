package caveworld.inventory;

import caveworld.item.ItemCaverBackpack;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotBackpack extends Slot
{
	public SlotBackpack(IInventory inventory, int index, int x, int y)
	{
		super(inventory, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		return itemstack == null || !(itemstack.getItem() instanceof ItemCaverBackpack);
	}
}