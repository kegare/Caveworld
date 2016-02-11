package caveworld.core;

import java.util.Map;

import com.google.common.collect.Maps;

import caveworld.api.ICaveBiome;
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
		return 2;
	}

	@Override
	public Map<BiomeGenBase, ICaveBiome> getRaw()
	{
		return CAVE_BIOMES;
	}
}