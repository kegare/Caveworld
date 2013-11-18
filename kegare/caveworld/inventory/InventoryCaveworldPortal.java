package kegare.caveworld.inventory;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryCaveworldPortal extends InventoryBasic
{
	public InventoryCaveworldPortal()
	{
		super("Caveworld Portal", true, 18);
	}

	public void loadInventoryFromNBT(NBTTagList list)
	{
		for (int slot = 0; slot < getSizeInventory(); ++slot)
		{
			setInventorySlotContents(slot, (ItemStack)null);
		}

		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttag = (NBTTagCompound)list.tagAt(i);
			int slot = nbttag.getByte("Slot") & 255;

			if (slot >= 0 && slot < getSizeInventory())
			{
				setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(nbttag));
			}
		}
	}

	public NBTTagList saveInventoryToNBT()
	{
		NBTTagList list = new NBTTagList();

		for (int slot = 0; slot < getSizeInventory(); ++slot)
		{
			ItemStack itemstack = getStackInSlot(slot);

			if (itemstack != null)
			{
				NBTTagCompound nbttag = new NBTTagCompound();
				nbttag.setByte("Slot", (byte)slot);
				itemstack.writeToNBT(nbttag);
				list.appendTag(nbttag);
			}
		}

		return list;
	}
}