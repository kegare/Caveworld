/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import java.util.Map;

import com.google.common.collect.Maps;

import caveworld.api.ICaveBiome;
import caveworld.world.WorldProviderCavern;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;

public class CavernBiomeManager extends CaveBiomeManager
{
	private final Map<BiomeGenBase, ICaveBiome> CAVE_BIOMES = Maps.newHashMap();

	@Override
	public Configuration getConfig()
	{
		return Config.biomesCavernCfg;
	}

	@Override
	public int getType()
	{
		return WorldProviderCavern.TYPE;
	}

	@Override
	public Map<BiomeGenBase, ICaveBiome> getRaw()
	{
		return CAVE_BIOMES;
	}
}