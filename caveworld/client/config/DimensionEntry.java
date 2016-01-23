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

import caveworld.client.config.CaveConfigGui.CaveCategoryElement;
import caveworld.core.Caveworld;
import caveworld.core.Config;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

@SideOnly(Side.CLIENT)
public class DimensionEntry extends CaveCategoryEntry
{
	public DimensionEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return Config.dimensionCfg;
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		list.add(new DimensionElement(getConfig().getCategory("Caveworld")));
		list.add(new DimensionElement(getConfig().getCategory("Cavern")));

		return list;
	}

	private class DimensionElement extends ConfigElement
	{
		private ConfigCategory category;

		public DimensionElement(ConfigCategory category)
		{
			super(category);
			this.category = category;
		}

		@Override
		public List getChildElements()
		{
			List result = super.getChildElements();

			if (result != null)
			{
				switch (category.getName())
				{
					case "Cavern":
						result.add(new CaveCategoryElement("biomesCavern", BiomesCavernEntry.class));
						result.add(new CaveCategoryElement("veinsCavern", VeinsCavernEntry.class));
						break;
					default:
						result.add(new CaveCategoryElement("biomes", BiomesEntry.class));
						result.add(new CaveCategoryElement("veins", VeinsEntry.class));
						break;
				}
			}

			return result;
		}

		@Override
		public String getComment()
		{
			return I18n.format(Caveworld.CONFIG_LANG + "dimension.entry.tooltip", getName());
		}
	}
}