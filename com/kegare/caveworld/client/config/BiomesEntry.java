/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.config;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.core.CaveBiomeManager;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BiomesEntry extends CaveCategoryEntry
{
	public BiomesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return Config.biomesCfg;
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiBiomesEntry(owningScreen);
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}

	@Override
	public void setToDefault()
	{
		CaveworldAPI.clearCaveBiomes();

		List<Property> properties;

		for (ICaveBiome entry : CaveBiomeManager.defaultMapping.values())
		{
			properties = getConfig().getCategory(Integer.toString(entry.getBiome().biomeID)).getOrderedValues();
			properties.get(0).set(entry.getGenWeight());
			properties.get(1).set(GameData.getBlockRegistry().getNameForObject(entry.getTerrainBlock().getBlock()));
			properties.get(2).set(entry.getTerrainBlock().getMetadata());
		}

		OnConfigChangedEvent event = new OnConfigChangedEvent(Caveworld.MODID, "biomes", mc.theWorld != null, false);

		FMLCommonHandler.instance().bus().post(event);

		if (!event.getResult().equals(Result.DENY))
		{
			FMLCommonHandler.instance().bus().post(new PostConfigChangedEvent(event.modID, event.configID, event.isWorldRunning, event.requiresMcRestart));
		}

		if (childScreen instanceof GuiBiomesEntry)
		{
			GuiBiomesEntry gui = (GuiBiomesEntry)childScreen;

			if (gui.biomeList != null)
			{
				gui.biomeList.biomes.clear();
				gui.biomeList.biomes.addAll(CaveworldAPI.getCaveBiomes());
				gui.biomeList.contents.clear();
				gui.biomeList.contents.addAll(gui.biomeList.biomes);
			}
		}
	}
}