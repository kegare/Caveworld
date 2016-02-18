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
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;

public class WorldProviderCavenia extends WorldProviderCaveworld
{
	public static final int TYPE = 4;

	private static NBTTagCompound dimData;
	private static long dimensionSeed;

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

		File dir = new File(root, new WorldProviderCavenia().getSaveFolder());

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
			File file = new File(dir, "cavenia.dat");

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
				CaveLog.log(Level.ERROR, e, "An error occurred trying to reading Cavenia dimension data");

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

		try (FileOutputStream output = new FileOutputStream(new File(dir, "cavenia.dat")))
		{
			CompressedStreamTools.writeCompressed(getDimData(), output);
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to writing Cavenia dimension data");
		}
	}

	public static void loadDimData(NBTTagCompound data)
	{
		if (!data.hasKey("Seed"))
		{
			data.setLong("Seed", new SecureRandom().nextLong());
		}

		dimensionSeed = data.getLong("Seed");
	}

	public static void saveDimData()
	{
		if (dimData != null)
		{
			writeDimData();

			dimData = null;
		}
	}

	public WorldProviderCavenia()
	{
		this.dimensionId = CaveworldAPI.getCaveniaDimension();
		this.hasNoSky = true;
	}

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.deepOcean, 0.0F);
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderCavenia(worldObj);
	}

	@Override
	public String getDimensionName()
	{
		return "Cavenia";
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
		return 100;
	}

	@Override
	public void calculateInitialWeather()
	{
		if (!worldObj.isRemote)
		{
			musicTime = 300;
		}

		worldObj.prevRainingStrength = 0.0F;
		worldObj.rainingStrength = 0.0F;
		worldObj.prevThunderingStrength = 0.0F;
		worldObj.thunderingStrength = 0.0F;
	}

	@Override
	public void updateWeather()
	{
		if (!worldObj.isRemote)
		{
			if (--musicTime <= 0)
			{
				musicTime = 300;

				CaveNetworkRegistry.sendToDimension(new CaveMusicMessage("cavemusic.battle" + (worldObj.rand.nextInt(2) + 1), false), dimensionId);
			}
		}

		worldObj.prevRainingStrength = 0.0F;
		worldObj.rainingStrength = 0.0F;
		worldObj.prevThunderingStrength = 0.0F;
		worldObj.thunderingStrength = 0.0F;
	}
}