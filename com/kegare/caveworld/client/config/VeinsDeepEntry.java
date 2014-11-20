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

public class VeinsDeepEntry extends CategoryEntry
{
	public VeinsDeepEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
	{
		super(owningScreen, owningEntryList, configElement);
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiVeinsEntry(owningScreen, CaveworldAPI.veinDeepManager);
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
			FileUtils.forceDelete(new File(Config.veinsDeepCfg.toString()));
		}
		catch (IOException e)
		{
			e.printStackTrace();

			return;
		}

		CaveworldAPI.clearCaveDeepVeins();

		Config.veinsDeepCfg = null;
		Config.syncVeinsDeepCfg();

		if (childScreen instanceof GuiVeinsEntry)
		{
			GuiVeinsEntry gui = (GuiVeinsEntry)childScreen;

			if (gui.veinList != null)
			{
				gui.veinList.veins.clear();
				gui.veinList.veins.addAll(CaveworldAPI.getCaveVeins());
				gui.veinList.contents.clear();
				gui.veinList.contents.addAll(gui.veinList.veins);
			}
		}
	}
}