/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.client;

import com.kegare.caveworld.client.config.SelectBiomeEntry;
import com.kegare.caveworld.client.config.SelectBlockEntry;
import com.kegare.caveworld.client.config.SelectItemEntry;
import com.kegare.caveworld.client.config.VeinsEntry.VeinConfigEntry;
import com.kegare.caveworld.core.CaveVeinManager;
import com.kegare.caveworld.core.CommonProxy;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;
import com.kegare.caveworld.plugin.mceconomy.ShopEntry.ShopProductEntry;
import com.kegare.caveworld.renderer.RenderPortalCaveworld;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void initializeConfigClasses()
	{
		Config.selectBlockEntryClass = SelectBlockEntry.class;
		Config.selectItemEntryClass = SelectItemEntry.class;
		Config.selectBiomeEntryClass = SelectBiomeEntry.class;
		CaveVeinManager.veinEntryClass = VeinConfigEntry.class;
		MCEconomyPlugin.productEntryClass = ShopProductEntry.class;
	}

	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerBlockHandler(new RenderPortalCaveworld());
	}

	@Override
	public int getUniqueRenderType()
	{
		return RenderingRegistry.getNextAvailableRenderId();
	}
}