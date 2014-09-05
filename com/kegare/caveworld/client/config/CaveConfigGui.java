/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.config;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;
import com.kegare.caveworld.plugin.mceconomy.ShopEntry;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaveConfigGui extends GuiConfig
{
	public static boolean instantFilter = true;

	private static final Map<String[], Set<Block>> ignoredBlocksCache = Maps.newHashMap();
	private static final Map<String[], Set<Item>> ignoredItemsCache = Maps.newHashMap();

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

		if (MCEconomyPlugin.enabled())
		{
			list.add(new CaveCategoryElement("shop", ShopEntry.class));
		}

		return list;
	}

	public static Set<Block> getIgnoredRenderBlocks()
	{
		String[] filter = Config.ignoredRenderGuiItems;

		if (filter == null || filter.length <= 0)
		{
			return Collections.EMPTY_SET;
		}

		if (!ignoredBlocksCache.containsKey(filter))
		{
			Set<Block> result = Sets.newHashSet();
			Block block;

			for (Iterator iterator = GameData.getBlockRegistry().iterator(); iterator.hasNext();)
			{
				block = (Block)iterator.next();

				UniqueIdentifier unique = GameRegistry.findUniqueIdentifierFor(block);

				if (unique != null && (ArrayUtils.contains(filter, unique.modId) || ArrayUtils.contains(filter, GameData.getBlockRegistry().getNameForObject(block))))
				{
					result.add(block);
				}
			}

			ignoredBlocksCache.put(filter, result);
		}

		return ignoredBlocksCache.get(filter);
	}

	public static Set<Item> getIgnoredRenderItems()
	{
		String[] filter = Config.ignoredRenderGuiItems;

		if (filter == null || filter.length <= 0)
		{
			return Collections.EMPTY_SET;
		}

		if (!ignoredItemsCache.containsKey(filter))
		{
			Set<Item> result = Sets.newHashSet();
			Item item;

			for (Iterator iterator = GameData.getItemRegistry().iterator(); iterator.hasNext();)
			{
				item = (Item)iterator.next();

				UniqueIdentifier unique = GameRegistry.findUniqueIdentifierFor(item);

				if (unique != null && (ArrayUtils.contains(filter, unique.modId) || ArrayUtils.contains(filter, GameData.getItemRegistry().getNameForObject(item))))
				{
					result.add(item);
				}
			}

			ignoredItemsCache.put(filter, result);
		}

		return ignoredItemsCache.get(filter);
	}

	public static class CaveCategoryElement extends DummyCategoryElement
	{
		public CaveCategoryElement(String name, Class clazz)
		{
			super(Caveworld.MODID + "@" + name + "Cfg", Caveworld.CONFIG_LANG + name, clazz);
		}
	}
}