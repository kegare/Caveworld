/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.config;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

@SideOnly(Side.CLIENT)
public abstract class CaveCategoryEntry extends CategoryEntry
{
	public CaveCategoryEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	protected abstract Configuration getConfig();

	protected abstract String getEntryName();

	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		for (String category : getConfig().getCategoryNames())
		{
			list.addAll(new ConfigElement(getConfig().getCategory(category)).getChildElements());
		}

		return list;
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiConfig(owningScreen, getConfigElements(), owningScreen.modID, getEntryName(),
			configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
			GuiConfig.getAbridgedConfigPath(getConfig().toString()));
	}

	@Override
	public boolean enabled()
	{
		if (childScreen instanceof GuiConfig)
		{
			if (((GuiConfig)childScreen).configElements.isEmpty())
			{
				return false;
			}
		}

		return super.enabled();
	}
}