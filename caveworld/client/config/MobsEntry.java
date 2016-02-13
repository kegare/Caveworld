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
public class MobsEntry extends CaveCategoryEntry
{
	public MobsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return Config.mobsCfg;
	}

	@Override
	protected String getEntryName()
	{
		return "mobs";
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		for (String category : getConfig().getCategoryNames())
		{
			list.add(new EntityElement(getConfig().getCategory(category)));
		}

		return list;
	}

	private class EntityElement extends ConfigElement
	{
		public EntityElement(ConfigCategory category)
		{
			super(category);
		}

		@Override
		public String getName()
		{
			return I18n.format("entity.caveworld." + super.getName() + ".name");
		}

		@Override
		public String getComment()
		{
			return I18n.format(Caveworld.CONFIG_LANG + getEntryName() +".entry.tooltip", getName());
		}
	}
}