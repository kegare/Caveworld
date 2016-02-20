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

import caveworld.api.BlockEntry;
import caveworld.block.CaveBlocks;
import caveworld.core.CaveVeinManager.CaveVein;
import caveworld.entity.CaveEntityRegistry;
import caveworld.entity.EntityCrazyCavenicSkeleton;
import caveworld.world.gen.MapGenCaveniaCaves;
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

public class ChunkProviderCavenia implements IChunkProvider
{
	public static int dimensionId;
	public static int caveMonsterSpawn;

	public static EnumCreatureType caveMonster;

	private final World worldObj;
	private final Random random;

	private final MapGenBase caveGenerator = new MapGenCaveniaCaves();

	public final CaveVein veinRandomite = new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 2), 30, 100, 100, 3, 95);

	public ChunkProviderCavenia(World world)
	{
		this.worldObj = world;
		this.random = new Random(world.getSeed());
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);

		Block[] blocks = new Block[65536];
		byte[] metadata = new byte[blocks.length];

		for (int i = 0; i < blocks.length; ++i)
		{
			blocks[i] = Blocks.stone;
			metadata[i] = (byte)0;
		}

		int size = 3;

		if (chunkX <= size && chunkX >= -size && chunkZ <= size && chunkZ >= -size)
		{
			caveGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
		}

		int i;

		++size;

		if (chunkX <= size && chunkX >= -size && chunkZ <= size && chunkZ >= -size)
		{
			for (int x = 0; x < 16; ++x)
			{
				for (int z = 0; z < 16; ++z)
				{
					i = (x * 16 + z) * 256;

					blocks[i] = Blocks.bedrock;
					blocks[i + 100] = Blocks.bedrock;
					blocks[i + 99] = Blocks.stone;

					for (int y = 255; y > 100; --y)
					{
						blocks[i + y] = null;
					}
				}
			}
		}
		else for (i = 0; i < blocks.length; ++i)
		{
			blocks[i] = Blocks.bedrock;
		}

		Chunk chunk = new Chunk(worldObj, blocks, metadata, chunkX, chunkZ);
		Arrays.fill(chunk.getBiomeArray(), (byte)BiomeGenBase.deepOcean.biomeID);

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

		veinRandomite.generateVeins(worldObj, random, worldX, worldZ);

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(worldObj, random, worldX, worldZ));

		if (chunkX == 0 && chunkZ == 0)
		{
			int y = 0;

			do
			{
				++y;
			}
			while (!worldObj.isAirBlock(0, y, 0));

			EntityCrazyCavenicSkeleton boss = new EntityCrazyCavenicSkeleton(worldObj);
			boss.setLocationAndAngles(0.5D, y + 0.5D, 0.5D, random.nextFloat() * 360.0F, 0.0F);
			boss.onSpawnWithEgg(null);
			worldObj.spawnEntityInWorld(boss);
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
		return "CaveniaRandomLevelSource";
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
}