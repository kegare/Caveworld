/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.world;

import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.renderer.EmptyRenderer;
import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.world.gen.MapGenStrongholdCaveworld;
import com.kegare.caveworld.world.gen.StructureStrongholdPiecesCaveworld;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.security.SecureRandom;

public class WorldProviderCaveworld extends WorldProvider
{
	public static long dimensionSeed;
	public static int subsurfaceHeight;

	private static NBTTagCompound dimData;

	public static NBTTagCompound getDimData()
	{
		if (dimData == null)
		{
			readDimData();
		}

		return dimData;
	}

	static void readDimData()
	{
		try
		{
			File dir = new File(DimensionManager.getCurrentSaveRootDirectory(), "DIM-Caveworld");

			if (!dir.exists())
			{
				dir.mkdirs();
			}

			dimData = CompressedStreamTools.read(new File(dir, "caveworld.dat"));
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to reading Caveworld dimension data");
		}
		finally
		{
			if (dimData == null)
			{
				dimData = new NBTTagCompound();
			}
		}
	}

	public static void writeDimData()
	{
		try
		{
			File dir = new File(DimensionManager.getCurrentSaveRootDirectory(), "DIM-Caveworld");

			if (!dir.exists())
			{
				dir.mkdirs();
			}

			CompressedStreamTools.write(getDimData(), new File(dir, "caveworld.dat"));
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to writing Caveworld dimension data");
		}
	}

	public static void clearDimData()
	{
		dimensionSeed = 0;
		subsurfaceHeight = 0;
		dimData = null;

		WorldChunkManagerCaveworld.biomeMap.clear();
	}

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerCaveworld(worldObj);
		dimensionId = Config.dimensionCaveworld;
		hasNoSky = true;

		MapGenStructureIO.registerStructure(MapGenStrongholdCaveworld.Start.class, "Caveworld.Stronghold");
		StructureStrongholdPiecesCaveworld.registerStrongholdPieces();
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
		return getActualHeight();
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
		return 10;
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
	public int getRespawnDimension(EntityPlayerMP player)
	{
		if (player.getBedLocation(dimensionId) != null)
		{
			return dimensionId;
		}

		return super.getRespawnDimension(player);
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
			try
			{
				NBTTagCompound data = getDimData();

				if (!data.hasKey("DimSeed"))
				{
					data.setLong("DimSeed", new SecureRandom().nextLong());
				}

				dimensionSeed = data.getLong("DimSeed");
			}
			finally
			{
				if (dimensionSeed == 0)
				{
					dimensionSeed = Long.reverseBytes(super.getSeed());
				}
			}
		}

		return dimensionSeed;
	}

	@Override
	public int getActualHeight()
	{
		if (!worldObj.isRemote && subsurfaceHeight == 0)
		{
			NBTTagCompound data = getDimData();

			if (!data.hasKey("SubsurfaceHeight"))
			{
				data.setInteger("SubsurfaceHeight", Config.subsurfaceHeight);
			}

			subsurfaceHeight = MathHelper.clamp_int(data.getInteger("SubsurfaceHeight"), 63, 255);
		}

		return subsurfaceHeight + 1;
	}

	@Override
	public double getHorizon()
	{
		return getActualHeight() - 1.0D;
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