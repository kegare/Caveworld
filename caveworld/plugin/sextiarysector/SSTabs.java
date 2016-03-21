/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.sextiarysector;

import caveworld.item.CaveItems;
import net.minecraft.item.ItemStack;
import shift.sextiarysector.api.equipment.EquipmentType;
import shift.sextiarysector.gui.tab.AbstractTab;
import shift.sextiarysector.gui.tab.InventoryTabEquipment;
import shift.sextiarysector.gui.tab.TabManager;

public class SSTabs
{
	public static AbstractTab tabCaverBackpack;

	public static void registerTabs()
	{
		tabCaverBackpack = new InventoryTabEquipment(EquipmentType.Bag, new ItemStack(CaveItems.caver_backpack));

		TabManager.registerTab(tabCaverBackpack);
	}
}