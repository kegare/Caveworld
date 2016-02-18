/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;

import org.apache.logging.log4j.Level;

import caveworld.api.CaveworldAPI;
import caveworld.core.CaveNetworkRegistry;
import caveworld.network.client.CaveAdjustMessage;
import caveworld.network.client.CaveMusicMessage;
import caveworld.util.CaveLog;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;

public class WorldProviderCaveland extends WorldProviderCaveworld
{
	public static final int TYPE = 3;

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

		File dir = new File(root, new WorldProviderCaveland().getSaveFolder());

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
			File file = new File(dir, "caveland.dat");

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
				CaveLog.log(Level.ERROR, e, "An error occurred trying to reading Caveland dimension data");

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

		try (FileOutputStream output = new FileOutputStream(new File(dir, "caveland.dat")))
		{
			CompressedStreamTools.writeCompressed(getDimData(), output);
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to writing Caveland dimension data");
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
			data.setInteger("SubsurfaceHeight", ChunkProviderCavern.subsurfaceHeight);
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

	public WorldProviderCaveland()
	{
		this.dimensionId = CaveworldAPI.getCavelandDimension();
		this.hasNoSky = true;
	}

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManager(worldObj);
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderCaveland(worldObj);
	}

	@Override
	public String getDimensionName()
	{
		return "Caveland";
	}

	@Override
	public long getSeed()
	{
		if (!worldObj.isRemote && dimData == null)
		{
			loadDimData(getDimData());

			CaveNetworkRegistry.sendToAll(new CaveAdjustMessage(TYPE, dimensionId, getDimData()));
		}

		return dimensionSeed;
	}

	@Override
	public int getActualHeight()
	{
		if (!worldObj.isRemote && dimData == null)
		{
			loadDimData(getDimData());

			CaveNetworkRegistry.sendToAll(new CaveAdjustMessage(TYPE, dimensionId, getDimData()));
		}

		return subsurfaceHeight + 1;
	}

	@Override
	public void updateWeather()
	{
		if (!worldObj.isRemote)
		{
			if (--musicTime <= 0)
			{
				musicTime = worldObj.rand.nextInt(5000) + 10000;

				CaveNetworkRegistry.sendToDimension(new CaveMusicMessage("cavemusic.hope"), dimensionId);
			}
		}

		worldObj.prevRainingStrength = 0.0F;
		worldObj.rainingStrength = 0.0F;
		worldObj.prevThunderingStrength = 0.0F;
		worldObj.thunderingStrength = 0.0F;
	}
}