package com.kegare.caveworld.world;

import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.packet.AbstractPacket;
import com.kegare.caveworld.renderer.EmptyRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.DimensionManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.Properties;

public class WorldProviderCaveworld extends WorldProvider
{
	private static final Properties dimensionData = new Properties();

	public static long dimensionSeed;
	public static int subsurfaceHeight;

	public static void loadDimensionData()
	{
		if (dimensionData.isEmpty())
		{
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
					FileInputStream fis = new FileInputStream(file);

					try
					{
						dimensionData.loadFromXML(fis);
					}
					finally
					{
						fis.close();
					}
				}
			}
			catch (Exception ignored)
			{
				dimensionData.clear();
			}
		}
	}

	public static void saveDimensionData()
	{
		if (!dimensionData.isEmpty())
		{
			try
			{
				File dir = new File(DimensionManager.getCurrentSaveRootDirectory(), "DIM-Caveworld");

				if (!dir.exists())
				{
					dir.mkdirs();
				}

				File file = new File(dir, "caveworld.xml");
				DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));

				try
				{
					dimensionData.storeToXML(dos, null);
				}
				finally
				{
					dos.close();
				}
			}
			catch (Exception ignored) {}
		}
	}

	public static Properties getDimensionData()
	{
		loadDimensionData();

		return dimensionData;
	}

	public static void clearDimensionData()
	{
		dimensionData.clear();
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
			Properties data = getDimensionData();
			String key = "DimSeed";

			if (!data.containsKey(key))
			{
				data.setProperty(key, String.valueOf(new SecureRandom().nextLong()));
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
			Properties data = getDimensionData();
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

	public static class DataSyncPacket extends AbstractPacket
	{
		private long dimensionSeed;
		private int subsurfaceHeight;

		public DataSyncPacket()
		{
			dimensionSeed = WorldProviderCaveworld.dimensionSeed;
			subsurfaceHeight = WorldProviderCaveworld.subsurfaceHeight;
		}

		@Override
		public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
		{
			buffer.writeLong(dimensionSeed);
			buffer.writeInt(subsurfaceHeight);
		}

		@Override
		public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
		{
			dimensionSeed = buffer.readLong();
			subsurfaceHeight = buffer.readInt();
		}

		@Override
		public void handleClientSide(EntityPlayer player)
		{
			WorldProviderCaveworld.dimensionSeed = dimensionSeed;
			WorldProviderCaveworld.subsurfaceHeight = subsurfaceHeight;
		}

		@Override
		public void handleServerSide(EntityPlayer player) {}
	}
}