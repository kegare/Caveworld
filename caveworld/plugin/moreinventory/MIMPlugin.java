/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.moreinventory;

import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public class MIMPlugin implements ICavePlugin
{
	public static final String MODID = "MoreInventoryMod";

	public static boolean pluginState = true;

	public static Item torchHolder;
	public static Item arrowHolder;

	public static boolean enabled()
	{
		return pluginState && Loader.isModLoaded(MODID);
	}

	public static boolean hasTorchHolder(EntityPlayer player)
	{
		return torchHolder != null && player.inventory.hasItem(torchHolder);
	}

	public static boolean hasArrowHolder(EntityPlayer player)
	{
		return arrowHolder != null && player.inventory.hasItem(arrowHolder);
	}

	@Override
	public String getModId()
	{
		return MODID;
	}

	@Override
	public boolean getPluginState()
	{
		return pluginState;
	}

	@Override
	public boolean setPluginState(boolean state)
	{
		return pluginState = state;
	}

	@Override
	public void invoke()
	{
		torchHolder = GameRegistry.findItem(MODID, "torchholder");
		arrowHolder = GameRegistry.findItem(MODID, "arrowholder");
	}
}