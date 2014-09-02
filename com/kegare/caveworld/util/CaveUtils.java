/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Strings;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.world.WorldProviderCaveworld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

public class CaveUtils
{
	public static boolean mcpc = FMLCommonHandler.instance().getModName().contains("mcpc");

	public static ModContainer getModContainer()
	{
		ModContainer mod = Loader.instance().getIndexedModList().get(Caveworld.MODID);

		if (mod == null)
		{
			mod = Loader.instance().activeModContainer();

			if (mod == null || mod.getModId() != Caveworld.MODID)
			{
				return null;
			}
		}

		return mod;
	}

	public static boolean isItemPickaxe(ItemStack itemstack)
	{
		if (itemstack != null && itemstack.stackSize > 0)
		{
			Item item = itemstack.getItem();

			if (item != null && item.isItemTool(itemstack))
			{
				if (item instanceof ItemPickaxe)
				{
					return true;
				}
				else if (item.getToolClasses(itemstack).contains("pickaxe"))
				{
					return true;
				}
				else if (ForgeHooks.isToolEffective(itemstack, Blocks.stone, 0))
				{
					return true;
				}
			}
		}

		return false;
	}

	public static <T extends Entity> T createEntity(Class<T> clazz, World world)
	{
		try
		{
			String name = String.valueOf(EntityList.classToStringMapping.get(clazz));
			Entity entity = EntityList.createEntityByName(Strings.nullToEmpty(name), world);

			if (entity == null || entity.getClass() != clazz)
			{
				return null;
			}

			return clazz.cast(entity);
		}
		catch (Exception e)
		{
			CaveLog.warning("Failed to create entity: %s", clazz.getSimpleName());

			return null;
		}
	}

	public static EntityPlayerMP forceTeleport(EntityPlayerMP player, int dim)
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		int dimOld = player.dimension;
		final WorldServer world = server.worldServerForDimension(dim);

		if (dim != player.dimension)
		{
			player = respawnPlayer(player, dim);

			FMLCommonHandler.instance().bus().post(new PlayerChangedDimensionEvent(player, dimOld, dim));
		}

		ChunkCoordinates spawn = world.getSpawnPoint();
		int var1 = 64;

		for (int x = spawn.posX - var1; x < spawn.posX + var1; ++x)
		{
			for (int z = spawn.posZ - var1; z < spawn.posZ + var1; ++z)
			{
				for (int y = world.getActualHeight() - 3; y > world.provider.getAverageGroundLevel(); --y)
				{
					if (world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z) &&
						world.isAirBlock(x - 1, y, z) && world.isAirBlock(x - 1, y + 1, z) &&
						world.isAirBlock(x + 1, y, z) && world.isAirBlock(x + 1, y + 1, z) &&
						world.isAirBlock(x, y, z - 1) && world.isAirBlock(x, y + 1, z - 1) &&
						world.isAirBlock(x, y, z + 1) && world.isAirBlock(x, y + 1, z + 1) &&
						!world.getBlock(x, y - 1, z).getMaterial().isLiquid())
					{
						while (world.isAirBlock(x, y - 1, z))
						{
							--y;
						}

						if (!world.getBlock(x, y - 1, z).getMaterial().isLiquid())
						{
							player.playerNetServerHandler.setPlayerLocation(x + 0.5D, y + 0.8D, z + 0.5D, player.rotationYaw, player.rotationPitch);
							player.addExperienceLevel(0);

							if (CaveworldAPI.isEntityInCaveworld(player))
							{
								WorldProviderCaveworld.recentTeleportPos = player.getPlayerCoordinates();
							}

							return player;
						}
					}
				}
			}
		}

		return player;
	}

	public static EntityPlayerMP respawnPlayer(EntityPlayerMP player, int dim)
	{
		player.isDead = false;
		player.forceSpawn = true;
		player.timeUntilPortal = player.getPortalCooldown();
		player.playerNetServerHandler.playerEntity = player.mcServer.getConfigurationManager().respawnPlayer(player, dim, true);

		return player.playerNetServerHandler.playerEntity;
	}

	public static boolean archiveDirZip(File dir, File dest)
	{
		ZipOutputStream zos = null;

		try
		{
			zos = new ZipOutputStream(new FileOutputStream(dest), Charset.defaultCharset());
			zos.setLevel(Deflater.BEST_COMPRESSION);

			addEntry(zos, dir, dir.getName());

			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			if (zos != null)
			{
				try
				{
					zos.close();
				}
				catch (IOException e) {}
			}
		}
	}

	private static void addEntry(ZipOutputStream zos, File dir, String root)
	{
		for (File file : dir.listFiles())
		{
			if (file.isDirectory())
			{
				addEntry(zos, file, root + File.separator + file.getName());
			}
			else
			{
				BufferedInputStream input = null;

				try
				{
					input = new BufferedInputStream(new FileInputStream(file));

					zos.putNextEntry(new ZipEntry(root + File.separator + FilenameUtils.getName(file.getAbsolutePath())));

					byte[] buf = new byte[1024];

					for (;;)
					{
						int len = input.read(buf);

						if (len < 0)
						{
							break;
						}

						zos.write(buf, 0, len);
					}

					zos.closeEntry();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					if (input != null)
					{
						try
						{
							input.close();
						}
						catch (IOException e) {}
					}
				}
			}
		}
	}
}