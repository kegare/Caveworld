package kegare.caveworld.world;

import com.google.common.io.Files;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kegare.caveworld.core.Config;
import kegare.caveworld.renderer.EmptyRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.DimensionManager;

import java.io.DataOutputStream;
import java.io.File;
import java.util.Properties;
import java.util.Random;

public class WorldProviderCaveworld extends WorldProvider
{
	private static Properties worldData;

	public static long dimensionSeed;
	public static int subsurfaceHeight;

	public static Properties getWorldData()
	{
		if (worldData == null)
		{
			worldData = new Properties();

			try
			{
				File dir = new File(DimensionManager.getCurrentSaveRootDirectory(), "DIM-Caveworld");

				if (!dir.exists())
				{
					dir.mkdirs();
				}

				File file = new File(dir, "caveworld.xml");

				if (file.exists() && file.canRead())
				{
					worldData.loadFromXML(Files.newInputStreamSupplier(file).getInput());
				}
			}
			catch (Exception ignored) {}
		}

		return worldData;
	}

	public static void saveWorldData()
	{
		if (worldData != null && !worldData.isEmpty())
		{
			try
			{
				File dir = new File(DimensionManager.getCurrentSaveRootDirectory(), "DIM-Caveworld");

				if (!dir.exists())
				{
					dir.mkdirs();
				}

				File file = new File(dir, "caveworld.xml");
				DataOutputStream dos = new DataOutputStream(Files.newOutputStreamSupplier(file).getOutput());

				worldData.storeToXML(dos, null);

				dos.close();
				dos.flush();
			}
			catch (Exception ignored) {}
		}
	}

	public static void clearWorldData()
	{
		saveWorldData();

		worldData = null;
		dimensionSeed = 0;
		subsurfaceHeight = 0;

		WorldChunkManagerCaveworld.biomeMap.clear();
	}

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerCaveworld(worldObj);
		dimensionId = Config.dimensionCaveworld;
		hasNoSky = true;
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderCaveworld(worldObj);
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z)
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float[] calcSunriseSunsetColors(float angle, float ticks)
	{
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 getFogColor(float angle, float ticks)
	{
		return worldObj.getWorldVec3Pool().getVecFromPool(0.01D, 0.01D, 0.01D);
	}

	@Override
	public boolean canRespawnHere()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getCloudHeight()
	{
		return (float)getActualHeight();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored()
	{
		return false;
	}

	@Override
	public int getAverageGroundLevel()
	{
		return 4;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean getWorldHasVoidParticles()
	{
		return terrainType != WorldType.FLAT;
	}

	@Override
	public String getDimensionName()
	{
		return "Caveworld";
	}

	@Override
	public String getSaveFolder()
	{
		return "DIM-Caveworld";
	}

	@Override
	public String getWelcomeMessage()
	{
		return "Entering the Caveworld";
	}

	@Override
	public String getDepartMessage()
	{
		return "Leaving the Caveworld";
	}

	@Override
	public double getMovementFactor()
	{
		return 3.0D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer()
	{
		return new EmptyRenderer();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getCloudRenderer()
	{
		return new EmptyRenderer();
	}

	@Override
	public boolean shouldMapSpin(String entity, double posX, double posY, double posZ)
	{
		return false;
	}

	@Override
	public boolean isDaytime()
	{
		return worldObj.getWorldTime() <= 12500;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 getSkyColor(Entity entity, float ticks)
	{
		return worldObj.getWorldVec3Pool().getVecFromPool(0.01D, 0.01D, 0.01D);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 drawClouds(float ticks)
	{
		return worldObj.getWorldVec3Pool().getVecFromPool(0.01D, 0.01D, 0.01D);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getStarBrightness(float ticks)
	{
		return 0.0F;
	}

	@Override
	public void calculateInitialWeather() {}

	@Override
	public void updateWeather() {}

	@Override
	public long getSeed()
	{
		if (!worldObj.isRemote && dimensionSeed == 0)
		{
			Properties data = getWorldData();
			String key = "DimSeed";

			if (!data.containsKey(key))
			{
				data.setProperty(key, String.valueOf((new Random()).nextLong()));
			}

			dimensionSeed = Long.valueOf(data.getProperty(key, String.valueOf(Long.reverseBytes(super.getSeed()))));
		}

		return dimensionSeed;
	}

	@Override
	public int getActualHeight()
	{
		if (!worldObj.isRemote && subsurfaceHeight == 0)
		{
			Properties data = getWorldData();
			String key = "SubsurfaceHeight";

			if (!data.containsKey(key))
			{
				data.setProperty(key, String.valueOf(Config.subsurfaceHeight));
			}

			subsurfaceHeight = Math.min(Math.max(Integer.valueOf(data.getProperty(key, String.valueOf(Config.subsurfaceHeight))), 63), 255);
		}

		return subsurfaceHeight + 1;
	}

	@Override
	public double getHorizon()
	{
		return (double)getActualHeight() - 1.0D;
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