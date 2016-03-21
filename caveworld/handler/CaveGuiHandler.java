/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.handler;

import caveworld.client.gui.GuiCaverBackpack;
import caveworld.inventory.ContainerCaverBackpack;
import caveworld.inventory.InventoryCaverBackpack;
import caveworld.plugin.sextiarysector.SSHelper;
import caveworld.plugin.sextiarysector.SextiarySectorPlugin;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import shift.sextiarysector.api.equipment.EquipmentType;

public class CaveGuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
			case 0:
				return new ContainerCaverBackpack(player.inventory, new InventoryCaverBackpack(player.getCurrentEquippedItem()));
		}

		if (SextiarySectorPlugin.enabled())
		{
			return getSSServerGuiElement(ID, player);
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
			case 0:
				return new GuiCaverBackpack(player.inventory, new InventoryCaverBackpack(player.getCurrentEquippedItem()));
		}

		if (SextiarySectorPlugin.enabled())
		{
			return getSSClientGuiElement(ID, player);
		}

		return null;
	}

	@Method(modid = SextiarySectorPlugin.MODID)
	public Object getSSServerGuiElement(int ID, EntityPlayer player)
	{
		switch (ID)
		{
			case 1:
				return new ContainerCaverBackpack(player.inventory, new InventoryCaverBackpack(SSHelper.getEquipment(player, EquipmentType.Bag)));
		}

		return null;
	}

	@Method(modid = SextiarySectorPlugin.MODID)
	@SideOnly(Side.CLIENT)
	public Object getSSClientGuiElement(int ID, EntityPlayer player)
	{
		switch (ID)
		{
			case 1:
				return new GuiCaverBackpack(player.inventory, new InventoryCaverBackpack(SSHelper.getEquipment(player, EquipmentType.Bag)));
		}

		return null;
	}
}