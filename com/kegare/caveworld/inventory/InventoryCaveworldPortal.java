/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.inventory;

import com.kegare.caveworld.world.WorldProviderCaveworld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;

public class InventoryCaveworldPortal extends InventoryBasic
{
	private ChunkCoordinates portalCoord;

	public InventoryCaveworldPortal()
	{
		super("inventory.portal.caveworld", false, 18);
	}

	public InventoryCaveworldPortal setPortalPosition(int x, int y, int z)
	{
		portalCoord = new ChunkCoordinates(x, y, z);

		return this;
	}

	public InventoryCaveworldPortal loadInventoryFromNBT()
	{
		NBTTagCompound data = WorldProviderCaveworld.getDimData();

		if (!data.hasKey("PortalItems"))
		{
			data.setTag("PortalItems", new NBTTagList());
		}

		NBTTagList list = (NBTTagList)data.getTag("PortalItems");

		for (int slot = 0; slot < getSizeInventory(); ++slot)
		{
			setInventorySlotContents(slot, null);
		}

		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttag = list.getCompoundTagAt(i);
			int slot = nbttag.getByte("Slot") & 255;

			if (slot >= 0 && slot < getSizeInventory())
			{
				setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(nbttag));
			}
		}

		return this;
	}

	public InventoryCaveworldPortal saveInventoryToNBT()
	{
		NBTTagList list = new NBTTagList();

		for (int slot = 0; slot < getSizeInventory(); ++slot)
		{
			ItemStack itemstack = getStackInSlot(slot);

			if (itemstack != null)
			{
				NBTTagCompound nbttag = new NBTTagCompound();
				nbttag.setByte("Slot", (byte) slot);
				itemstack.writeToNBT(nbttag);
				list.appendTag(nbttag);
			}
		}

		WorldProviderCaveworld.getDimData().setTag("PortalItems", list);

		return this;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return portalCoord != null && player.getDistance(portalCoord.posX, portalCoord.posY, portalCoord.posZ) <= 6.0D;
	}

	@Override
	public void closeInventory()
	{
		super.closeInventory();

		portalCoord = null;
	}
}