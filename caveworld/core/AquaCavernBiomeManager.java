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
import caveworld.world.WorldProviderAquaCavern;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;

public class AquaCavernBiomeManager extends CaveBiomeManager
{
	private final Map<BiomeGenBase, ICaveBiome> CAVE_BIOMES = Maps.newHashMap();

	@Override
	public Configuration getConfig()
	{
		return Config.biomesAquaCavernCfg;
	}

	@Override
	public int getType()
	{
		return WorldProviderAquaCavern.TYPE;
	}

	@Override
	public Map<BiomeGenBase, ICaveBiome> getCaveBiomeMap()
	{
		return CAVE_BIOMES;
	}
}