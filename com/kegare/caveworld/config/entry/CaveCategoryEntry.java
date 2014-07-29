package com.kegare.caveworld.config.entry;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import com.google.common.collect.Lists;
import com.kegare.caveworld.config.Config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class CaveCategoryEntry extends CategoryEntry
{
	public CaveCategoryEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	protected abstract Configuration getConfig();

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
		return new GuiConfig(owningScreen, getConfigElements(), owningScreen.modID, Config.getConfigName(getConfig()),
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