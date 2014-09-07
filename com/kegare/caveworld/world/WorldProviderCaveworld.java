/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.client.renderer.EmptyRenderer;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.network.CaveSoundMessage;
import com.kegare.caveworld.network.RegenerateMessage;
import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.world.gen.MapGenStrongholdCaveworld;
import com.kegare.caveworld.world.gen.StructureStrongholdPiecesCaveworld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class WorldProviderCaveworld extends WorldProviderSurface
{
	private static NBTTagCompound dimData;

	private static long dimensionSeed;
	private static int subsurfaceHeight;

	public static ChunkCoordinates recentTeleportPos;

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
			NBTTagCompound data = getDimData();

			if (recentTeleportPos != null)
			{
				NBTTagCompound dat = new NBTTagCompound();

				dat.setInteger("PosX", recentTeleportPos.posX);
				dat.setInteger("PosY", recentTeleportPos.posY);
				dat.setInteger("PosZ", recentTeleportPos.posZ);

				data.setTag("TeleportPos", dat);
			}

			CompressedStreamTools.writeCompressed(data, output);
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

		NBTTagCompound dat = data.getCompoundTag("TeleportPos");

		if (dat != null)
		{
			int posX = dat.getInteger("PosX");
			int posY = dat.getInteger("PosY");
			int posZ = dat.getInteger("PosZ");

			recentTeleportPos = new ChunkCoordinates(posX, posY, posZ);
		}
	}

	public static void saveDimData()
	{
		if (dimData != null)
		{
			writeDimData();

			dimData = null;
		}
	}

	public static void regenerate(final boolean backup)
	{
		final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		Set<EntityPlayerMP> target = Sets.newHashSet();

		for (Object obj : server.getConfigurationManager().playerEntityList.toArray())
		{
			if (CaveworldAPI.isEntityInCaveworld((EntityPlayerMP)obj))
			{
				target.add(CaveUtils.respawnPlayer((EntityPlayerMP)obj, 0));
			}
		}

		boolean result = new ForkJoinPool().invoke(new RecursiveTask<Boolean>()
		{
			@Override
			protected Boolean compute()
			{
				IChatComponent component;

				try
				{
					component = new ChatComponentTranslation("caveworld.regenerate.regenerating");
					component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
					server.getConfigurationManager().sendChatMsg(component);

					if (server.isSinglePlayer())
					{
						Caveworld.network.sendToAll(new RegenerateMessage(backup));
					}

					Caveworld.network.sendToAll(new RegenerateMessage.ProgressNotify(0));

					CaveBlocks.caveworld_portal.portalDisabled = true;

					int dim = CaveworldAPI.getDimension();
					WorldServer world = DimensionManager.getWorld(dim);

					if (world != null)
					{
						world.saveAllChunks(true, null);
						world.flush();

						MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(world));

						DimensionManager.setWorld(dim, null);
					}

					File dir = WorldProviderCaveworld.getDimDir();

					if (dir != null)
					{
						if (backup)
						{
							File parent = dir.getParentFile();
							final Pattern pattern = Pattern.compile("^" + dir.getName() + "_bak-..*\\.zip$");
							File[] files = parent.listFiles(new FilenameFilter()
							{
								@Override
								public boolean accept(File dir, String name)
								{
									return pattern.matcher(name).matches();
								}
							});

							if (files != null && files.length >= 5)
							{
								Arrays.sort(files, new Comparator<File>()
								{
									@Override
									public int compare(File o1, File o2)
									{
										int i = CaveUtils.compareWithNull(o1, o2);

										if (i == 0 && o1 != null && o2 != null)
										{
											try
											{
												i = Files.getLastModifiedTime(o1.toPath()).compareTo(Files.getLastModifiedTime(o2.toPath()));
											}
											catch (IOException e) {}
										}

										return i;
									}
								});

								FileUtils.forceDelete(files[0]);
							}

							Calendar calendar = Calendar.getInstance();
							String year = Integer.toString(calendar.get(Calendar.YEAR));
							String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
							String day = String.format("%02d", calendar.get(Calendar.DATE));
							String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
							String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
							String second = String.format("%02d", calendar.get(Calendar.SECOND));
							File bak = new File(parent, dir.getName() + "_bak-" + Joiner.on("").join(year, month, day) + "-" + Joiner.on("").join(hour, minute, second) + ".zip");

							if (bak.exists())
							{
								FileUtils.deleteQuietly(bak);
							}

							component = new ChatComponentTranslation("caveworld.regenerate.backingup");
							component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
							server.getConfigurationManager().sendChatMsg(component);

							Caveworld.network.sendToAll(new RegenerateMessage.ProgressNotify(1));

							if (CaveUtils.archiveDirZip(dir, bak))
							{
								ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_FILE, FilenameUtils.normalize(bak.getParentFile().getPath()));

								component = new ChatComponentTranslation("caveworld.regenerate.backedup");
								component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true).setChatClickEvent(click);
								server.getConfigurationManager().sendChatMsg(component);
							}
							else
							{
								component = new ChatComponentTranslation("caveworld.regenerate.backup.failed");
								component.getChatStyle().setColor(EnumChatFormatting.RED).setItalic(true);
								server.getConfigurationManager().sendChatMsg(component);
							}
						}

						FileUtils.deleteDirectory(dir);
					}

					if (DimensionManager.shouldLoadSpawn(dim))
					{
						DimensionManager.initDimension(dim);

						world = DimensionManager.getWorld(dim);

						if (world != null)
						{
							world.saveAllChunks(true, null);
							world.flush();
						}
					}

					CaveBlocks.caveworld_portal.portalDisabled = false;

					component = new ChatComponentTranslation("caveworld.regenerate.regenerated");
					component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
					server.getConfigurationManager().sendChatMsg(component);

					Caveworld.network.sendToAll(new RegenerateMessage.ProgressNotify(2));

					return true;
				}
				catch (Exception e)
				{
					component = new ChatComponentTranslation("caveworld.regenerate.failed");
					component.getChatStyle().setColor(EnumChatFormatting.RED).setItalic(true);
					server.getConfigurationManager().sendChatMsg(component);

					Caveworld.network.sendToAll(new RegenerateMessage.ProgressNotify(3));

					CaveLog.log(Level.ERROR, e, component.getUnformattedText());
				}

				return false;
			}
		});

		if (result && (Config.hardcore || Config.caveborn))
		{
			for (EntityPlayerMP player : target)
			{
				if (!CaveworldAPI.isEntityInCaveworld(player))
				{
					CaveUtils.forceTeleport(player, CaveworldAPI.getDimension());
				}
			}
		}
	}

	private int ambientTickCountdown = 0;

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerCaveworld(worldObj);
		dimensionId = CaveworldAPI.getDimension();
		hasNoSky = true;

		MapGenStructureIO.registerStructure(MapGenStrongholdCaveworld.Start.class, "Caveworld.Stronghold");
		StructureStrongholdPiecesCaveworld.registerStrongholdPieces();

		if (!worldObj.isRemote)
		{
			CaveBlocks.caveworld_portal.loadInventoryFromDimData();
		}
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
		if (CaveUtils.mcpc)
		{
			return "DIM" + CaveworldAPI.getDimension();
		}

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

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getSkyRenderer()
	{
		return EmptyRenderer.instance;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getCloudRenderer()
	{
		return EmptyRenderer.instance;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getWeatherRenderer()
	{
		return EmptyRenderer.instance;
	}

	@Override
	public boolean shouldMapSpin(String entity, double posX, double posY, double posZ)
	{
		return posY < 0 || posY >= getActualHeight();
	}

	@Override
	public ChunkCoordinates getSpawnPoint()
	{
		return recentTeleportPos == null ? new ChunkCoordinates(0, getAverageGroundLevel(), 0) : recentTeleportPos;
	}

	@Override
	public int getRespawnDimension(EntityPlayerMP player)
	{
		return dimensionId;
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