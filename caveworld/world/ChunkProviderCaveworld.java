/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world;

import java.util.List;
import java.util.Random;

import com.google.common.base.Strings;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveBiome;
import caveworld.api.ICaveVein;
import caveworld.entity.CaveEntityRegistry;
import caveworld.world.gen.MapGenCaverns;
import caveworld.world.gen.MapGenCavesCaveworld;
import caveworld.world.gen.MapGenExtremeCaves;
import caveworld.world.gen.MapGenExtremeRavine;
import caveworld.world.gen.MapGenRavineCaveworld;
import caveworld.world.gen.MapGenUnderCaves;
import caveworld.world.gen.WorldGenAnimalDungeons;
import caveworld.world.gen.WorldGenDungeonsCaveworld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkProviderCaveworld implements IChunkProvider
{
	public static int dimensionId;
	public static int subsurfaceHeight;
	public static boolean generateCaves;
	public static boolean generateRavine;
	public static boolean generateUnderCaves;
	public static boolean generateExtremeCaves;
	public static boolean generateExtremeRavine;
	public static boolean generateMineshaft;
	public static boolean generateStronghold;
	public static boolean generateLakes;
	public static boolean generateDungeons;
	public static boolean generateAnimalDungeons;
	public static boolean decorateVines;
	public static String[] spawnerMobs;
	public static int caveMonsterSpawn;
	public static float caveBrightness;

	public static EnumCreatureType caveMonster;

	private final World worldObj;
	private final Random random;
	private final boolean generateStructures;

	private BiomeGenBase[] biomesForGeneration;

	private final MapGenCavesCaveworld caveGenerator = new MapGenCavesCaveworld();
	private final MapGenCaverns cavernsGenerator = new MapGenCaverns();
	private final MapGenRavineCaveworld ravineGenerator = new MapGenRavineCaveworld();
	private final MapGenUnderCaves underCaveGenerator = new MapGenUnderCaves();
	private final MapGenExtremeCaves extremeCaveGenerator = new MapGenExtremeCaves();
	private final MapGenExtremeRavine extremeRavineGenerator = new MapGenExtremeRavine();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
	private final MapGenStronghold strongholdGenerator = new MapGenStronghold();

	private final WorldGenerator lakeWaterGen = new WorldGenLakes(Blocks.water);
	private final WorldGenerator lakeLavaGen = new WorldGenLakes(Blocks.lava);
	private final WorldGenerator dungeonGen = new WorldGenDungeonsCaveworld(spawnerMobs);
	private final WorldGenerator animalDungeonGen = new WorldGenAnimalDungeons();
	private final WorldGenerator glowStoneGen = new WorldGenGlowStone1();
	private final WorldGenerator liquidWaterGen = new WorldGenLiquids(Blocks.flowing_water);
	private final WorldGenerator liquidLavaGen = new WorldGenLiquids(Blocks.flowing_lava);
	private final WorldGenerator vinesGen = new WorldGenVines();

	{
		mineshaftGenerator = (MapGenMineshaft)TerrainGen.getModdedMapGen(mineshaftGenerator, InitMapGenEvent.EventType.MINESHAFT);
	}

	public ChunkProviderCaveworld(World world)
	{
		this.worldObj = world;
		this.random = new Random(world.getSeed());
		this.generateStructures = world.getWorldInfo().isMapFeaturesEnabled();
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);

		biomesForGeneration = worldObj.getWorldChunkManager().getBiomeGenAt(biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16, false);
		int worldHeight = worldObj.provider.getActualHeight();
		Block[] blocks = new Block[65536];
		byte[] metadata = new byte[blocks.length];

		for (int i = 0; i < blocks.length; ++i)
		{
			blocks[i] = Blocks.stone;
			metadata[i] = (byte)0;
		}

		if (generateCaves)
		{
			caveGenerator.generate(this, worldObj, chunkX, chunkZ, blocks, generateUnderCaves);
		}

		if (generateRavine)
		{
			ravineGenerator.generate(this, worldObj, chunkX, chunkZ, blocks, generateUnderCaves);
		}

		if (generateUnderCaves)
		{
			underCaveGenerator.generate(this, worldObj, chunkX, chunkZ, blocks, true);
		}

		if (generateExtremeCaves)
		{
			cavernsGenerator.generate(this, worldObj, chunkX, chunkZ, blocks, generateUnderCaves);

			if (worldHeight > 150)
			{
				extremeCaveGenerator.generate(this, worldObj, chunkX, chunkZ, blocks, generateUnderCaves);
			}
		}

		if (generateExtremeRavine)
		{
			extremeRavineGenerator.generate(this, worldObj, chunkX, chunkZ, blocks, generateUnderCaves);
		}

		if (generateStructures)
		{
			if (generateMineshaft)
			{
				mineshaftGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
			}

			if (generateStronghold)
			{
				strongholdGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
			}
		}

		int i;

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				i = (x * 16 + z) * 256;

				BiomeGenBase biome = biomesForGeneration[x * 16 + z];
				ICaveBiome caveBiome = CaveworldAPI.getCaveBiome(biome);
				Block top = caveBiome.getTopBlock().getBlock();
				int topMeta = caveBiome.getTopBlock().getMetadata();
				Block filler = caveBiome.getTerrainBlock().getBlock();
				int fillerMeta = caveBiome.getTerrainBlock().getMetadata();

				blocks[i] = Blocks.bedrock;
				blocks[i + worldHeight - 1] = Blocks.bedrock;
				blocks[i + worldHeight - 2] = filler;
				metadata[i + worldHeight - 2] = (byte)fillerMeta;

				for (int y = 0; y < worldHeight; ++y)
				{
					if (blocks[i + y] != null && blocks[i + y] == Blocks.stone)
					{
						blocks[i + y] = filler;
						metadata[i + y] = (byte)fillerMeta;
					}
				}

				if (top != filler || topMeta != fillerMeta)
				{
					for (int y = 1; y < worldHeight - 4; ++y)
					{
						if (blocks[i + y] != null && blocks[i + y].getMaterial().isSolid() && blocks[i + y + 1] == null)
						{
							blocks[i + y] = top;
							metadata[i + y] = (byte)topMeta;
						}
					}
				}

				for (int y = 255; y >= worldHeight; --y)
				{
					blocks[i + y] = null;
				}
			}
		}

		Chunk chunk = new Chunk(worldObj, blocks, metadata, chunkX, chunkZ);
		byte[] biomeArray = chunk.getBiomeArray();

		for (i = 0; i < biomeArray.length; ++i)
		{
			biomeArray[i] = (byte)biomesForGeneration[i].biomeID;
		}

		chunk.resetRelightChecks();

		return chunk;
	}

	@Override
	public Chunk loadChunk(int chunkX, int chunkZ)
	{
		return provideChunk(chunkX, chunkZ);
	}

	@Override
	public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ)
	{
		BlockFalling.fallInstantly = true;

		int worldX = chunkX * 16;
		int worldZ = chunkZ * 16;
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(worldX, worldZ);
		BiomeDecorator decorator = biome.theBiomeDecorator;
		int worldHeight = worldObj.provider.getActualHeight();

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider, worldObj, random, chunkX, chunkZ, false));

		if (generateStructures)
		{
			if (generateMineshaft)
			{
				mineshaftGenerator.generateStructuresInChunk(worldObj, random, chunkX, chunkZ);
			}

			if (generateStronghold)
			{
				strongholdGenerator.generateStructuresInChunk(worldObj, random, chunkX, chunkZ);
			}
		}

		int i, x, y, z;

		if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
		{
			if (generateLakes && random.nextInt(4) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAVA))
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 16);
				z = worldZ + random.nextInt(16) + 8;

				lakeLavaGen.generate(worldObj, random, x, y, z);
			}

			if (TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.GLOWSTONE))
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 10) + 10;
				z = worldZ + random.nextInt(16) + 8;

				glowStoneGen.generate(worldObj, random, x, y, z);
			}
		}
		else if (!BiomeDictionary.isBiomeOfType(biome, Type.END))
		{
			if (generateLakes)
			{
				if (!BiomeDictionary.isBiomeOfType(biome, Type.SANDY) && random.nextInt(4) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAKE))
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight - 16);
					z = worldZ + random.nextInt(16) + 8;

					lakeWaterGen.generate(worldObj, random, x, y, z);
				}

				if (random.nextInt(20) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAVA))
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight / 2);
					z = worldZ + random.nextInt(16) + 8;

					lakeLavaGen.generate(worldObj, random, x, y, z);
				}
			}

			if (generateDungeons && generateStructures && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
			{
				for (i = 0; i < 20; ++i)
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight - 24);
					z = worldZ + random.nextInt(16) + 8;

					dungeonGen.generate(worldObj, random, x, y, z);
				}
			}

			if (generateAnimalDungeons && random.nextInt(4) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 24);
				z = worldZ + random.nextInt(16) + 8;

				animalDungeonGen.generate(worldObj, random, x, y, z);
			}
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, random, worldX, worldZ));

		for (ICaveVein vein : CaveworldAPI.getCaveVeins())
		{
			vein.generateVeins(worldObj, random, worldX, worldZ);
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(worldObj, random, worldX, worldZ));
		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(worldObj, random, worldX, worldZ));

		if (TerrainGen.decorate(worldObj, random, worldX, worldZ, Decorate.EventType.SHROOM))
		{
			i = 0;

			if (BiomeDictionary.isBiomeOfType(biome, Type.MUSHROOM))
			{
				i += 2;
			}
			else if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
			{
				i += 1;
			}

			if (random.nextInt(3) <= i)
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 16) + 4;
				z = worldZ + random.nextInt(16) + 8;

				decorator.mushroomBrownGen.generate(worldObj, random, x, y, z);
			}

			if (random.nextInt(8) <= i)
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 16) + 4;
				z = worldZ + random.nextInt(16) + 8;

				decorator.mushroomRedGen.generate(worldObj, random, x, y, z);
			}
		}

		if (decorator.generateLakes && TerrainGen.decorate(worldObj, random, worldX, worldZ, Decorate.EventType.LAKE))
		{
			if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
			{
				for (i = 0; i < 40; ++i)
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight - 12) + 10;
					z = worldZ + random.nextInt(16) + 8;

					liquidLavaGen.generate(worldObj, random, x, y, z);
				}
			}
			else if (BiomeDictionary.isBiomeOfType(biome, Type.WATER))
			{
				for (i = 0; i < 65; ++i)
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(random.nextInt(worldHeight - 16) + 10);
					z = worldZ + random.nextInt(16) + 8;

					liquidWaterGen.generate(worldObj, random, x, y, z);
				}
			}
			else if (!BiomeDictionary.isBiomeOfType(biome, Type.END))
			{
				for (i = 0; i < 50; ++i)
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(random.nextInt(worldHeight - 16) + 10);
					z = worldZ + random.nextInt(16) + 8;

					liquidWaterGen.generate(worldObj, random, x, y, z);
				}

				for (i = 0; i < 20; ++i)
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight / 2);
					z = worldZ + random.nextInt(16) + 8;

					liquidLavaGen.generate(worldObj, random, x, y, z);
				}
			}
		}

		if (decorateVines && (BiomeDictionary.isBiomeOfType(biome, Type.FOREST) || BiomeDictionary.isBiomeOfType(biome, Type.MOUNTAIN)) && random.nextInt(6) == 0)
		{
			for (i = 0; i < 50; ++i)
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 40) + 40;
				z = worldZ + random.nextInt(16) + 8;

				vinesGen.generate(worldObj, random, x, y, z);
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(worldObj, random, worldX, worldZ));
		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider, worldObj, random, chunkX, chunkZ, false));

		BlockFalling.fallInstantly = false;
	}

	@Override
	public boolean chunkExists(int chunkX, int chunkZ)
	{
		return true;
	}

	@Override
	public boolean saveChunks(boolean flag, IProgressUpdate progress)
	{
		return true;
	}

	@Override
	public void saveExtraData() {}

	@Override
	public boolean unloadQueuedChunks()
	{
		return false;
	}

	@Override
	public boolean canSave()
	{
		return true;
	}

	@Override
	public String makeString()
	{
		return "CaveworldRandomLevelSource";
	}

	@Override
	public List getPossibleCreatures(EnumCreatureType creature, int x, int y, int z)
	{
		if (y <= 0 || y >= worldObj.getActualHeight())
		{
			return null;
		}

		if (creature == caveMonster)
		{
			return CaveEntityRegistry.spawns;
		}

		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x, z);

		return biome == null ? null : biome.getSpawnableList(creature);
	}

	@Override
	public ChunkPosition func_147416_a(World world, String name, int x, int y, int z)
	{
		if (Strings.isNullOrEmpty(name))
		{
			return null;
		}

		switch (name)
		{
			case "Mineshaft":
				return mineshaftGenerator.func_151545_a(world, x, y, z);
			case "Stronghold":
				return strongholdGenerator.func_151545_a(world, x, y, z);
			default:
				return null;
		}
	}

	@Override
	public int getLoadedChunkCount()
	{
		return 0;
	}

	@Override
	public void recreateStructures(int chunkX, int chunkZ)
	{
		if (generateStructures)
		{
			if (generateMineshaft)
			{
				mineshaftGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, null);
			}

			if (generateStronghold)
			{
				strongholdGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, null);
			}
		}
	}
}