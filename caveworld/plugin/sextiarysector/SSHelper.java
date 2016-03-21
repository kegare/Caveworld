/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.sextiarysector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import shift.sextiarysector.api.equipment.EquipmentType;
import shift.sextiarysector.container.InventoryPlayerNext;
import shift.sextiarysector.player.EntityPlayerManager;

public class SSHelper
{
	public static InventoryPlayerNext getSSInventory(EntityPlayer player)
	{
		return EntityPlayerManager.getEquipmentStats(player).inventory;
	}

	public static ItemStack getEquipment(EntityPlayer player, EquipmentType type, int slot)
	{
		return getSSInventory(player).getStackInSlot(type.getSlot(slot));
	}

	public static ItemStack getEquipment(EntityPlayer player, EquipmentType type)
	{
		return getEquipment(player, type, 0);
	}
}