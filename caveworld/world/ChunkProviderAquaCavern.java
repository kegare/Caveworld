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

import com.google.common.collect.Lists;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveBiome;
import caveworld.api.ICaveVein;
import caveworld.entity.CaveEntityRegistry;
import caveworld.world.gen.MapGenAquaCaves;
import caveworld.world.gen.MapGenAquaRavine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

public class ChunkProviderAquaCavern implements IChunkProvider
{
	public static int dimensionId;
	public static int subsurfaceHeight;
	public static boolean generateRavine;
	public static int caveMonsterSpawn;
	public static float caveBrightness;

	public static EnumCreatureType caveMonster;
	public static EnumCreatureType waterMob;

	public static final List<SpawnListEntry> waterSpawns = Lists.newArrayList();

	private final World worldObj;
	private final Random random;

	private BiomeGenBase[] biomesForGeneration;

	private final MapGenBase caveGenerator = new MapGenAquaCaves();
	private final MapGenBase ravineGenerator = new MapGenAquaRavine();

	public ChunkProviderAquaCavern(World world)
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
			blocks[i] = Blocks.stone;
			metadata[i] = (byte)0;
		}

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

				BiomeGenBase biome = biomesForGeneration[x * 16 + z];
				ICaveBiome caveBiome = CaveworldAPI.getAquaCavernBiome(biome);
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

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider, worldObj, random, chunkX, chunkZ, false));
		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, random, worldX, worldZ));

		for (ICaveVein vein : CaveworldAPI.getAquaCavernVeins())
		{
			vein.generateVeins(worldObj, random, worldX, worldZ);
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
		return "AquaCavernRandomLevelSource";
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
		else if (creature == waterMob)
		{
			return waterSpawns;
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