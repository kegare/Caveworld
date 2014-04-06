/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
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
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenDungeons;
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

import com.kegare.caveworld.core.CaveBiomeManager;
import com.kegare.caveworld.core.CaveBiomeManager.CaveBiome.BlockEntry;
import com.kegare.caveworld.core.CaveOreManager;
import com.kegare.caveworld.core.CaveOreManager.CaveOre;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.world.gen.MapGenCavesCaveworld;
import com.kegare.caveworld.world.gen.MapGenRavineCaveworld;
import com.kegare.caveworld.world.gen.MapGenStrongholdCaveworld;

public class ChunkProviderCaveworld implements IChunkProvider
{
	private final World worldObj;
	private final Random random;
	private final boolean generateStructures;

	private final MapGenBase caveGenerator = new MapGenCavesCaveworld();
	private final MapGenBase ravineGenerator = new MapGenRavineCaveworld();
	private MapGenStronghold strongholdGenerator = new MapGenStrongholdCaveworld();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();

	private final WorldGenerator lakeWaterGen = new WorldGenLakes(Blocks.water);
	private final WorldGenerator lakeLavaGen = new WorldGenLakes(Blocks.lava);
	private final WorldGenerator dungeonGen = new WorldGenDungeons();
	private final WorldGenerator glowStoneGen = new WorldGenGlowStone1();
	private final WorldGenerator liquidWaterGen = new WorldGenLiquids(Blocks.flowing_water);
	private final WorldGenerator liquidLavaGen = new WorldGenLiquids(Blocks.flowing_lava);
	private final WorldGenerator vinesGen = new WorldGenVines();

	{
		strongholdGenerator = (MapGenStronghold)TerrainGen.getModdedMapGen(strongholdGenerator, InitMapGenEvent.EventType.STRONGHOLD);
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
		BiomeGenBase biome = worldObj.getWorldChunkManager().getBiomeGenAt(chunkX << 4, chunkZ << 4);
		Block[] blocks = new Block[256 * MathHelper.clamp_int(worldHeight, 128, 256)];
		byte[] metadata = new byte[blocks.length];
		BlockEntry entry = CaveBiomeManager.getBiomeTerrainBlock(biome);
		Block block = entry.block;
		int meta = entry.blockMetadata;

		Arrays.fill(blocks, block);
		Arrays.fill(metadata, (byte)meta);

		if (Config.generateCaves) caveGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
		if (Config.generateRavine) ravineGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);

		if (generateStructures)
		{
			if (Config.generateMineshaft) mineshaftGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
			if (Config.generateStronghold) strongholdGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
		}

		int i;

		for (i = 0; meta != 0 && i < blocks.length; ++i)
		{
			if (blocks[i] != block)
			{
				metadata[i] = 0;
			}
		}

		Chunk chunk = new Chunk(worldObj, blocks, metadata, chunkX, chunkZ);

		Arrays.fill(chunk.getBiomeArray(), (byte)biome.biomeID);

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				chunk.func_150807_a(x, 0, z, Blocks.bedrock, 0);
				chunk.func_150807_a(x, worldHeight - 1, z, Blocks.bedrock, 0);
				chunk.func_150807_a(x, worldHeight - 2, z, block, 0);

				for (i = worldHeight; worldHeight < 128 && i < 128; ++i)
				{
					chunk.func_150807_a(x, i, z, Blocks.air, 0);
				}
			}
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

		int worldX = chunkX << 4;
		int worldZ = chunkZ << 4;
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
			if (Config.generateMineshaft) mineshaftGenerator.generateStructuresInChunk(worldObj, random, chunkX, chunkZ);
			if (Config.generateStronghold) strongholdGenerator.generateStructuresInChunk(worldObj, random, chunkX, chunkZ);
		}

		int i, x, y, z;

		if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
		{
			if (Config.generateLakes && random.nextInt(4) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAVA))
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
			if (Config.generateLakes)
			{
				if (!BiomeDictionary.isBiomeOfType(biome, Type.DESERT) && random.nextInt(4) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAKE))
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

			if (Config.generateDungeons && generateStructures && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
			{
				for (i = 0; i < 8; ++i)
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight - 24);
					z = worldZ + random.nextInt(16) + 8;

					dungeonGen.generate(worldObj, random, x, y, z);
				}
			}
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, random, worldX, worldZ));

		for (CaveOre ore : CaveOreManager.getCaveOres())
		{
			generateOre(ore.getGenRarity(), ore, worldX, worldZ, ore.getGenMinHeight(), ore.getGenMaxHeight());
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

		if (Config.decorateVines && (BiomeDictionary.isBiomeOfType(biome, Type.FOREST) || BiomeDictionary.isBiomeOfType(biome, Type.MOUNTAIN)) && random.nextInt(6) == 0)
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

	private void generateOre(int rarity, WorldGenerator worldGenerator, int worldX, int worldZ, int minY, int maxY)
	{
		int worldHeight = worldObj.getActualHeight();

		if (rarity > 0 && minY < worldHeight && minY < maxY)
		{
			int x, y, z;

			for (int i = 0; i < rarity; ++i)
			{
				x = worldX + random.nextInt(16);
				y = random.nextInt(Math.min(maxY, worldHeight - 1) - minY) + minY;
				z = worldZ + random.nextInt(16);

				worldGenerator.generate(worldObj, random, x, y, z);
			}
		}
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
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x, z);

		return biome == null ? null : biome.getSpawnableList(creature);
	}

	@Override
	public ChunkPosition func_147416_a(World world, String name, int x, int y, int z)
	{
		return "Mineshaft".equals(name) ? mineshaftGenerator.func_151545_a(world, x, y, z) : "Stronghold".equals(name) ? strongholdGenerator.func_151545_a(world, x, y, z) : null;
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
			if (Config.generateMineshaft) mineshaftGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, null);
			if (Config.generateStronghold) strongholdGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, null);
		}
	}
}