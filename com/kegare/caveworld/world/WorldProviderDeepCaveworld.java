/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;

import org.apache.logging.log4j.Level;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.util.CaveLog;

public class WorldProviderDeepCaveworld extends WorldProviderCaveworld
{
	private static NBTTagCompound dimData;
	private static long dimensionSeed;
	private static int subsurfaceHeight;

	public static NBTTagCompound getDimData()
	{
		if (dimData == null)
		{
			dimData = readDimData();
		}

		return dimData;
	}

	public static File getDimDir()
	{
		File root = DimensionManager.getCurrentSaveRootDirectory();

		if (root == null || !root.exists() || root.isFile())
		{
			return null;
		}

		File dir = new File(root, new WorldProviderDeepCaveworld().getSaveFolder());

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return dir.isDirectory() ? dir : null;
	}

	private static NBTTagCompound readDimData()
	{
		NBTTagCompound data;
		File dir = getDimDir();

		if (dir == null)
		{
			data = null;
		}
		else
		{
			File file = new File(dir, "caveworld.dat");

			if (!file.exists() || !file.isFile() || !file.canRead())
			{
				data = null;
			}
			else try (FileInputStream input = new FileInputStream(file))
			{
				data = CompressedStreamTools.readCompressed(input);
			}
			catch (Exception e)
			{
				CaveLog.log(Level.ERROR, e, "An error occurred trying to reading Caveworld dimension data");

				data = null;
			}
		}

		return data == null ? new NBTTagCompound() : data;
	}

	private static void writeDimData()
	{
		File dir = getDimDir();

		if (dir == null)
		{
			return;
		}

		try (FileOutputStream output = new FileOutputStream(new File(dir, "caveworld.dat")))
		{
			CompressedStreamTools.writeCompressed(getDimData(), output);
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to writing Caveworld dimension data");
		}
	}

	public static void loadDimData(NBTTagCompound data)
	{
		if (!data.hasKey("Seed"))
		{
			data.setLong("Seed", new SecureRandom().nextLong());
		}

		if (!data.hasKey("SubsurfaceHeight"))
		{
			data.setInteger("SubsurfaceHeight", ChunkProviderDeepCaveworld.subsurfaceHeight);
		}

		dimensionSeed = data.getLong("Seed");
		subsurfaceHeight = data.getInteger("SubsurfaceHeight");
	}

	public static void saveDimData()
	{
		if (dimData != null)
		{
			writeDimData();

			dimData = null;
		}
	}

	public WorldProviderDeepCaveworld()
	{
		this.dimensionId = CaveworldAPI.getDeepDimension();
		this.hasNoSky = true;
	}

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerCaveworld(worldObj, ChunkProviderDeepCaveworld.biomeSize, CaveworldAPI.biomeDeepManager);
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderDeepCaveworld(worldObj);
	}

	@Override
	public String getDimensionName()
	{
		return "Deep Caveworld";
	}

	@Override
	public long getSeed()
	{
		if (!worldObj.isRemote && dimData == null)
		{
			loadDimData(getDimData());
		}

		return dimensionSeed;
	}

	@Override
	public int getActualHeight()
	{
		if (!worldObj.isRemote && dimData == null)
		{
			loadDimData(getDimData());
		}

		return subsurfaceHeight + 1;
	}
}