package kegare.caveworld.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import kegare.caveworld.core.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkProviderCaveworld implements IChunkProvider
{
	private final World worldObj;
	private final Random random;
	private final boolean mapFeaturesEnabled;

	private MapGenBase caveGenerator = new MapGenCaves();
	private MapGenBase ravineGenerator = new MapGenRavine();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();

	{
		caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, InitMapGenEvent.EventType.CAVE);
		ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, InitMapGenEvent.EventType.RAVINE);
		mineshaftGenerator = (MapGenMineshaft)TerrainGen.getModdedMapGen(mineshaftGenerator, InitMapGenEvent.EventType.MINESHAFT);
	}

	public ChunkProviderCaveworld(World world, long seed)
	{
		this.worldObj = world;
		this.random = new Random(seed);
		this.mapFeaturesEnabled = world.getWorldInfo().isMapFeaturesEnabled();
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		random.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);

		byte[] blocks = new byte[32768];
		Arrays.fill(blocks, (byte)Block.stone.blockID);

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				for (int y = 127; y >= 0; --y)
				{
					int index = (z * 16 + x) * 128 + y;

					if (y <= 0 || y >= 127)
					{
						blocks[index] = (byte)Block.bedrock.blockID;
					}
					else
					{
						blocks[index] = (byte)Block.stone.blockID;
					}
				}
			}
		}

		if (Config.generateCaves)
		{
			caveGenerator.generate(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (Config.generateRavine)
		{
			ravineGenerator.generate(this, worldObj, chunkX, chunkZ, blocks);
		}

		if (mapFeaturesEnabled && Config.generateMineshaft)
		{
			mineshaftGenerator.generate(this, worldObj, chunkX, chunkZ, blocks);
		}

		return new Chunk(worldObj, blocks, chunkX, chunkZ);
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

		int chunk_X = chunkX * 16;
		int chunk_Z = chunkZ * 16;
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(chunk_X + 16, chunk_Z + 16);
		random.setSeed(worldObj.getSeed());
		long var1 = random.nextLong() / 2L * 2L + 1L;
		long var2 = random.nextLong() / 2L * 2L + 1L;
		random.setSeed((long)chunkX * var1 + (long)chunkZ * var2 ^ worldObj.getSeed());

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider, worldObj, random, chunkX, chunkZ, false));

		if (mapFeaturesEnabled && Config.generateMineshaft)
		{
			mineshaftGenerator.generateStructuresInChunk(worldObj, random, chunkX, chunkZ);
		}

		if (Config.generateLakes)
		{
			if (TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAKE) && random.nextInt(4) == 0)
			{
				int x = chunk_X + random.nextInt(16) + 8;
				int y = random.nextInt(112);
				int z = chunk_Z + random.nextInt(16) + 8;

				(new WorldGenLakes(Block.waterStill.blockID)).generate(worldObj, random, x, y, z);
			}

			if (TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.LAVA) && random.nextInt(8) == 0)
			{
				int x = chunk_X + random.nextInt(16) + 8;
				int y = random.nextInt(random.nextInt(120) + 8);
				int z = chunk_Z + random.nextInt(16) + 8;

				if (y < 63 || random.nextInt(10) == 0)
				{
					(new WorldGenLakes(Block.lavaStill.blockID)).generate(worldObj, random, x, y, z);
				}
			}
		}

		if (Config.generateDungeon && TerrainGen.populate(chunkProvider, worldObj, random, chunkX, chunkZ, false, EventType.DUNGEON))
		{
			for (int i = 0; i < 8; ++i)
			{
				int x = chunk_X + random.nextInt(16) + 8;
				int y = random.nextInt(112);
				int z = chunk_Z + random.nextInt(16) + 8;

				(new WorldGenDungeons()).generate(worldObj, random, x, y, z);
			}
		}

		biome.decorate(worldObj, random, chunk_X, chunk_Z);

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
	public ChunkPosition findClosestStructure(World world, String name, int x, int y, int z)
	{
		return name.equals("Mineshaft") && mineshaftGenerator != null ? mineshaftGenerator.getNearestInstance(world, x, y, z) : null;
	}

	@Override
	public int getLoadedChunkCount()
	{
		return 0;
	}

	@Override
	public void recreateStructures(int chunkX, int chunkZ)
	{
		if (mapFeaturesEnabled && Config.generateMineshaft)
		{
			mineshaftGenerator.generate(this, worldObj, chunkX, chunkZ, (byte[])null);
		}
	}

	@Override
	public void func_104112_b() {}
}