package caveworld.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;

/**
 * NOTE: Do NOT access to this class fields.
 * You should use API from this class methods.
 */
public final class CaveworldAPI
{
	public static final String
	MODID = "caveworld",
	API_VERSION = "2.2.7";

	public static ICaveAPIHandler apiHandler;
	public static ICaveBiomeManager biomeManager;
	public static ICaveVeinManager veinManager;
	public static ICaveBiomeManager biomeCavernManager;
	public static ICaveVeinManager veinCavernManager;
	public static ICaveBiomeManager biomeAquaCavernManager;
	public static ICaveVeinManager veinAquaCavernManager;

	private CaveworldAPI() {}

	/**
	 * Returns current mod version of Caveworld
	 */
	public static String getVersion()
	{
		return apiHandler == null ? null : apiHandler.getVersion();
	}

	/**
	 * Returns dimension id of the Caveworld dimension.
	 */
	public static int getDimension()
	{
		return apiHandler == null ? DimensionManager.getNextFreeDimId() : apiHandler.getDimension();
	}

	/**
	 * Returns dimension id of the Cavern dimension.
	 */
	public static int getCavernDimension()
	{
		return apiHandler == null ? DimensionManager.getNextFreeDimId() : apiHandler.getCavernDimension();
	}

	/**
	 * Returns dimension id of the Aqua Cavern dimension.
	 */
	public static int getAquaCavernDimension()
	{
		return apiHandler == null ? DimensionManager.getNextFreeDimId() : apiHandler.getAquaCavernDimension();
	}

	/**
	 * Returns dimension id of the Caveland dimension.
	 */
	public static int getCavelandDimension()
	{
		return apiHandler == null ? DimensionManager.getNextFreeDimId() : apiHandler.getCavelandDimension();
	}

	/**
	 * Returns dimension id of the Cavenia dimension.
	 */
	public static int getCaveniaDimension()
	{
		return apiHandler == null ? DimensionManager.getNextFreeDimId() : apiHandler.getCaveniaDimension();
	}

	/**
	 * Checks if entity is in Caveworld.
	 * @param entity The entity
	 * @return <tt>true</tt> if the entity is in the Caveworld dimension.
	 */
	public static boolean isEntityInCaveworld(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInCaveworld(entity);
	}

	/**
	 * Checks if entity is in Cavern.
	 * @param entity The entity
	 * @return <tt>true</tt> if the entity is in the Cavern dimension.
	 */
	public static boolean isEntityInCavern(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInCavern(entity);
	}

	/**
	 * Checks if entity is in Aqua Cavern.
	 * @param entity The entity
	 * @return <tt>true</tt> if the entity is in the Aqua Cavern dimension.
	 */
	public static boolean isEntityInAquaCavern(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInAquaCavern(entity);
	}

	/**
	 * Checks if entity is in Caveland.
	 * @param entity The entity
	 * @return <tt>true</tt> if the entity is in the Caveland dimension.
	 */
	public static boolean isEntityInCaveland(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInCaveland(entity);
	}

	/**
	 * Checks if entity is in Cavenia.
	 * @param entity The entity
	 * @return <tt>true</tt> if the entity is in the Cavenia dimension.
	 */
	public static boolean isEntityInCavenia(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInCavenia(entity);
	}

	/**
	 * Checks if entity is in dimensions for Caveworld mod.
	 * @param entity The entity
	 */
	public static boolean isEntityInCaves(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInCaves(entity);
	}

	/**
	 * Checks if the dimension is dimensions for Caveworld mod.
	 * @param dim The dimension
	 */
	public static boolean isCaveDimensions(int dim)
	{
		return apiHandler != null && apiHandler.isCaveDimensions(dim);
	}

	/**
	 * Returns true if hardcore option is enabled.
	 */
	public static boolean isHardcore()
	{
		return apiHandler != null && apiHandler.isHardcore();
	}

	/**
	 * Returns true if caveborn option is enabled.
	 */
	public static boolean isCaveborn()
	{
		return getCaveborn() > 0;
	}

	/**
	 * Returns caveborn type.
	 * @return 0: Disabled, 1: Caveworld, 2: Cavern, 3:Aqua Cavern, 4:Caveland
	 */
	public static int getCaveborn()
	{
		return apiHandler == null ? 0 : apiHandler.getCaveborn();
	}

