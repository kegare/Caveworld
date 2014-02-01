package kegare.caveworld.world;

import kegare.caveworld.core.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChunkProviderCaveworld implements IChunkProvider
{
	private final World worldObj;
	private final Random random;
	private final boolean generateStructures;

	private MapGenBase caveGenerator = new MapGenCaves();
	private MapGenBase ravineGenerator = new MapGenRavine();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();

	private final WorldGenerator lakeWaterGen = new WorldGenLakes(Block.waterStill.blockID);
	private final WorldGenerator lakeLavaGen = new WorldGenLakes(Block.lavaStill.blockID);
	private final WorldGenerator dungeonGen = new WorldGenDungeons();
	private final WorldGenerator coalGen = new WorldGenMinable(Block.oreCoal.blockID, 15);
	private final WorldGenerator ironGen = new WorldGenMinable(Block.oreIron.blockID, 10);
	private final WorldGenerator goldGen = new WorldGenMinable(Block.oreGold.blockID, 8);
	private final WorldGenerator redstoneGen = new WorldGenMinable(Block.oreRedstone.blockID, 8);
	private final WorldGenerator lapisGen = new WorldGenMinable(Block.oreLapis.blockID, 5);
	private final WorldGenerator diamondGen = new WorldGenMinable(Block.oreDiamond.blockID, 8);
	private final WorldGenerator emeraldGen = new WorldGenMinable(Block.oreEmerald.blockID, 8);
	private final WorldGenerator liquidWaterGen = new WorldGenLiquids(Block.waterMoving.blockID);
	private final WorldGenerator liquidLavaGen = new WorldGenLiquids(Block.lavaMoving.blockID);
	private final WorldGenerator vinesGen = new WorldGenVines();

	{
		caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, InitMapGenEvent.EventType.CAVE);
		ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, InitMapGenEvent.EventType.RAVINE);
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
		random.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);

		int worldHeight = worldObj.provider.getActualHeight();
		byte[] blocks = new byte[256 * Math.max(worldHeight, 128)];

		Arrays.fill(blocks, (byte)Block.stone.blockID);

		if (Config.generateCaves)
		{
			caveGenerator.generate(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (Config.generateRavine)
		{
			ravineGenerator.generate(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (Config.generateMineshaft && generateStructures)
		{
			mineshaftGenerator.generate(this, worldObj, chunkX, chunkZ, blocks);
		}

		Chunk chunk = new Chunk(worldObj, blocks, chunkX, chunkZ);

		Arrays.fill(chunk.getBiomeArray(), (byte)worldObj.getWorldChunkManager().getBiomeGenAt(chunkX << 4, chunkZ << 4).biomeID);

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				chunk.setBlockIDWithMetadata(x, 0, z, Block.bedrock.blockID, 0);
				chunk.setBlockIDWithMetadata(x, worldHeight - 1, z, Block.bedrock.blockID, 0);
				chunk.setBlockIDWithMetadata(x, worldHeight - 2, z, Block.stone.blockID, 0);

				for (int y = worldHeight; worldHeight < 128 && y < 128; ++y)
				{
					chunk.setBlockIDWithMetadata(x, y, z, 0, 0);
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
		BlockSand.fallInstantly = true;

		int worldX = chunkX << 4;
		int worldZ = chunkZ << 4;
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(worldX + 16, worldZ + 16);
		BiomeDecorator decorator = biome.theBiomeDecorator;
		long worldSeed = worldObj.getSeed();
		int worldHeight = worldObj.provider.getActualHeight();
		random.setSeed(worldSeed);
		long xSeed = random.nextLong() >> 2 + 1L;
		long zSeed = random.nextLong() >> 2 + 1L;
		random.setSeed(chunkX * xSeed + chunkZ * zSeed ^ worldSeed);

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider, worldObj, random, chunkX, chunkZ, false));

		if (Config.generateMineshaft && generateStructures)
		{
			mineshaftGenerator.generateStructuresInChunk(worldObj, random, chunkX, chunkZ);
		}

		int i, x, y, z;

		if (Config.generateLakes)
		{
			if (random.nextInt(4) == 0 && !BiomeDictionary.isBiomeOfType(biome, Type.DESERT) && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAKE))
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

		if (Config.generateDungeon && generateStructures && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
		{
			for (i = 0; i < 8; ++i)
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 24);
				z = worldZ + random.nextInt(16) + 8;

				dungeonGen.generate(worldObj, random, x, y, z);
			}
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, random, worldX, worldZ));

		generateOre(Config.genRateDirt, decorator.dirtGen, worldX, worldZ, 0, worldHeight, GenerateMinable.EventType.DIRT);
		generateOre(Config.genRateGravel, decorator.gravelGen, worldX, worldZ, 0, worldHeight, GenerateMinable.EventType.GRAVEL);
		generateOre(Config.genRateCoal, coalGen, worldX, worldZ, 0, worldHeight, GenerateMinable.EventType.COAL);
		generateOre(Config.genRateIron, ironGen, worldX, worldZ, 0, worldHeight, GenerateMinable.EventType.IRON);
		generateOre(Config.genRateGold, goldGen, worldX, worldZ, 0, worldHeight / 2, GenerateMinable.EventType.GOLD);
		generateOre(Config.genRateRedstone, redstoneGen, worldX, worldZ, 0, worldHeight / 4, GenerateMinable.EventType.REDSTONE);
		generateOre(Config.genRateLapis, lapisGen, worldX, worldZ, 0, 32, GenerateMinable.EventType.LAPIS);
		generateOre(Config.genRateDiamond, diamondGen, worldX, worldZ, 0, 16, GenerateMinable.EventType.DIAMOND);
		generateOre(Config.genRateEmerald, emeraldGen, worldX, worldZ, worldHeight - (worldHeight / 3), worldHeight, GenerateMinable.EventType.CUSTOM);

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(worldObj, random, worldX, worldZ));
		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(worldObj, random, worldX, worldZ));

		if (TerrainGen.decorate(worldObj, random, worldX, worldZ, Decorate.EventType.SHROOM))
		{
			if (random.nextInt(3) <= (BiomeDictionary.isBiomeOfType(biome, Type.MUSHROOM) ? 1 : 0))
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 16) + 4;
				z = worldZ + random.nextInt(16) + 8;

				decorator.mushroomBrownGen.generate(worldObj, random, x, y, z);
			}

			if (random.nextInt(8) <= (BiomeDictionary.isBiomeOfType(biome, Type.MUSHROOM) ? 2 : 0))
			{
				x = worldX + random.nextInt(16) + 8;
				y = random.nextInt(worldHeight - 16) + 4;
				z = worldZ + random.nextInt(16) + 8;

				decorator.mushroomRedGen.generate(worldObj, random, x, y, z);
			}
		}

		if (TerrainGen.decorate(worldObj, random, worldX, worldZ, Decorate.EventType.LAKE))
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

		if (Config.decorateVines && BiomeDictionary.isBiomeOfType(biome, Type.FOREST) && random.nextInt(10) == 0)
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
		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider, worldObj, random, chunkX, chunkZ, false));

		BlockSand.fallInstantly = false;
	}

	private void generateOre(int count, WorldGenerator worldGenerator, int worldX, int worldZ, int minY, int maxY, GenerateMinable.EventType type)
	{
		if (count > 0 && TerrainGen.generateOre(worldObj, random, worldGenerator, worldX, worldZ, type))
		{
			int i, x, y, z;

			for (i = 0; i < count; ++i)
			{
				x = worldX + random.nextInt(16);
				y = random.nextInt(maxY - minY) + minY;
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
		BiomeGenBase biome = worldObj.getWorldChunkManager().getBiomeGenAt(x, z);

		return biome == null ? null : biome.getSpawnableList(creature);
	}

	@Override
	public ChunkPosition findClosestStructure(World world, String name, int x, int y, int z)
	{
		return "Mineshaft".equals(name) ? mineshaftGenerator.getNearestInstance(world, x, y, z) : null;
	}

	@Override
	public int getLoadedChunkCount()
	{
		return 0;
	}

	@Override
	public void recreateStructures(int chunkX, int chunkZ)
	{
		if (Config.generateMineshaft && generateStructures)
		{
			mineshaftGenerator.generate(this, worldObj, chunkX, chunkZ, null);
		}
	}
}