package caveworld.inventory;

import caveworld.item.ItemCaverBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryCaverBackpack implements IInventory
{
	private ItemStack itemstack;

	private final ItemStack[] inventoryContents;

	public InventoryCaverBackpack(ItemStack itemstack)
	{
		this.itemstack = itemstack;
		this.inventoryContents = new ItemStack[getSizeInventory()];
		this.loadNBTData();
	}

	@Override
	public String getInventoryName()
	{
		return itemstack.getUnlocalizedName() + ".name";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getSizeInventory()
	{
		return 9 * 3;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return slot >= 0 && slot < inventoryContents.length ? inventoryContents[slot] : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int stack)
	{
		if (getStackInSlot(slot) != null)
		{
			ItemStack itemstack;

			if (getStackInSlot(slot).stackSize <= stack)
			{
				itemstack = getStackInSlot(slot);
				setInventorySlotContents(slot, null);

				return itemstack;
			}

			itemstack = getStackInSlot(slot).splitStack(stack);

			if (getStackInSlot(slot).stackSize == 0)
			{
				setInventorySlotContents(slot, null);
			}

			return itemstack;
		}

		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if (getStackInSlot(slot) != null)
		{
			ItemStack itemstack = getStackInSlot(slot);
			setInventorySlotContents(slot, null);

			return itemstack;
		}

		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		inventoryContents[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public void markDirty()
	{
		saveNBTData();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return itemstack != null;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return itemstack != null && !(itemstack.getItem() instanceof ItemCaverBackpack);
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	public void loadNBTData()
	{
		NBTTagCompound data = itemstack.getTagCompound();

		if (data == null)
		{
			return;
		}

		NBTTagList list = (NBTTagList)data.getTag("Items");

		if (list != null)
		{
			for (int i = 0; i < list.tagCount(); ++i)
			{
				NBTTagCompound nbttag = list.getCompoundTagAt(i);
				int slot = nbttag.getByte("Slot") & 0xFF;

				if (slot >= 0 && slot < getSizeInventory())
				{
					setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(nbttag));
				}
			}
		}
	}

	public void saveNBTData()
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

		NBTTagCompound data = itemstack.getTagCompound();

		if (data == null)
		{
			data = new NBTTagCompound();
		}

		data.setTag("Items", list);
		itemstack.setTagCompound(data);
	}
}