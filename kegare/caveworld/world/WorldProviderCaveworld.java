package kegare.caveworld.world;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Random;

import kegare.caveworld.core.Caveworld;
import kegare.caveworld.renderer.EmptyRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WorldProviderCaveworld extends WorldProvider
{
	public static long dimensionSeed;

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerCaveworld(worldObj);
		dimensionId = Caveworld.dimensionCaveworld;
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
		return 256.0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored()
	{
		return false;
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
			Random random = new Random();
			long seed = Long.reverse(worldObj.getWorldInfo().getSeed());

			try
			{
				File root = DimensionManager.getCurrentSaveRootDirectory();
				File dir = new File(root, getSaveFolder());

				if (!dir.exists())
				{
					dir.mkdirs();
				}

				File seedFile = new File(dir, "caveworld.txt");

				if (!seedFile.exists())
				{
					PrintWriter writer = new PrintWriter(seedFile);

					writer.print(random.nextLong());
					writer.close();
					writer.flush();
				}

				BufferedReader reader = new BufferedReader(new FileReader(seedFile));

				seed = Long.valueOf(reader.readLine()).longValue();
				reader.close();
			}
			catch (Exception e)
			{
				return seed;
			}

			dimensionSeed = seed;
		}

		return dimensionSeed;
	}

	@Override
	public int getActualHeight()
	{
		return 256;
	}

	@Override
	public double getHorizon()
	{
		return 255.0D;
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