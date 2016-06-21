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