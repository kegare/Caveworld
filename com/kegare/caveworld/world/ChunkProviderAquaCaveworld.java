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
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

import com.google.common.base.Strings;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.api.ICaveVein;
import com.kegare.caveworld.world.gen.MapGenAquaCaves;
import com.kegare.caveworld.world.gen.MapGenAquaRavine;
import com.kegare.caveworld.world.gen.MapGenStrongholdCaveworld;
import com.kegare.caveworld.world.gen.WorldGenAnimalDungeons;
import com.kegare.caveworld.world.gen.WorldGenDungeonsCaveworld;

public class ChunkProviderAquaCaveworld implements IChunkProvider
{
	public static int dimensionId;
	public static int subsurfaceHeight;
	public static int biomeSize;
	public static boolean generateRavine;
	public static boolean generateMineshaft;
	public static boolean generateStronghold;
	public static boolean generateDungeons;
	public static boolean generateAnimalDungeons;
	public static boolean aquaLivingAssist;

	private final World worldObj;
	private final Random random;
	private final boolean generateStructures;

	private final MapGenBase caveGenerator = new MapGenAquaCaves();
	private final MapGenBase ravineGenerator = new MapGenAquaRavine();
	private final MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
	private final MapGenStronghold strongholdGenerator = new MapGenStrongholdCaveworld();

	private final WorldGenerator dungeonGen = new WorldGenDungeonsCaveworld();
	private final WorldGenerator animalDungeonGen = new WorldGenAnimalDungeons();

	public ChunkProviderAquaCaveworld(World world)
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
		ICaveBiome entry = CaveworldAPI.getCaveAquaBiome(biome);
		Block block = entry.getTerrainBlock().getBlock();
		int meta = entry.getTerrainBlock().getMetadata();

		Arrays.fill(blocks, block);
		Arrays.fill(metadata, (byte)meta);

		caveGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);

		if (generateRavine)
		{
			ravineGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
		}

		int i;

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				i = (x * 16 + z) * 256;

				blocks[i] = Blocks.bedrock;
				blocks[i + worldHeight - 1] = Blocks.bedrock;
				blocks[i + worldHeight - 2] = block;

				if (!entry.getTerrainBlock().equals(entry.getTopBlock()))
				{
					for (int y = 1; y < worldHeight - 4; ++y)
					{
						if (blocks[i + y] != null && blocks[i + y].getMaterial().isSolid() && blocks[i + y + 1] == null)
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

		for (i = 0; i < blocks.length; ++i)
		{
			if (blocks[i] == null)
			{
				blocks[i] = Blocks.water;
			}
			else if (blocks[i] != block && meta != 0)
			{
				metadata[i] = 0;
			}
		}

		Chunk chunk = new Chunk(worldObj, blocks, metadata, chunkX, chunkZ);
		Arrays.fill(chunk.getBiomeArray(), (byte)biome.biomeID);

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

		if (!BiomeDictionary.isBiomeOfType(biome, Type.END))
		{
			if (generateDungeons && generateStructures && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
			{
				for (i = 0; i < 12; ++i)
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight - 50) + 20;
					z = worldZ + random.nextInt(16) + 8;

					dungeonGen.generate(worldObj, random, x, y, z);
				}
			}

			if (generateAnimalDungeons && random.nextInt(5) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 50) + 20;
				z = worldZ + random.nextInt(16) + 8;

				animalDungeonGen.generate(worldObj, random, x, y, z);
			}
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, random, worldX, worldZ));

		for (ICaveVein vein : CaveworldAPI.getCaveAquaVeins())
		{
			vein.generateVein(worldObj, random, worldX, worldZ);
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(worldObj, random, worldX, worldZ));
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