	/**
	 * @see ICaveBiomeManager#addCaveBiome(ICaveBiome)
	 */
	public static boolean addCaveBiome(ICaveBiome biome)
	{
		return biomeManager != null && biomeManager.addCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#removeCaveBiome(BiomeGenBase)
	 */
	public static boolean removeCaveBiome(BiomeGenBase biome)
	{
		return biomeManager != null && biomeManager.removeCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getActiveBiomeCount()
	 */
	public static int getActiveBiomeCount()
	{
		return biomeManager == null ? 0 : biomeManager.getActiveBiomeCount();
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiome(BiomeGenBase)
	 */
	public static ICaveBiome getCaveBiome(BiomeGenBase biome)
	{
		return biomeManager == null ? new DummyCaveBiome(biome) : biomeManager.getCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getRandomCaveBiome(Random)
	 */
	public static ICaveBiome getRandomCaveBiome(Random random)
	{
		return biomeManager == null ? null : biomeManager.getRandomCaveBiome(random);
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiomes()
	 */
	public static Set<ICaveBiome> getCaveBiomes()
	{
		return biomeManager == null ? new HashSet<ICaveBiome>() : biomeManager.getCaveBiomes();
	}

	/**
	 * @see ICaveBiomeManager#getBiomeList()
	 */
	public static List<BiomeGenBase> getBiomeList()
	{
		return biomeManager == null ? new ArrayList<BiomeGenBase>() : biomeManager.getBiomeList();
	}

	/**
	 * @see ICaveBiomeManager#clearCaveBiomes()
	 */
	public static void clearCaveBiomes()
	{
		if (biomeManager != null)
		{
			biomeManager.clearCaveBiomes();
		}
	}

	/**
	 * @see ICaveVeinManager#addCaveVein(ICaveVein)
	 */
	public static boolean addCaveVein(ICaveVein vein)
	{
		return veinManager != null && veinManager.addCaveVein(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(ICaveVein)
	 */
	public static int removeCaveVeins(ICaveVein vein)
	{
		return veinManager == null ? 0 : veinManager.removeCaveVeins(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(Block, int)
	 */
	public static int removeCaveVeins(Block block, int metadata)
	{
		return veinManager == null ? 0 : veinManager.removeCaveVeins(block, metadata);
	}

	/**
	 * @see ICaveVeinManager#getRandomCaveVein(Random)
	 */
	public static ICaveVein getRandomCaveVein(Random random)
	{
		return veinManager == null ? null : veinManager.getRandomCaveVein(random);
	}

	public static List<ICaveVein> getCaveVeins()
	{
		return veinManager == null ? new ArrayList<ICaveVein>() : veinManager.getCaveVeins();
	}

	/**
	 * @see ICaveVeinManager#clearCaveVeins()
	 */
	public static void clearCaveVeins()
	{
		if (veinManager != null)
		{
			veinManager.clearCaveVeins();
		}
	}

	/**
	 * @see ICaveBiomeManager#addCaveBiome(ICaveBiome)
	 */
	public static boolean addCavernBiome(ICaveBiome biome)
	{
		return biomeCavernManager != null && biomeCavernManager.addCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#removeCaveBiome(BiomeGenBase)
	 */
	public static boolean removeCavernBiome(BiomeGenBase biome)
	{
		return biomeCavernManager != null && biomeCavernManager.removeCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getActiveBiomeCount()
	 */
	public static int getActiveCavernBiomeCount()
	{
		return biomeCavernManager == null ? 0 : biomeCavernManager.getActiveBiomeCount();
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiome(BiomeGenBase)
	 */
	public static ICaveBiome getCavernBiome(BiomeGenBase biome)
	{
		return biomeCavernManager == null ? new DummyCaveBiome(biome) : biomeCavernManager.getCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getRandomCaveBiome(Random)
	 */
	public static ICaveBiome getRandomCavernBiome(Random random)
	{
		return biomeCavernManager == null ? null : biomeCavernManager.getRandomCaveBiome(random);
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiomes()
	 */
	public static Set<ICaveBiome> getCavernBiomes()
	{
		return biomeCavernManager == null ? new HashSet<ICaveBiome>() : biomeCavernManager.getCaveBiomes();
	}

	/**
	 * @see ICaveBiomeManager#getBiomeList()
	 */
	public static List<BiomeGenBase> getCavernBiomeList()
	{
		return biomeCavernManager == null ? new ArrayList<BiomeGenBase>() : biomeCavernManager.getBiomeList();
	}

	/**
	 * @see ICaveBiomeManager#clearCaveBiomes()
	 */
	public static void clearCavernBiomes()
	{
		if (biomeCavernManager != null)
		{
			biomeCavernManager.clearCaveBiomes();
		}
	}

	/**
	 * @see ICaveVeinManager#addCaveVein(ICaveVein)
	 */
	public static boolean addCavernVein(ICaveVein vein)
	{
		return veinCavernManager != null && veinCavernManager.addCaveVein(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(ICaveVein)
	 */
	public static int removeCavernVeins(ICaveVein vein)
	{
		return veinCavernManager == null ? 0 : veinCavernManager.removeCaveVeins(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(Block, int)
	 */
	public static int removeCavernVeins(Block block, int metadata)
	{
		return veinCavernManager == null ? 0 : veinCavernManager.removeCaveVeins(block, metadata);
	}

	/**
	 * @see ICaveVeinManager#getRandomCaveVein(Random)
	 */
	public static ICaveVein getRandomCavernVein(Random random)
	{
		return veinCavernManager == null ? null : veinCavernManager.getRandomCaveVein(random);
	}

	public static List<ICaveVein> getCavernVeins()
	{
		return veinCavernManager == null ? new ArrayList<ICaveVein>() : veinCavernManager.getCaveVeins();
	}

	/**
	 * @see ICaveVeinManager#clearCaveVeins()
	 */
	public static void clearCavernVeins()
	{
		if (veinCavernManager != null)
		{
			veinCavernManager.clearCaveVeins();
		}
	}

	/**
	 * @see ICaveBiomeManager#addCaveBiome(ICaveBiome)
	 */
	public static boolean addAquaCavernBiome(ICaveBiome biome)
	{
		return biomeAquaCavernManager != null && biomeAquaCavernManager.addCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#removeCaveBiome(BiomeGenBase)
	 */
	public static boolean removeAquaCavernBiome(BiomeGenBase biome)
	{
		return biomeAquaCavernManager != null && biomeAquaCavernManager.removeCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getActiveBiomeCount()
	 */
	public static int getActiveAquaCavernBiomeCount()
	{
		return biomeAquaCavernManager == null ? 0 : biomeAquaCavernManager.getActiveBiomeCount();
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiome(BiomeGenBase)
	 */
	public static ICaveBiome getAquaCavernBiome(BiomeGenBase biome)
	{
		return biomeAquaCavernManager == null ? new DummyCaveBiome(biome) : biomeAquaCavernManager.getCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getRandomCaveBiome(Random)
	 */
	public static ICaveBiome getRandomAquaCavernBiome(Random random)
	{
		return biomeAquaCavernManager == null ? null : biomeAquaCavernManager.getRandomCaveBiome(random);
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiomes()
	 */
	public static Set<ICaveBiome> getAquaCavernBiomes()
	{
		return biomeAquaCavernManager == null ? new HashSet<ICaveBiome>() : biomeAquaCavernManager.getCaveBiomes();
	}

	/**
	 * @see ICaveBiomeManager#getBiomeList()
	 */
	public static List<BiomeGenBase> getAquaCavernBiomeList()
	{
		return biomeAquaCavernManager == null ? new ArrayList<BiomeGenBase>() : biomeAquaCavernManager.getBiomeList();
	}

	/**
	 * @see ICaveBiomeManager#clearCaveBiomes()
	 */
	public static void clearAquaCavernBiomes()
	{
		if (biomeAquaCavernManager != null)
		{
			biomeAquaCavernManager.clearCaveBiomes();
		}
	}

	/**
	 * @see ICaveVeinManager#addCaveVein(ICaveVein)
	 */
	public static boolean addAquaCavernVein(ICaveVein vein)
	{
		return veinAquaCavernManager != null && veinAquaCavernManager.addCaveVein(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(ICaveVein)
	 */
	public static int removeAquaCavernVeins(ICaveVein vein)
	{
		return veinAquaCavernManager == null ? 0 : veinAquaCavernManager.removeCaveVeins(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(Block, int)
	 */
	public static int removeAquaCavernVeins(Block block, int metadata)
	{
		return veinAquaCavernManager == null ? 0 : veinAquaCavernManager.removeCaveVeins(block, metadata);
	}

	/**
	 * @see ICaveVeinManager#getRandomCaveVein(Random)
	 */
	public static ICaveVein getRandomAquaCavernVein(Random random)
	{
		return veinAquaCavernManager == null ? null : veinAquaCavernManager.getRandomCaveVein(random);
	}

	public static List<ICaveVein> getAquaCavernVeins()
	{
		return veinAquaCavernManager == null ? new ArrayList<ICaveVein>() : veinAquaCavernManager.getCaveVeins();
	}

	/**
	 * @see ICaveVeinManager#clearCaveVeins()
	 */
	public static void clearAquaCavernVeins()
	{
		if (veinAquaCavernManager != null)
		{
			veinAquaCavernManager.clearCaveVeins();
		}
	}

	public static void addCavesVein(ICaveVein vein)
	{
		addCaveVein(vein);
		addCavernVein(vein);
		addAquaCavernVein(vein);
	}
}