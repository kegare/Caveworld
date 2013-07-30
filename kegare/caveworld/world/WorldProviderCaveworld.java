package kegare.caveworld.world;

import kegare.caveworld.core.Config;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WorldProviderCaveworld extends WorldProvider
{
	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerCaveworld(new BiomeGenCaveworld(Config.biomeCaveworld));
		dimensionId = Config.dimensionCaveworld;
		hasNoSky = true;
	}

	@Override
	public String getDimensionName()
	{
		return "Caveworld";
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
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderCaveworld(worldObj, worldObj.getSeed());
	}

	@Override
	public boolean isSurfaceWorld()
	{
		return false;
	}

	@Override
	public boolean canRespawnHere()
	{
		return false;
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z)
	{
		return false;
	}

	@Override
	public boolean isDaytime()
	{
		return false;
	}

	@Override
	public boolean shouldMapSpin(String entity, double posX, double posY, double posZ)
	{
		return true;
	}

	@Override
	public long getSeed()
	{
		return Long.reverse(super.getSeed());
	}

	@Override
	public double getHorizon()
	{
		return 0.0D;
	}

	@Override
	public int getAverageGroundLevel()
	{
		return 64;
	}

	@Override
	public void updateWeather()
	{
		//NOOP
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

	@Override
	public float calculateCelestialAngle(long time, float ticks)
	{
		return 0.0F;
	}

	@Override
	public int getMoonPhase(long time)
	{
		return 0;
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
		return worldObj.getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3 getSkyColor(Entity entity, float ticks)
	{
		return worldObj.getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3 drawClouds(float ticks)
	{
		return worldObj.getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getStarBrightness(float ticks)
	{
		return 0.0F;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isSkyColored()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean getWorldHasVoidParticles()
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean doesXZShowFog(int x, int z)
	{
		return true;
	}
}