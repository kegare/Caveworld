/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.item;

import caveworld.core.Caveworld;
import caveworld.inventory.InventoryCaverBackpack;
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.server.OpenGuiMessage;
import caveworld.plugin.sextiarysector.SextiarySectorPlugin;
import caveworld.util.CaveUtils;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import shift.sextiarysector.api.equipment.EquipmentType;
import shift.sextiarysector.api.equipment.IEquipment;
import shift.sextiarysector.item.ISSEquipment;

@InterfaceList(value =
{
	@Interface(iface = "shift.sextiarysector.api.equipment.IEquipment", modid = SextiarySectorPlugin.MODID, striprefs = true),
	@Interface(iface = "shift.sextiarysector.item.ISSEquipment", modid = SextiarySectorPlugin.MODID, striprefs = true)
})
public class ItemCaverBackpack extends Item implements IEquipment, ISSEquipment
{
	public ItemCaverBackpack(String name)
	{
		super();
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:caver_backpack");
		this.setMaxStackSize(1);
		this.setFull3D();
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			player.openGui(Caveworld.instance, 0, world, 0, 0, 0);

			world.playSoundAtEntity(player, "random.click", 0.6F, 1.5F);
		}

		return super.onItemRightClick(itemstack, world, player);
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack)
	{
		return ItemCavenium.cavenium;
	}

	public IInventory getInventory(ItemStack itemstack)
	{
		return new InventoryCaverBackpack(itemstack);
	}

	public void carryInventory(IInventory from, IInventory to)
	{
		int i = 0;

		for (int j = 0; j < to.getSizeInventory(); ++j)
		{
			if (to.getStackInSlot(j) != null)
			{
				++i;
				break;
			}
		}

		if (i > 0)
		{
			for (int j = 0; j < from.getSizeInventory(); ++j)
			{
				ItemStack item = from.getStackInSlot(j);

				if (item != null)
				{
					CaveUtils.addItemStackToInventory(to, item);

					if (item.stackSize <= 0)
					{
						from.setInventorySlotContents(j, null);
					}
				}
			}
		}
		else
		{
			int size = to.getSizeInventory();

			for (int j = 0; j < from.getSizeInventory(); ++j)
			{
				if (j > size)
				{
					continue;
				}

				ItemStack item = from.getStackInSlot(j);

				if (item != null && to.isItemValidForSlot(j, item))
				{
					to.setInventorySlotContents(j, item);

					from.setInventorySlotContents(j, null);
				}
			}
		}

		from.markDirty();
		to.markDirty();
	}

	@Override
	public boolean canTakeStack(EquipmentType equipment, ItemStack stack, EntityPlayer player)
	{
		return equipment == EquipmentType.Bag;
	}

	@Override
	public boolean isItemValid(EquipmentType equipment, ItemStack stack)
	{
		return equipment == EquipmentType.Bag;
	}

	@Override
	public void onUpdate(EquipmentType equipment, ItemStack stack, World world, Entity player, int slot) {}

	@Override
	public boolean canDrop(EquipmentType equipment, ItemStack stack, EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getTabName(EquipmentType equipment, ItemStack stack, EntityPlayer player)
	{
		return getUnlocalizedName(stack);
	}

	@Override
	public boolean shouldAddToList(EquipmentType equipment, ItemStack stack, EntityPlayer player)
	{
		return true;
	}

	@Override
	public void onTabClicked(EquipmentType equipment, ItemStack stack, EntityPlayer player)
	{
		CaveNetworkRegistry.sendToServer(new OpenGuiMessage(1));
	}
}