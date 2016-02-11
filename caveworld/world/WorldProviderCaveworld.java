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
import caveworld.client.renderer.EmptyRenderer;
import caveworld.core.Caveworld;
import caveworld.network.client.CaveMusicMessage;
import caveworld.network.client.CaveworldAdjustMessage;
import caveworld.util.CaveLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.DimensionManager;

public class WorldProviderCaveworld extends WorldProviderSurface
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

		File dir = new File(root, new WorldProviderCaveworld().getSaveFolder());

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
			data.setInteger("SubsurfaceHeight", ChunkProviderCaveworld.subsurfaceHeight);
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

	protected int ambientTickCountdown = 0;

	public WorldProviderCaveworld()
	{
		this.dimensionId = CaveworldAPI.getDimension();
		this.hasNoSky = true;
	}

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerCaveworld(worldObj, CaveworldAPI.biomeManager);
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderCaveworld(worldObj);
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float[] calcSunriseSunsetColors(float angle, float ticks)
	{
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3 getFogColor(float angle, float ticks)
	{
		return Vec3.createVectorHelper(0.01D, 0.01D, 0.01D);
	}

	@Override
	public int getAverageGroundLevel()
	{
		return 10;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean getWorldHasVoidParticles()
	{
		return false;
	}

	@Override
	public String getDimensionName()
	{
		return "Caveworld";
	}

	@Override
	public String getSaveFolder()
	{
		return "DIM-" + getDimensionName();
	}

	@Override
	public String getWelcomeMessage()
	{
		return "Entering the " + getDimensionName();
	}

	@Override
	public String getDepartMessage()
	{
		return "Leaving the " + getDimensionName();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getSkyRenderer()
	{
		if (super.getSkyRenderer() == null)
		{
			setSkyRenderer(EmptyRenderer.instance);
		}

		return super.getSkyRenderer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getCloudRenderer()
	{
		if (super.getCloudRenderer() == null)
		{
			setCloudRenderer(EmptyRenderer.instance);
		}

		return super.getCloudRenderer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getWeatherRenderer()
	{
		if (super.getWeatherRenderer() == null)
		{
			setWeatherRenderer(EmptyRenderer.instance);
		}

		return super.getWeatherRenderer();
	}

	@Override
	public boolean shouldMapSpin(String entity, double posX, double posY, double posZ)
	{
		return posY < 0 || posY >= getActualHeight();
	}

	@Override
	public ChunkCoordinates getSpawnPoint()
	{
		return new ChunkCoordinates(0, 50, 0);
	}

	@Override
	public ChunkCoordinates getRandomizedSpawnPoint()
	{
		return getSpawnPoint();
	}

	@Override
	public boolean isDaytime()
	{
		return false;
	}

	@Override
	public void calculateInitialWeather()
	{
		if (!worldObj.isRemote)
		{
			ambientTickCountdown = worldObj.rand.nextInt(4000) + 8000;
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
			if (--ambientTickCountdown <= 0)
			{
				ambientTickCountdown = worldObj.rand.nextInt(5000) + 10000;

				Caveworld.network.sendToDimension(new CaveMusicMessage(worldObj.rand.nextInt(3) == 0 ? "cavemusic.cave" : "cavemusic.unrest"), dimensionId);
			}
		}

		worldObj.prevRainingStrength = 0.0F;
		worldObj.rainingStrength = 0.0F;
		worldObj.prevThunderingStrength = 0.0F;
		worldObj.thunderingStrength = 0.0F;
	}

	@Override
	public long getSeed()
	{
		if (!worldObj.isRemote && dimData == null)
		{
			loadDimData(getDimData());

			Caveworld.network.sendToAll(new CaveworldAdjustMessage(dimensionId, getDimData()));
		}

		return dimensionSeed;
	}

	@Override
	public int getActualHeight()
	{
		if (!worldObj.isRemote && dimData == null)
		{
			loadDimData(getDimData());

			Caveworld.network.sendToAll(new CaveworldAdjustMessage(dimensionId, getDimData()));
		}

		return subsurfaceHeight + 1;
	}

	@Override
	public double getHorizon()
	{
		return getActualHeight();
	}

	@Override
	public boolean canDoLightning(Chunk chunk)
	{
		return false;
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk)
	{
		return false;
	}
}