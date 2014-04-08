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

import java.io.File;
import java.security.SecureRandom;

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

import com.google.common.base.Optional;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.packet.PlayCaveSoundPacket;
import com.kegare.caveworld.renderer.EmptyRenderer;
import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.world.gen.MapGenStrongholdCaveworld;
import com.kegare.caveworld.world.gen.StructureStrongholdPiecesCaveworld;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WorldProviderCaveworld extends WorldProvider
{
	private static Optional<NBTTagCompound> dimData = Optional.absent();

	public static Optional<Long> dimensionSeed = Optional.absent();
	public static Optional<Integer> subsurfaceHeight = Optional.absent();

	private static final String DIM_NAME;

	static
	{
		String dim = null;
		WorldProvider provider = null;

		try
		{
			provider = WorldProviderCaveworld.class.newInstance();
		}
		catch (Exception e)
		{
			provider = null;
		}

		if (provider != null)
		{
			dim = provider.getSaveFolder();
		}

		if (dim == null || !dim.startsWith("DIM"))
		{
			dim = "DIM-Caveworld";
		}

		DIM_NAME = dim;
	}

	public static NBTTagCompound getDimData()
	{
		if (!dimData.isPresent())
		{
			dimData = Optional.of(readDimData());
		}

		return dimData.or(new NBTTagCompound());
	}

	public static File getDimDir()
	{
		File root = DimensionManager.getCurrentSaveRootDirectory();

		if (root == null || !root.exists() || root.isFile())
		{
			return null;
		}

		File dir = new File(root, DIM_NAME);

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return dir;
	}

	private static NBTTagCompound readDimData()
	{
		NBTTagCompound data = null;

		try
		{
			File dir = getDimDir();

			if (dir != null)
			{
				data = CompressedStreamTools.read(new File(dir, "caveworld.dat"));
			}
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to reading Caveworld dimension data");
		}
		finally
		{
			if (data == null)
			{
				data = new NBTTagCompound();
			}
		}

		return data;
	}

	public static void writeDimData()
	{
		try
		{
			CompressedStreamTools.write(getDimData(), new File(getDimDir(), "caveworld.dat"));
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to writing Caveworld dimension data");
		}
	}

	public static void clearDimData()
	{
		dimData = Optional.absent();
		dimensionSeed = Optional.absent();
		subsurfaceHeight = Optional.absent();
	}

	private int ambientTickCountdown = 0;

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
		return true;
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
		return posY < 0 || posY >= getActualHeight();
	}

	@Override
	public int getRespawnDimension(EntityPlayerMP player)
	{
		if (Config.hardcoreEnabled || player.getBedLocation(dimensionId) != null)
		{
			return dimensionId;
		}

		return player.getEntityData().getInteger("Caveworld:LastDim");
	}

	@Override
	public boolean isDaytime()
	{
		return worldObj.getWorldInfo().getWorldTime() < 12500;
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
	public void calculateInitialWeather()
	{
		if (!worldObj.isRemote)
		{
			ambientTickCountdown = worldObj.rand.nextInt(8000) + 20000;
		}
	}

	@Override
	public void updateWeather()
	{
		if (!worldObj.isRemote)
		{
			if (ambientTickCountdown > 0)
			{
				--ambientTickCountdown;
			}
			else
			{
				Caveworld.packetPipeline.sendPacketToAllInDimension(new PlayCaveSoundPacket("caveworld:ambient.cave"), dimensionId);

				ambientTickCountdown = worldObj.rand.nextInt(8000) + 20000;
			}
		}
	}

	@Override
	public long getSeed()
	{
		if (!worldObj.isRemote && !dimensionSeed.isPresent())
		{
			NBTTagCompound data = getDimData();

			if (!data.hasKey("DimSeed"))
			{
				data.setLong("DimSeed", new SecureRandom().nextLong());
			}

			dimensionSeed = Optional.of(data.getLong("DimSeed"));
		}

		return dimensionSeed.or(Long.reverseBytes(super.getSeed()));
	}

	@Override
	public int getActualHeight()
	{
		if (!worldObj.isRemote && !subsurfaceHeight.isPresent())
		{
			NBTTagCompound data = getDimData();

			if (!data.hasKey("SubsurfaceHeight"))
			{
				data.setInteger("SubsurfaceHeight", Config.subsurfaceHeight);
			}

			subsurfaceHeight = Optional.of(MathHelper.clamp_int(data.getInteger("SubsurfaceHeight"), 63, 255));
		}

		return subsurfaceHeight.or(127) + 1;
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