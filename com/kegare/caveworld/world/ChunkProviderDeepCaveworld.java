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
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

import org.apache.commons.lang3.ArrayUtils;

import com.bioxx.tfc.Chunkdata.ChunkData;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.WorldGen.DataLayer;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.api.ICaveVein;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.world.gen.MapGenDeepCaves;
import com.kegare.caveworld.world.gen.MapGenUnderCaves;

import cpw.mods.fml.common.Optional.Method;

public class ChunkProviderDeepCaveworld implements IChunkProvider
{
	private final World worldObj;
	private final Random random;

	private final MapGenBase caveGenerator = new MapGenDeepCaves();
	private final MapGenBase underCaveGenerator = new MapGenUnderCaves();

	private final WorldGenerator lakeWaterGen = new WorldGenLakes(Blocks.water);
	private final WorldGenerator lakeLavaGen = new WorldGenLakes(Blocks.lava);
	private final WorldGenerator liquidWaterGen = new WorldGenLiquids(Blocks.flowing_water);
	private final WorldGenerator liquidLavaGen = new WorldGenLiquids(Blocks.flowing_lava);
	private final WorldGenerator vinesGen = new WorldGenVines();

	public ChunkProviderDeepCaveworld(World world)
	{
		this.worldObj = world;
		this.random = new Random(world.getSeed());
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

		if (Config.generateDeepCaves)
		{
			caveGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (Config.generateUnderCaves)
		{
			underCaveGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
		}

		int i;
		boolean flag = random.nextInt(100) == 0;

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				i = (x * 16 + z) * 256;

				blocks[i] = Blocks.bedrock;
				blocks[i + worldHeight - 1] = block;

				if (!entry.getTerrainBlock().equals(entry.getTopBlock()))
				{
					for (int y = 1; y < worldHeight - 2; ++y)
					{
						if (blocks[i + y] != null)
						{
							if (blocks[i + y + 1] == null)
							{
								blocks[i + y] = entry.getTopBlock().getBlock();
								metadata[i + y] = (byte)entry.getTopBlock().getMetadata();
							}
							else if (blocks[i + y - 1] == null && flag && random.nextInt(30) == 0)
							{
								blocks[i + y] = Blocks.web;
								metadata[i + y] = 0;
							}
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

		try
		{
			provideChunkTFC(chunk);
		}
		catch (NoSuchMethodError e) {}

		return chunk;
	}

	@Method(modid = "terrafirmacraft")
	private void provideChunkTFC(Chunk chunk)
	{
		ChunkData data = new ChunkData()
		{
			@Override
			public float getRainfall(int x, int z)
			{
				return 0.0F;
			};
		}.CreateNew(worldObj, chunk.xPosition, chunk.zPosition);

		data.heightmap = chunk.heightMap;
		Arrays.fill(data.rainfallMap, DataLayer.NoTree);

		TFC_Core.getCDM(worldObj).addData(chunk, data);
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

		int i, x, y, z;

		if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
		{
			if (Config.generateLakes && random.nextInt(8) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAVA))
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 16);
				z = worldZ + random.nextInt(16) + 8;

				lakeLavaGen.generate(worldObj, random, x, y, z);
			}
		}
		else if (!BiomeDictionary.isBiomeOfType(biome, Type.END))
		{
			if (Config.generateLakes)
			{
				if (!BiomeDictionary.isBiomeOfType(biome, Type.SANDY) && random.nextInt(8) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAKE))
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight - 16);
					z = worldZ + random.nextInt(16) + 8;

					lakeWaterGen.generate(worldObj, random, x, y, z);
				}

				if (random.nextInt(50) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAVA))
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight / 2);
					z = worldZ + random.nextInt(16) + 8;

					lakeLavaGen.generate(worldObj, random, x, y, z);
				}
			}
		}
		else if (random.nextInt(4) == 0)
		{
			x = worldX + random.nextInt(16) + 8;
			y = random.nextInt(worldHeight - 16);
			z = worldZ + random.nextInt(16) + 8;

			lakeWaterGen.generate(worldObj, random, x, y, z);
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, random, worldX, worldZ));

		for (ICaveVein vein : CaveworldAPI.getCaveVeins())
		{
			generateVein(vein, worldX, worldZ, 16, worldHeight - 1, Math.max(vein.getGenBlockCount() / 3, 1), 0);
			generateVein(vein, worldX, worldZ, 1, 15, 0, Math.max(vein.getGenWeight() / 3, 1));
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(worldObj, random, worldX, worldZ));
		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(worldObj, random, worldX, worldZ));

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

		if (Config.decorateVines && (BiomeDictionary.isBiomeOfType(biome, Type.FOREST) || BiomeDictionary.isBiomeOfType(biome, Type.MOUNTAIN)) && random.nextInt(10) == 0)
		{
			for (i = 0; i < 30; ++i)
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

	private void generateVein(ICaveVein vein, int worldX, int worldZ, int min, int max, int countBonus, int weightBonus)
	{
		int worldHeight = worldObj.getActualHeight();
		BlockEntry block = vein.getBlock();
		int count = vein.getGenBlockCount() + countBonus;
		int weight = vein.getGenWeight() * 2 + weightBonus;
		int rate = vein.getGenRate();
		BlockEntry target = vein.getGenTargetBlock();
		int[] biomes = vein.getGenBiomes();

		if (weight > 0)
		{
			for (int i = 0; i < weight; ++i)
			{
				if (random.nextInt(100) + 1 > rate)
				{
					continue;
				}

				int x = worldX + random.nextInt(16);
				int y = random.nextInt(Math.min(max, worldHeight - 1) - min) + min;
				int z = worldZ + random.nextInt(16);
				float var1 = random.nextFloat() * (float)Math.PI;
				double var2 = x + 8 + MathHelper.sin(var1) * count / 8.0F;
				double var3 = x + 8 - MathHelper.sin(var1) * count / 8.0F;
				double var4 = z + 8 + MathHelper.cos(var1) * count / 8.0F;
				double var5 = z + 8 - MathHelper.cos(var1) * count / 8.0F;
				double var6 = y + random.nextInt(3) - 2;
				double var7 = y + random.nextInt(3) - 2;
				int gen = 0;

				for (int j = 0; gen <= count && j <= count; ++j)
				{
					double var8 = var2 + (var3 - var2) * j / count;
					double var9 = var6 + (var7 - var6) * j / count;
					double var10 = var4 + (var5 - var4) * j / count;
					double var11 = random.nextDouble() * count / 16.0D;
					double var12 = (MathHelper.sin(j * (float)Math.PI / count) + 1.0F) * var11 + 1.0D;
					double var13 = (MathHelper.sin(j * (float)Math.PI / count) + 1.0F) * var11 + 1.0D;

					for (x = MathHelper.floor_double(var8 - var12 / 2.0D); gen <= count && x <= MathHelper.floor_double(var8 + var12 / 2.0D); ++x)
					{
						double xScale = (x + 0.5D - var8) / (var12 / 2.0D);

						if (xScale * xScale < 1.0D)
						{
							for (y = MathHelper.floor_double(var9 - var13 / 2.0D); gen <= count && y <= MathHelper.floor_double(var9 + var13 / 2.0D); ++y)
							{
								double yScale = (y + 0.5D - var9) / (var13 / 2.0D);

								if (xScale * xScale + yScale * yScale < 1.0D)
								{
									for (z = MathHelper.floor_double(var10 - var12 / 2.0D); z <= MathHelper.floor_double(var10 + var12 / 2.0D); ++z)
									{
										double zScale = (z + 0.5D - var10) / (var12 / 2.0D);

										if (xScale * xScale + yScale * yScale + zScale * zScale < 1.0D)
										{
											if (target == null)
											{
												if (!worldObj.getBlock(x, y, z).isReplaceableOreGen(worldObj, x, y, z, Blocks.stone))
												{
													continue;
												}
											}
											else if (!worldObj.getBlock(x, y, z).isReplaceableOreGen(worldObj, x, y, z, target.getBlock()) || worldObj.getBlockMetadata(x, y, z) != target.getMetadata())
											{
												continue;
											}

											if (biomes == null || biomes.length <= 0 || ArrayUtils.contains(biomes, worldObj.getBiomeGenForCoords(x, z).biomeID))
											{
												if (worldObj.setBlock(x, y, z, block.getBlock(), block.getMetadata(), 2))
												{
													++gen;
												}
											}
										}
									}
								}
							}
						}
					}
				}
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
		if (y <= 0 || y >= worldObj.getActualHeight() || creature == EnumCreatureType.monster && y < 64)
		{
			return null;
		}

		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x, z);

		return biome == null ? null : biome.getSpawnableList(creature);
	}

	@Override
	public ChunkPosition func_147416_a(World world, String name, int x, int y, int z)
	{
		return null;
	}

	@Override
	public int getLoadedChunkCount()
	{
		return 0;
	}

	@Override
	public void recreateStructures(int chunkX, int chunkZ) {}
}