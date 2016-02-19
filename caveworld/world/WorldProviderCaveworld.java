/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveBiomeManager;
import caveworld.client.renderer.EmptyRenderer;
import caveworld.core.CaveNetworkRegistry;
import caveworld.network.client.CaveAdjustMessage;
import caveworld.network.client.CaveMusicMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

public class WorldProviderCaveworld extends WorldProviderSurface
{
	public static final int TYPE = 0;
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler("Caveworld");

	protected int musicTime = 0;

	public WorldProviderCaveworld()
	{
		this.dimensionId = CaveworldAPI.getDimension();
		this.hasNoSky = true;
	}

	public ICaveBiomeManager getBiomeManager()
	{
		return CaveworldAPI.biomeManager;
	}

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerCaveLegacy(worldObj, 1, getBiomeManager());
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
			musicTime = worldObj.rand.nextInt(4000) + 8000;
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
				musicTime = worldObj.rand.nextInt(5000) + 10000;

				CaveNetworkRegistry.sendToDimension(new CaveMusicMessage(worldObj.rand.nextInt(3) == 0 ? "cavemusic.cave" : "cavemusic.unrest"), dimensionId);
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
		if (!worldObj.isRemote && saveHandler.getRawData() == null)
		{
			CaveNetworkRegistry.sendToAll(new CaveAdjustMessage(TYPE, dimensionId, saveHandler));
		}

		return saveHandler.getWorldSeed();
	}

	@Override
	public int getActualHeight()
	{
		if (!worldObj.isRemote && saveHandler.getRawData() == null)
		{
			CaveNetworkRegistry.sendToAll(new CaveAdjustMessage(TYPE, dimensionId, saveHandler));
		}

		return saveHandler.getSubsurfaceHeight() + 1;
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