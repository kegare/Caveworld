/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.config;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;

import com.google.common.collect.Lists;
import com.kegare.caveworld.config.entry.BiomesEntry;
import com.kegare.caveworld.config.entry.BlocksEntry;
import com.kegare.caveworld.config.entry.DimensionEntry;
import com.kegare.caveworld.config.entry.GeneralEntry;
import com.kegare.caveworld.config.entry.VeinsEntry;
import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaveConfigGui extends GuiConfig
{
	public CaveConfigGui(GuiScreen parent)
	{
		super(parent, getConfigElements(), Caveworld.MODID, false, false, I18n.format(Caveworld.CONFIG_LANG + "title"));
	}

	private static List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();
		list.add(new CaveCategoryElement(Configuration.CATEGORY_GENERAL, GeneralEntry.class));
		list.add(new CaveCategoryElement("blocks", BlocksEntry.class));
		list.add(new CaveCategoryElement("dimension", DimensionEntry.class));
		list.add(new CaveCategoryElement("biomes", BiomesEntry.class));
		list.add(new CaveCategoryElement("veins", VeinsEntry.class));

		return list;
	}

	public static class CaveCategoryElement extends DummyCategoryElement
	{
		public CaveCategoryElement(String name, Class clazz)
		{
			super(Caveworld.MODID + "@" + name + "Cfg", Caveworld.CONFIG_LANG + name, clazz);
		}
	}
}