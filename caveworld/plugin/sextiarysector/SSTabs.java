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