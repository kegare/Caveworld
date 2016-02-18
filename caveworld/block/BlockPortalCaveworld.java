/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.block;

import java.util.Arrays;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import caveworld.api.CaveworldAPI;
import caveworld.client.gui.MenuType;
import caveworld.world.TeleporterCaveworld;
import caveworld.world.WorldProviderCaveworld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class BlockPortalCaveworld extends BlockCavePortal implements IInventory
{
	private final ItemStack[] inventoryContents = new ItemStack[getSizeInventory()];
	private final Table<String, Integer, ChunkCoordinates> portalCoord = HashBasedTable.create();

	public BlockPortalCaveworld(String name)
	{
		super(name);
		this.setBlockTextureName("caveworld:caveworld_portal");
	}

	@Override
	public int getType()
	{
		return WorldProviderCaveworld.TYPE;
	}

	@Override
	public MenuType getMenuType()
	{
		return MenuType.CAVEWORLD_PORTAL;
	}

	@Override
	public boolean isEntityInCave(Entity entity)
	{
		return CaveworldAPI.isEntityInCaveworld(entity);
	}

	@Override
	public int getDimension()
	{
		return CaveworldAPI.getDimension();
	}

	@Override
	public int getLastDimension(Entity entity)
	{
		return CaveworldAPI.getLastDimension(entity);
	}

	@Override
	public void setLastDimension(Entity entity, int dim)
	{
		CaveworldAPI.setLastDimension(entity, dim);
	}

	@Override
	public Teleporter getTeleporter(WorldServer worldServer, boolean brick)
	{
		return new TeleporterCaveworld(worldServer, brick);
	}

	@Override
	public String getInventoryName()
	{
		return "inventory.caveworld.portal";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getSizeInventory()
	{
		return 18;
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
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		ChunkCoordinates coord = portalCoord.get(player.getGameProfile().getId().toString(), player.dimension);

		if (coord == null)
		{
			return false;
		}

		int x = coord.posX;
		int y = coord.posY;
		int z = coord.posZ;

		if (player.worldObj.getBlock(x, y, z) != this)
		{
			return false;
		}

		return player.getDistance(x, y, z) <= 6.0D;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return true;
	}

	public void displayInventory(EntityPlayer player, int x, int y, int z)
	{
		portalCoord.put(player.getGameProfile().getId().toString(), player.dimension, new ChunkCoordinates(x, y, z));

		player.displayGUIChest(this);
	}

	public void clearInventory()
	{
		Arrays.fill(inventoryContents, null);
	}

	public void loadInventoryFromDimData()
	{
		NBTTagCompound data = WorldProviderCaveworld.getDimData();

		if (!data.hasKey("PortalItems"))
		{
			return;
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

		data.removeTag("PortalItems");
	}

	public void saveInventoryToDimData()
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
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}
}