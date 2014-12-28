/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
import net.minecraft.world.gen.MapGenBase;
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

import com.google.common.base.Strings;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.api.ICaveVein;
import com.kegare.caveworld.world.gen.MapGenCavesCaveworld;
import com.kegare.caveworld.world.gen.MapGenExtremeCaves;
import com.kegare.caveworld.world.gen.MapGenExtremeRavine;
import com.kegare.caveworld.world.gen.MapGenRavineCaveworld;
import com.kegare.caveworld.world.gen.MapGenStrongholdCaveworld;
import com.kegare.caveworld.world.gen.MapGenUnderCaves;
import com.kegare.caveworld.world.gen.WorldGenAnimalDungeons;
import com.kegare.caveworld.world.gen.WorldGenDungeonsCaveworld;

public class ChunkProviderCaveworld implements IChunkProvider
{
	public static int dimensionId;
	public static int subsurfaceHeight;
	public static int biomeSize;
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
	public static boolean underPeaceful;

	private final World worldObj;
	private final Random random;
	private final boolean generateStructures;

	private final MapGenBase caveGenerator = new MapGenCavesCaveworld(false);
	private final MapGenBase ravineGenerator = new MapGenRavineCaveworld(false);
	private final MapGenBase underCaveGenerator = new MapGenUnderCaves(false);
	private final MapGenBase extremeCaveGenerator = new MapGenExtremeCaves(false);
	private final MapGenBase extremeRavineGenerator = new MapGenExtremeRavine(false);
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
	private final MapGenStronghold strongholdGenerator = new MapGenStrongholdCaveworld();

	private final WorldGenerator lakeWaterGen = new WorldGenLakes(Blocks.water);
	private final WorldGenerator lakeLavaGen = new WorldGenLakes(Blocks.lava);
	private final WorldGenerator dungeonGen = new WorldGenDungeonsCaveworld();
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

		int worldHeight = worldObj.provider.getActualHeight();
		BiomeGenBase biome = worldObj.getWorldChunkManager().getBiomeGenAt(chunkX * 16, chunkZ * 16);
		Block[] blocks = new Block[65536];
		byte[] metadata = new byte[65536];
		ICaveBiome entry = CaveworldAPI.getCaveBiome(biome);
		Block block = entry.getTerrainBlock().getBlock();
		int meta = entry.getTerrainBlock().getMetadata();

		Arrays.fill(blocks, block);
		Arrays.fill(metadata, (byte)meta);

		if (generateCaves)
		{
			caveGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (generateRavine)
		{
			ravineGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (generateUnderCaves)
		{
			underCaveGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (generateExtremeCaves && worldHeight > 150)
		{
			extremeCaveGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (generateExtremeRavine)
		{
			extremeRavineGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
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

				blocks[i + worldHeight - 1] = Blocks.bedrock;
				blocks[i + worldHeight - 2] = block;

				if (!entry.getTerrainBlock().equals(entry.getTopBlock()))
				{
					for (int y = 1; y < worldHeight - 4; ++y)
					{
						if (blocks[i + y] != null && blocks[i + y + 1] == null)
						{
							blocks[i + y] = entry.getTopBlock().getBlock();
							metadata[i + y] = (byte)entry.getTopBlock().getMetadata();
						}
					}
				}

				for (int y = 255; y >= worldHeight; --y)
				{
					blocks[i + y] = null;
				}
			}
		}

		for (i = 0; meta != 0 && i < blocks.length; ++i)
		{
			if (blocks[i] != block)
			{
				metadata[i] = 0;
			}
		}

		Chunk chunk = new Chunk(worldObj, blocks, metadata, chunkX, chunkZ);
		byte[] biomes = new byte[256];

		Arrays.fill(biomes, (byte)biome.biomeID);

		chunk.setBiomeArray(biomes);
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
		long worldSeed = worldObj.getSeed();
		random.setSeed(worldSeed);
		long xSeed = random.nextLong() >> 2 + 1L;
		long zSeed = random.nextLong() >> 2 + 1L;
		random.setSeed(chunkX * xSeed + chunkZ * zSeed ^ worldSeed);

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
				for (i = 0; i < 12; ++i)
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight - 24);
					z = worldZ + random.nextInt(16) + 8;

					dungeonGen.generate(worldObj, random, x, y, z);
				}
			}

			if (generateAnimalDungeons && random.nextInt(5) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
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
			vein.generateVein(worldObj, random, worldX, worldZ);
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
		if (y <= 0 || y >= worldObj.getActualHeight() || underPeaceful && creature == EnumCreatureType.monster && y < 64)
		{
			return null;
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