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
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;

import com.google.common.collect.Lists;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;
import com.kegare.caveworld.plugin.mceconomy.ShopEntry;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaveConfigGui extends GuiConfig
{
	public static boolean detailInfo = true;
	public static boolean instantFilter = true;

	public CaveConfigGui(GuiScreen parent)
	{
		super(parent, getConfigElements(), Caveworld.MODID, false, false, I18n.format(Caveworld.CONFIG_LANG + "title"));
	}

	private static List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		list.add(new CaveCategoryElement(Configuration.CATEGORY_GENERAL, GeneralEntry.class));
		list.add(new CaveCategoryElement("blocks", BlocksEntry.class));
		list.add(new CaveCategoryElement("items", ItemsEntry.class));
		list.add(new CaveCategoryElement("entities", EntitiesEntry.class));
		list.add(new CaveCategoryElement("dimension", DimensionEntry.class));

		if (MCEconomyPlugin.enabled())
		{
			list.add(new CaveCategoryElement("shop", ShopEntry.class));
		}

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