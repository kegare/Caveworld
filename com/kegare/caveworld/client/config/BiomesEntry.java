/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.config;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;

import org.apache.commons.io.FileUtils;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.Config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BiomesEntry extends CategoryEntry
{
	public BiomesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiBiomesEntry(owningScreen, CaveworldAPI.biomeManager);
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}

	@Override
	public void setToDefault()
	{
		try
		{
			FileUtils.forceDelete(new File(Config.biomesCfg.toString()));
		}
		catch (IOException e)
		{
			e.printStackTrace();

			return;
		}

		CaveworldAPI.clearCaveBiomes();

		Config.biomesCfg = null;
		Config.syncBiomesCfg();

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