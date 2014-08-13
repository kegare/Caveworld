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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.DimensionManager;

import org.apache.logging.log4j.Level;

import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.network.CaveSoundMessage;
import com.kegare.caveworld.renderer.EmptyRenderer;
import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.world.gen.MapGenStrongholdCaveworld;
import com.kegare.caveworld.world.gen.StructureStrongholdPiecesCaveworld;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WorldProviderCaveworld extends WorldProvider
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
		WorldProvider provider = null;

		try
		{
			provider = WorldProviderCaveworld.class.newInstance();
		}
		catch (Exception e)
		{
			provider = null;
		}
		finally
		{
			if (provider == null)
			{
				return null;
			}
		}

		File root = DimensionManager.getCurrentSaveRootDirectory();

		if (root == null || !root.exists() || root.isFile())
		{
			return null;
		}

		File dir = new File(root, provider.getSaveFolder());

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return dir.isDirectory() ? dir : null;
	}

	private static NBTTagCompound readDimData()
	{
		NBTTagCompound data;
		File file = new File(getDimDir(), "caveworld.dat");

		if (file == null || !file.exists())
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

		return data == null ? new NBTTagCompound() : data;
	}

	private static void writeDimData()
	{
		File file = new File(getDimDir(), "caveworld.dat");

		try (FileOutputStream output = new FileOutputStream(file))
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
			data.setInteger("SubsurfaceHeight", Config.subsurfaceHeight);
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
		return Vec3.createVectorHelper(0.01D, 0.01D, 0.01D);
	}

	@Override
	public boolean canRespawnHere()
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

	@Override
	public double getMovementFactor()
	{
		return 3.0D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer()
	{
		return EmptyRenderer.instance;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getCloudRenderer()
	{
		return EmptyRenderer.instance;
	}

	@Override
	public boolean shouldMapSpin(String entity, double posX, double posY, double posZ)
	{
		return posY < 0 || posY >= getActualHeight();
	}

	@Override
	public int getRespawnDimension(EntityPlayerMP player)
	{
		if (Config.hardcore || player.getBedLocation(dimensionId) != null)
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
				Caveworld.network.sendToDimension(new CaveSoundMessage(new ResourceLocation("caveworld", "ambient.cave")), dimensionId);

				ambientTickCountdown = worldObj.rand.nextInt(8000) + 20000;
			}
		}
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