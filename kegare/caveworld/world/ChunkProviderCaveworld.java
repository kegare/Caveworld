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
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
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

	private BiomeGenBase[] biomesForGeneration;

	private MapGenBase caveGenerator = new MapGenCaves();
	private MapGenBase ravineGenerator = new MapGenRavine();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();

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

		MinecraftForge.TERRAIN_GEN_BUS.register(this);
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		random.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);

		int worldHeight = worldObj.provider.getActualHeight();
		byte[] blocks = new byte[256 * worldHeight];

		Arrays.fill(blocks, (byte)Block.stone.blockID);

		biomesForGeneration = worldObj.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);

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
		byte[] biomes = chunk.getBiomeArray();

		for (int index = 0; index < biomes.length; ++index)
		{
			biomes[index] = (byte)biomesForGeneration[index].biomeID;
		}

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				chunk.setBlockIDWithMetadata(x, 0, z, Block.bedrock.blockID, 0);
				chunk.setBlockIDWithMetadata(x, worldHeight - 1, z, Block.bedrock.blockID, 0);
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

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider, worldObj, random, chunkX, chunkZ, false));

		int x = chunkX << 4;
		int z = chunkZ << 4;
		BiomeGenBase biome = worldObj.getWorldChunkManager().getBiomeGenAt(x + 16, z + 16);
		BiomeDecorator decorator = biome.createBiomeDecorator();
		long worldSeed = worldObj.getSeed();
		int worldHeight = worldObj.provider.getActualHeight();
		random.setSeed(worldSeed);
		long xSeed = random.nextLong() >> 2 + 1L;
		long zSeed = random.nextLong() >> 2 + 1L;
		random.setSeed((xSeed * chunkX + zSeed * chunkZ) ^ worldSeed);

		int var1, var2, var3, i;

		if (Config.generateMineshaft && generateStructures)
		{
			mineshaftGenerator.generateStructuresInChunk(worldObj, random, chunkX, chunkZ);
		}

		if (Config.generateLakes && !BiomeDictionary.isBiomeOfType(biome, Type.DESERT) && random.nextInt(4) == 0 && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAKE))
		{
			var1 = x + random.nextInt(16) + 8;
			var2 = random.nextInt(worldHeight - 32);
			var3 = z + random.nextInt(16) + 8;

			(new WorldGenLakes(Block.waterStill.blockID)).generate(worldObj, random, var1, var2, var3);
		}

		if (Config.generateDungeon && generateStructures && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
		{
			for (i = 0; i < 8; ++i)
			{
				var1 = x + random.nextInt(16) + 8;
				var2 = random.nextInt(worldHeight - 24);
				var3 = z + random.nextInt(16) + 8;

				(new WorldGenDungeons()).generate(worldObj, random, var1, var2, var3);
			}
		}

		biome.decorate(worldObj, random, x, z);

		if (worldHeight > 64 && TerrainGen.decorate(worldObj, random, chunkX, chunkZ, Decorate.EventType.SHROOM))
		{
			if (random.nextInt(3) == 0)
			{
				var1 = x + random.nextInt(16);
				var2 = random.nextInt(192) + 64;
				var3 = z + random.nextInt(16);

				decorator.mushroomBrownGen.generate(worldObj, random, var1, var2, var3);
			}

			if (random.nextInt(8) == 0)
			{
				var1 = x + random.nextInt(16);
				var2 = random.nextInt(192) + 64;
				var3 = z + random.nextInt(16);

				decorator.mushroomRedGen.generate(worldObj, random, var1, var2, var3);
			}
		}

		if (TerrainGen.generateOre(worldObj, random, decorator.coalGen, chunkX, chunkZ, GenerateMinable.EventType.COAL))
		{
			for (i = 0; i < 30; ++i)
			{
				var1 = x + random.nextInt(16);
				var2 = random.nextInt(worldHeight - 4) + 4;
				var3 = z + random.nextInt(16);

				decorator.coalGen.generate(worldObj, random, var1, var2, var3);
			}
		}

		if (TerrainGen.generateOre(worldObj, random, decorator.ironGen, chunkX, chunkZ, GenerateMinable.EventType.IRON))
		{
			for (i = 0; i < 30; ++i)
			{
				var1 = x + random.nextInt(16);
				var2 = random.nextInt(worldHeight - 4) + 4;
				var3 = z + random.nextInt(16);

				decorator.ironGen.generate(worldObj, random, var1, var2, var3);
			}
		}

		if (TerrainGen.generateOre(worldObj, random, decorator.goldGen, chunkX, chunkZ, GenerateMinable.EventType.GOLD))
		{
			for (i = 0; i < 8; ++i)
			{
				var1 = x + random.nextInt(16);
				var2 = random.nextInt(worldHeight - 4) + 4;
				var3 = z + random.nextInt(16);

				decorator.goldGen.generate(worldObj, random, var1, var2, var3);
			}
		}

		if (worldHeight > 128)
		{
			if (TerrainGen.generateOre(worldObj, random, decorator.dirtGen, chunkX, chunkZ, GenerateMinable.EventType.DIRT))
			{
				for (i = 0; i < 20; ++i)
				{
					var1 = x + random.nextInt(16);
					var2 = random.nextInt(128) + 128;
					var3 = z + random.nextInt(16);

					decorator.dirtGen.generate(worldObj, random, var1, var2, var3);
				}
			}

			if (TerrainGen.generateOre(worldObj, random, decorator.gravelGen, chunkX, chunkZ, GenerateMinable.EventType.GRAVEL))
			{
				for (i = 0; i < 15; ++i)
				{
					var1 = x + random.nextInt(16);
					var2 = random.nextInt(128) + 128;
					var3 = z + random.nextInt(16);

					decorator.gravelGen.generate(worldObj, random, var1, var2, var3);
				}
			}

			for (i = 0; i < 6; ++i)
			{
				var1 = x + random.nextInt(16);
				var2 = random.nextInt(128) + 128;
				var3 = z + random.nextInt(16);

				(new WorldGenMinable(Block.oreEmerald.blockID, 8)).generate(worldObj, random, var1, var2, var3);
			}
		}

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider, worldObj, random, chunkX, chunkZ, false));

		BlockSand.fallInstantly = false;
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

	@ForgeSubscribe
	public void onDecorate(Decorate event)
	{
		World world = event.world;
		Decorate.EventType type = event.type;

		if (world.provider.dimensionId == Config.dimensionCaveworld)
		{
			if (type == Decorate.EventType.BIG_SHROOM)
			{
				event.setResult(Result.DENY);
			}
			else if (type == Decorate.EventType.TREE)
			{
				event.setResult(Result.DENY);
			}
		}
	}
}