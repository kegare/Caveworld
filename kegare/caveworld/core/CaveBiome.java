package kegare.caveworld.core;

import kegare.caveworld.world.BiomeGenCaveworld;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;

public class CaveBiome
{
	public static BiomeGenBase biomeCaveworld;

	public static void load()
	{
		biomeCaveworld = new BiomeGenCaveworld(Config.biomeCaveworld);

		BiomeManager.removeSpawnBiome(biomeCaveworld);
		BiomeDictionary.registerBiomeType(biomeCaveworld, Type.MAGICAL);
	}
}