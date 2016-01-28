/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.inventory;

import invtweaks.api.container.ChestContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

@ChestContainer
public class ContainerCaverBackpack extends Container
{
	private InventoryCaverBackpack backpackInventory;
	private int numRows;

	public ContainerCaverBackpack(InventoryPlayer inventory, InventoryCaverBackpack backpackInventory)
	{
		this.backpackInventory = backpackInventory;
		this.numRows = backpackInventory.getSizeInventory() / 9;
		backpackInventory.openInventory();
		int i = (numRows - 4) * 18;
		int j;
		int k;

		for (j = 0; j < numRows; ++j)
		{
			for (k = 0; k < 9; ++k)
			{
				addSlotToContainer(new SlotBackpack(backpackInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}

		for (j = 0; j < 3; ++j)
		{
			for (k = 0; k < 9; ++k)
			{
				addSlotToContainer(new Slot(inventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
			}
		}

		for (j = 0; j < 9; ++j)
		{
			if (j == inventory.currentItem)
			{
				addSlotToContainer(new SlotFixed(inventory, j, 8 + j * 18, 161 + i));
			}
			else
			{
				addSlotToContainer(new Slot(inventory, j, 8 + j * 18, 161 + i));
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return backpackInventory.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();

			if (!backpackInventory.isItemValidForSlot(0, itemstack1))
			{
				return null;
			}

			itemstack = itemstack1.copy();

			if (index < numRows * 9)
			{
				if (!mergeItemStack(itemstack1, numRows * 9, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!mergeItemStack(itemstack1, 0, numRows * 9, false))
			{
				return null;
			}

			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack)null);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);

		backpackInventory.closeInventory();
	}
}