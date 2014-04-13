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

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;

import com.google.common.collect.Maps;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.world.WorldProviderCaveworld;

public class InventoryCaveworldPortal extends InventoryBasic
{
	private final Map<String, ChunkCoordinates> portalCoord = Maps.newHashMap();

	public InventoryCaveworldPortal()
	{
		super("inventory.portal.caveworld", false, 18);
	}

	public InventoryCaveworldPortal setPortalPosition(EntityPlayer player, int x, int y, int z)
	{
		portalCoord.put(player.getCommandSenderName(), new ChunkCoordinates(x, y, z));

		return this;
	}

	public InventoryCaveworldPortal loadInventoryFromNBT()
	{
		NBTTagCompound data = WorldProviderCaveworld.getDimData();

		if (!data.hasKey("PortalItems"))
		{
			return this;
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
				nbttag.setByte("Slot", (byte)slot);
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
		ChunkCoordinates coord = portalCoord.get(player.getCommandSenderName());

		if (coord == null)
		{
			return false;
		}

		int x = coord.posX;
		int y = coord.posY;
		int z = coord.posZ;

		if (player.worldObj.getBlock(x, y, z) != CaveBlocks.caveworld_portal)
		{
			return false;
		}

		return player.getDistance(x, y, z) <= 6.0D;
	}
}