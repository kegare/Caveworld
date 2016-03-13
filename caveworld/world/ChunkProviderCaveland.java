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

import caveworld.block.CaveBlocks;
import caveworld.entity.CaveEntityRegistry;
import caveworld.world.gen.MapGenCavelandCaves;
import caveworld.world.gen.MapGenCavelandRavine;
import caveworld.world.gen.WorldGenAnimalDungeons;
import caveworld.world.gen.WorldGenPervertedForest;
import caveworld.world.gen.WorldGenPervertedTaiga;
import caveworld.world.gen.WorldGenPervertedTrees;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
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

public class ChunkProviderCaveland implements IChunkProvider
{
	public static int dimensionId;
	public static int subsurfaceHeight;
	public static boolean generateLakes;
	public static boolean generateAnimalDungeons;
	public static boolean decorateVines;
	public static int caveMonsterSpawn;
	public static float caveBrightness;

	public static EnumCreatureType caveMonster;

	private final World worldObj;
	private final Random random;

	private BiomeGenBase[] biomesForGeneration;

	private final MapGenBase caveGenerator = new MapGenCavelandCaves();
	private final MapGenBase ravineGenerator = new MapGenCavelandRavine();

	private final WorldGenerator lakeWaterGen = new WorldGenLakes(Blocks.water);
	private final WorldGenerator lakeLavaGen = new WorldGenLakes(Blocks.lava);
	private final WorldGenerator animalDungeonGen = new WorldGenAnimalDungeons();
	private final WorldGenerator liquidWaterGen = new WorldGenLiquids(Blocks.flowing_water);
	private final WorldGenerator liquidLavaGen = new WorldGenLiquids(Blocks.flowing_lava);
	private final WorldGenerator deadBushGen = new WorldGenDeadBush(Blocks.deadbush);
	private final WorldGenerator vinesGen = new WorldGenVines();
	private final WorldGenFlowers acresiaGen = new WorldGenFlowers(CaveBlocks.acresia_crops);
	private final WorldGenAbstractTree treesGen = new WorldGenPervertedTrees(false);
	private final WorldGenAbstractTree treesGen2 = new WorldGenPervertedTaiga(false);
	private final WorldGenAbstractTree treesGen3 = new WorldGenPervertedForest(false);

	public ChunkProviderCaveland(World world)
	{
		this.worldObj = world;
		this.random = new Random(world.getSeed());
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
			blocks[i] = Blocks.dirt;
			metadata[i] = (byte)0;
		}

		caveGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
		ravineGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);

		int i;

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				i = (x * 16 + z) * 256;

				BiomeGenBase biome = biomesForGeneration[x * 16 + z];
				Block top = biome.topBlock;
				int topMeta = biome.field_150604_aj;
				Block filler = biome.fillerBlock;
				int fillerMeta = biome.field_76754_C;

				if (filler == Blocks.sand)
				{
					filler = Blocks.sandstone;
					fillerMeta = 0;
				}

				blocks[i] = Blocks.bedrock;
				blocks[i + 1] = blocks[i + 2];
				blocks[i + worldHeight - 1] = Blocks.bedrock;
				blocks[i + worldHeight - 2] = filler;
				metadata[i + worldHeight - 2] = (byte)fillerMeta;

				for (int y = 1; y < worldHeight - 3; ++y)
				{
					if (blocks[i + y] == Blocks.dirt)
					{
						blocks[i + y] = filler;
						metadata[i + y] = (byte)fillerMeta;
					}

					if (blocks[i + y] == Blocks.grass)
					{
						blocks[i + y] = top;
						metadata[i + y] = (byte)topMeta;
					}

					if (blocks[i + y] != null && blocks[i + y].getMaterial().isSolid() && blocks[i + y + 1] == null)
					{
						blocks[i + y] = top;
						metadata[i + y] = (byte)topMeta;
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

		int x, y, z, i;

		if (generateLakes)
		{
			if (TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAKE))
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 16);
				z = worldZ + random.nextInt(16) + 8;

				lakeWaterGen.generate(worldObj, random, x, y, z);
			}

			if (random.nextInt(30) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAVA))
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight / 2);
				z = worldZ + random.nextInt(16) + 8;

				lakeLavaGen.generate(worldObj, random, x, y, z);
			}
		}

		if (generateAnimalDungeons && random.nextInt(3) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
		{
			x = worldX + random.nextInt(16) + 8;
			y = random.nextInt(worldHeight - 24);
			z = worldZ + random.nextInt(16) + 8;

			animalDungeonGen.generate(worldObj, random, x, y, z);
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(worldObj, random, worldX, worldZ));
		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, random, worldX, worldZ));

		if (decorator.gravelGen != null)
		{
			for (i = 0; i < 10; ++i)
			{
				x = worldX + random.nextInt(16);
				y = random.nextInt(worldHeight - 2);
				z = worldZ + random.nextInt(16);

				decorator.gravelGen.generate(worldObj, random, x, y, z);
			}
		}

		if (decorator.coalGen != null)
		{
			for (i = 0; i < 20; ++i)
			{
				x = worldX + random.nextInt(16);
				y = random.nextInt(worldHeight - 2);
				z = worldZ + random.nextInt(16);

				decorator.coalGen.generate(worldObj, random, x, y, z);
			}

			for (i = 0; i < 15; ++i)
			{
				x = worldX + random.nextInt(16);
				y = random.nextInt(10);
				z = worldZ + random.nextInt(16);

				decorator.coalGen.generate(worldObj, random, x, y, z);
			}
		}

		if (decorator.ironGen != null)
		{
			for (i = 0; i < 20; ++i)
			{
				x = worldX + random.nextInt(16);
				y = random.nextInt(worldHeight - 2);
				z = worldZ + random.nextInt(16);

				decorator.ironGen.generate(worldObj, random, x, y, z);
			}

			for (i = 0; i < 15; ++i)
			{
				x = worldX + random.nextInt(16);
				y = random.nextInt(10);
				z = worldZ + random.nextInt(16);

				decorator.ironGen.generate(worldObj, random, x, y, z);
			}
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(worldObj, random, worldX, worldZ));

		if (TerrainGen.decorate(worldObj, random, worldX, worldZ, Decorate.EventType.SHROOM))
		{
			for (i = 0; i < 5; ++i)
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 10);
				z = worldZ + random.nextInt(16) + 8;

				decorator.mushroomBrownGen.generate(worldObj, random, x, y, z);
			}

			for (i = 0; i < 5; ++i)
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 10);
				z = worldZ + random.nextInt(16) + 8;

				decorator.mushroomRedGen.generate(worldObj, random, x, y, z);
			}
		}

		for (i = 0; i < 10; ++i)
		{
			x = worldX + random.nextInt(16) + 8;
			y = random.nextInt(worldHeight - 5);
			z = worldZ + random.nextInt(16) + 8;

			acresiaGen.func_150550_a(CaveBlocks.acresia_crops, 2 + random.nextInt(3));
			acresiaGen.generate(worldObj, random, x, y, z);
		}

		for (i = 0; i < 15; ++i)
		{
			x = worldX + random.nextInt(16) + 8;
			y = random.nextInt(worldHeight / 2 - 5) + worldHeight / 2;
			z = worldZ + random.nextInt(16) + 8;

			acresiaGen.func_150550_a(CaveBlocks.acresia_crops, 3 + random.nextInt(2));
			acresiaGen.generate(worldObj, random, x, y, z);
		}

		if (BiomeDictionary.isBiomeOfType(biome, Type.SANDY))
		{
			if (TerrainGen.decorate(worldObj, random, worldX, worldZ, Decorate.EventType.CACTUS))
			{
				for (i = 0; i < 80; ++i)
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight - 5);
					z = worldZ + random.nextInt(16) + 8;

					decorator.cactusGen.generate(worldObj, random, x, y, z);
				}
			}

			if (TerrainGen.decorate(worldObj, random, worldX, worldZ, Decorate.EventType.DEAD_BUSH))
			{
				for (i = 0; i < 10; ++i)
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight - 5);
					z = worldZ + random.nextInt(16) + 8;

					deadBushGen.generate(worldObj, random, x, y, z);
				}
			}
		}
		else
		{
			if (TerrainGen.decorate(worldObj, random, worldX, worldZ, Decorate.EventType.FLOWERS))
			{
				for (i = 0; i < 8; ++i)
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(worldHeight - 5);
					z = worldZ + random.nextInt(16) + 8;

					decorator.yellowFlowerGen.generate(worldObj, random, x, y, z);
				}
			}

			for (i = 0; i < 18; ++i)
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 5);
				z = worldZ + random.nextInt(16) + 8;

				biome.getRandomWorldGenForGrass(random).generate(worldObj, random, x, y, z);
			}

			if (TerrainGen.decorate(worldObj, random, worldX, worldZ, Decorate.EventType.TREE))
			{
				if (BiomeDictionary.isBiomeOfType(biome, Type.JUNGLE))
				{
					WorldGenAbstractTree worldGen = new WorldGenPervertedTrees(false, 4 + random.nextInt(7), 3, 3, false);
					worldGen.setScale(1.0D, 1.0D, 1.0D);

					for (i = 0; i < 80; ++i)
					{
						x = worldX + random.nextInt(16) + 8;
						y = random.nextInt(worldHeight);
						z = worldZ + random.nextInt(16) + 8;

						if (worldGen.generate(worldObj, random, x, y, z))
						{
							worldGen.func_150524_b(worldObj, random, x, y, z);
						}
					}

					for (i = 0; i < 60; ++i)
					{
						x = worldX + random.nextInt(16) + 8;
						y = 8 + random.nextInt(5);
						z = worldZ + random.nextInt(16) + 8;

						if (worldGen.generate(worldObj, random, x, y, z))
						{
							worldGen.func_150524_b(worldObj, random, x, y, z);
						}
					}
				}
				else
				{
					for (i = 0; i < 80; ++i)
					{
						x = worldX + random.nextInt(16) + 8;
						y = random.nextInt(worldHeight);
						z = worldZ + random.nextInt(16) + 8;

						WorldGenAbstractTree worldGen;

						if (random.nextInt(5) == 0)
						{
							worldGen = treesGen3;
						}
						else if (random.nextInt(3) == 0)
						{
							worldGen = treesGen2;
						}
						else
						{
							worldGen = treesGen;
						}

						worldGen.setScale(1.0D, 1.0D, 1.0D);

						if (worldGen.generate(worldObj, random, x, y, z))
						{
							worldGen.func_150524_b(worldObj, random, x, y, z);
						}
					}

					for (i = 0; i < 60; ++i)
					{
						x = worldX + random.nextInt(16) + 8;
						y = 8 + random.nextInt(5);
						z = worldZ + random.nextInt(16) + 8;

						WorldGenAbstractTree worldGen;

						if (random.nextInt(5) == 0)
						{
							worldGen = treesGen3;
						}
						else if (random.nextInt(3) == 0)
						{
							worldGen = treesGen2;
						}
						else
						{
							worldGen = treesGen;
						}

						worldGen.setScale(1.0D, 1.0D, 1.0D);

						if (worldGen.generate(worldObj, random, x, y, z))
						{
							worldGen.func_150524_b(worldObj, random, x, y, z);
						}
					}
				}
			}
		}

		if (decorator.generateLakes && TerrainGen.decorate(worldObj, random, worldX, worldZ, Decorate.EventType.LAKE))
		{
			if (BiomeDictionary.isBiomeOfType(biome, Type.WATER))
			{
				for (i = 0; i < 150; ++i)
				{
					x = worldX + random.nextInt(16) + 8;
					y = random.nextInt(random.nextInt(worldHeight - 16) + 10);
					z = worldZ + random.nextInt(16) + 8;

					liquidWaterGen.generate(worldObj, random, x, y, z);
				}
			}
			else
			{
				for (i = 0; i < 100; ++i)
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

		if (decorateVines && (BiomeDictionary.isBiomeOfType(biome, Type.FOREST) || BiomeDictionary.isBiomeOfType(biome, Type.MOUNTAIN)) && random.nextInt(3) == 0)
		{
			for (i = 0; i < 50; ++i)
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 30) + 30;
				z = worldZ + random.nextInt(16) + 8;

				vinesGen.generate(worldObj, random, x, y, z);
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(worldObj, random, worldX, worldZ));

		if (TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.ANIMALS))
		{
			performWorldGenSpawning(worldObj, biome, worldX + 8, worldZ + 8, 16, 16, random);
		}

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
		return "CavelandRandomLevelSource";
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
		return null;
	}

	@Override
	public int getLoadedChunkCount()
	{
		return 0;
	}

	@Override
	public void recreateStructures(int chunkX, int chunkZ) {}

	public static void performWorldGenSpawning(World world, BiomeGenBase biome, int worldX, int worldZ, int xScale, int zScale, Random random)
	{
		List list = biome.getSpawnableList(EnumCreatureType.creature);

		if (list != null && !list.isEmpty())
		{
			float chance = MathHelper.clamp_float(biome.getSpawningChance() * 2.5F, 0.0F, 1.0F);

			outside: while (random.nextFloat() < chance)
			{
				SpawnListEntry entry = (SpawnListEntry)WeightedRandom.getRandomItem(world.rand, list);
				IEntityLivingData data = null;
				int i = entry.minGroupCount + random.nextInt(1 + entry.maxGroupCount - entry.minGroupCount);
				int x = worldX + random.nextInt(xScale);
				int z = worldZ + random.nextInt(zScale);
				int j = x;
				int k = z;

				for (int l = 0; l < i; ++l)
				{
					boolean flag = false;

					for (int m = 0; !flag && m < 4; ++m)
					{
						int y = 0;

						do
						{
							++y;
						}
						while (!world.isAirBlock(x, y, z));

						if (y >= world.getActualHeight())
						{
							continue outside;
						}

						if (SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.creature, world, x, y, z))
						{
							float posX = x + 0.5F;
							float posY = y;
							float posZ = z + 0.5F;
							EntityLiving entityliving;

							try
							{
								entityliving = (EntityLiving)entry.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
							}
							catch (Exception e)
							{
								e.printStackTrace();

								continue;
							}

							entityliving.setLocationAndAngles(posX, posY, posZ, random.nextFloat() * 360.0F, 0.0F);
							world.spawnEntityInWorld(entityliving);
							data = entityliving.onSpawnWithEgg(data);
							flag = true;
						}

						x += random.nextInt(5) - random.nextInt(5);

						for (z += random.nextInt(5) - random.nextInt(5); x < worldX || x >= worldX + xScale || z < worldZ || z >= worldZ + xScale; z = k + random.nextInt(5) - random.nextInt(5))
						{
							x = j + random.nextInt(5) - random.nextInt(5);
						}
					}
				}
			}
		}
	}
}