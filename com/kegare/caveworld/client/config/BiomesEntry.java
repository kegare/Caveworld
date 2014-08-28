/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.client.config;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.util.ConfigCategoryFunction;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BiomesEntry extends CaveCategoryEntry
{
	public BiomesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return Config.biomesCfg;
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();
		Collection<ConfigCategory> categories = Collections2.transform(getConfig().getCategoryNames(), new ConfigCategoryFunction(getConfig()));

		for (ConfigCategory category : categories)
		{
			list.add(new BiomeElement(category));
		}

		return list;
	}

	public static class BiomeElement extends ConfigElement
	{
		private final ConfigCategory category;
		private BiomeGenBase biome;

		public BiomeElement(ConfigCategory category)
		{
			super(category);
			this.category = category;
			this.biome = BiomeGenBase.getBiome(NumberUtils.toInt(category.getName(), BiomeGenBase.plains.biomeID));
		}

		public BiomeGenBase getBiome()
		{
			return biome == null ? BiomeGenBase.plains : biome;
		}

		@Override
		public String getName()
		{
			return BiomeGenBase.getBiome(Integer.parseInt(super.getName())).biomeName;
		}

		@Override
		public String getComment()
		{
			List<String> list = Lists.newArrayList();
			list.add(category.get("genWeight").getString());
			list.add(category.get("terrainBlock").getString());
			list.add(category.get("terrainBlockMetadata").getString());
			String data = Joiner.on(", ").skipNulls().join(list);

			if (BiomeDictionary.isBiomeRegistered(getBiome()))
			{
				Set<String> types = Sets.newTreeSet();

				for (Type type : BiomeDictionary.getTypesForBiome(getBiome()))
				{
					types.add(type.name());
				}

				return super.getName() + ": " + Joiner.on(", ").skipNulls().join(types) + " [" + data + "]";
			}

			return super.getName() + ": [" + data + "]";
		}
	}
}