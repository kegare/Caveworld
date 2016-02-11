/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveBiome;
import caveworld.api.ICaveVein;
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

	private final World worldObj;
	private final Random random;

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

		int worldHeight = worldObj.provider.getActualHeight();
		BiomeGenBase biome = worldObj.getWorldChunkManager().getBiomeGenAt(chunkX * 16, chunkZ * 16);
		Block[] blocks = new Block[65536];
		byte[] metadata = new byte[blocks.length];
		ICaveBiome entry = CaveworldAPI.getAquaCavernBiome(biome);
		Block block = entry.getTerrainBlock().getBlock();
		int meta = entry.getTerrainBlock().getMetadata();

		for (int i = 0; i < blocks.length; ++i)
		{
			blocks[i] = block;
			metadata[i] = (byte)meta;
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

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider, worldObj, random, chunkX, chunkZ, false));
		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, random, worldX, worldZ));

		for (ICaveVein vein : CaveworldAPI.getAquaCavernVeins())
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
		return "AquaCavernRandomLevelSource";
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