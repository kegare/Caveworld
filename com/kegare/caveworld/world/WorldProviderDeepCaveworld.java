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
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
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
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.network.RegenerateMessage;
import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.util.CaveUtils;

import cpw.mods.fml.common.FMLCommonHandler;

public class WorldProviderDeepCaveworld extends WorldProviderCaveworld
{
	public static File getDimDir()
	{
		File root = DimensionManager.getCurrentSaveRootDirectory();

		if (root == null || !root.exists() || root.isFile())
		{
			return null;
		}

		File dir = new File(root, new WorldProviderDeepCaveworld().getSaveFolder());

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return dir.isDirectory() ? dir : null;
	}

	public static void regenerate(final boolean backup)
	{
		final File dir = getDimDir();
		final String name = dir.getName().substring(4);
		final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		Set<EntityPlayerMP> target = Sets.newHashSet();

		for (Object obj : server.getConfigurationManager().playerEntityList.toArray())
		{
			if (obj != null && ((EntityPlayerMP)obj).dimension == CaveworldAPI.getDeepDimension())
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
					component = new ChatComponentText(StatCollector.translateToLocalFormatted("caveworld.regenerate.regenerating", name));
					component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
					server.getConfigurationManager().sendChatMsg(component);

					if (server.isSinglePlayer())
					{
						Caveworld.network.sendToAll(new RegenerateMessage(backup));
					}

					Caveworld.network.sendToAll(new RegenerateMessage.ProgressNotify(0));

					CaveBlocks.caveworld_portal.portalDisabled = true;

					int dim = CaveworldAPI.getDeepDimension();
					WorldServer world = DimensionManager.getWorld(dim);

					if (world != null)
					{
						world.saveAllChunks(true, null);
						world.flush();

						MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(world));

						DimensionManager.setWorld(dim, null);
					}

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

							component = new ChatComponentText(StatCollector.translateToLocalFormatted("caveworld.regenerate.backingup", name));
							component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
							server.getConfigurationManager().sendChatMsg(component);

							Caveworld.network.sendToAll(new RegenerateMessage.ProgressNotify(1));

							if (CaveUtils.archiveDirZip(dir, bak))
							{
								ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_FILE, FilenameUtils.normalize(bak.getParentFile().getPath()));

								component = new ChatComponentText(StatCollector.translateToLocalFormatted("caveworld.regenerate.backedup", name));
								component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true).setChatClickEvent(click);
								server.getConfigurationManager().sendChatMsg(component);
							}
							else
							{
								component = new ChatComponentText(StatCollector.translateToLocalFormatted("caveworld.regenerate.backup.failed", name));
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

					component = new ChatComponentText(StatCollector.translateToLocalFormatted("caveworld.regenerate.regenerated", name));
					component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
					server.getConfigurationManager().sendChatMsg(component);

					Caveworld.network.sendToAll(new RegenerateMessage.ProgressNotify(2));

					return true;
				}
				catch (Exception e)
				{
					component = new ChatComponentText(StatCollector.translateToLocalFormatted("caveworld.regenerate.failed", name));
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
					CaveUtils.forceTeleport(player, CaveworldAPI.getDeepDimension(), false);
				}
			}
		}
	}

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerCaveworld(worldObj);
		dimensionId = CaveworldAPI.getDeepDimension();
		hasNoSky = true;

		try
		{
			registerWorldChunkManagerTFC();
		}
		catch (NoSuchMethodError e) {}
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderDeepCaveworld(worldObj);
	}

	@Override
	public String getDimensionName()
	{
		return "Deep Caveworld";
	}
}