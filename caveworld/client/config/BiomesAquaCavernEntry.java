/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import caveworld.api.CaveworldAPI;
import caveworld.core.Config;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public class BiomesAquaCavernEntry extends CategoryEntry
{
	public BiomesAquaCavernEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiBiomesEntry(owningScreen, CaveworldAPI.biomeAquaCavernManager);
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
			FileUtils.forceDelete(new File(Config.biomesAquaCavernCfg.toString()));
		}
		catch (IOException e)
		{
			e.printStackTrace();

			return;
		}

		CaveworldAPI.clearAquaCavernBiomes();

		Config.biomesAquaCavernCfg = null;
		Config.syncBiomesAquaCavernCfg();

		if (childScreen instanceof GuiBiomesEntry)
		{
			GuiBiomesEntry gui = (GuiBiomesEntry)childScreen;

			if (gui.biomeList != null)
			{
				gui.biomeList.biomes.clear();
				gui.biomeList.biomes.addAll(CaveworldAPI.getAquaCavernBiomes());
				gui.biomeList.contents.clear();
				gui.biomeList.contents.addAll(gui.biomeList.biomes);
			}
		}
	}
}