/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.world.WorldProviderCaveworld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CaveUtils
{
	public static boolean mcpc = FMLCommonHandler.instance().getModName().contains("mcpc");

	public static List<ConfigCategory> getConfigCategories(final Configuration config)
	{
		return Lists.newArrayList(Collections2.transform(config.getCategoryNames(), new Function<String, ConfigCategory>()
		{
			@Override
			public ConfigCategory apply(String category)
			{
				return config.getCategory(category);
			}
		}));
	}

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

	public static Set<BiomeGenBase> getBiomes()
	{
		Set<BiomeGenBase> result = Sets.newHashSet();

		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
		{
			if (biome != null)
			{
				result.add(biome);
			}
		}

		return result;
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

	public static MovingObjectPosition rayTrace(EntityPlayer player, double distance)
	{
		double eyeHeight = player.worldObj.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight();
		Vec3 lookVec = player.getLookVec();
		Vec3 origin = Vec3.createVectorHelper(player.posX, player.posY + eyeHeight, player.posZ);
		Vec3 direction = origin.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);

		return player.worldObj.rayTraceBlocks(origin, direction, true);
	}

	public static boolean archiveDirZip(final File dir, final File dest)
	{
		final Path dirPath = dir.toPath();
		final String parent = dir.getName();
		Map<String, String> env = Maps.newHashMap();
		env.put("create", "true");
		URI uri = dest.toURI();

		try
		{
			uri = new URI("jar:" + uri.getScheme(), uri.getPath(), null);
		}
		catch (Exception e)
		{
			return false;
		}

		try (FileSystem zipfs = FileSystems.newFileSystem(uri, env))
		{
			Files.createDirectory(zipfs.getPath(parent));

			for (File file : dir.listFiles())
			{
				if (file.isDirectory())
				{
					Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>()
					{
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
						{
							Files.copy(file, zipfs.getPath(parent, dirPath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);

							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
						{
							Files.createDirectory(zipfs.getPath(parent, dirPath.relativize(dir).toString()));

							return FileVisitResult.CONTINUE;
						}
					});
				}
				else
				{
					Files.copy(file.toPath(), zipfs.getPath(parent, file.getName()), StandardCopyOption.REPLACE_EXISTING);
				}
			}

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
	}

	public static int compareWithNull(Object o1, Object o2)
	{
		return (o1 == null ? 1 : 0) - (o2 == null ? 1 : 0);
	}

	public static boolean canMerge(ItemStack itemstack1, ItemStack itemstack2)
	{
		if (itemstack1 == null || itemstack2 == null)
		{
			return false;
		}
		else if (!itemstack1.isItemEqual(itemstack2) || !itemstack1.isStackable() || !ItemStack.areItemStackTagsEqual(itemstack1, itemstack2))
		{
			return false;
		}

		return true;
	}

	public static String toStringHelper(Block block, int metadata)
	{
		String name = GameData.getBlockRegistry().getNameForObject(block);

		if (metadata == OreDictionary.WILDCARD_VALUE)
		{
			return name;
		}

		return name + "@" + metadata;
	}

	@SideOnly(Side.CLIENT)
	public static boolean renderItemStack(Minecraft mc, ItemStack itemstack, int x, int y, boolean overlay, String txt)
	{
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		boolean isLightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
		boolean rc = false;

		if (itemstack != null && itemstack.getItem() != null)
		{
			if (Block.getBlockFromItem(itemstack.getItem()).hasTileEntity(itemstack.getItemDamage()) ||
				MinecraftForgeClient.getItemRenderer(itemstack, ItemRenderType.INVENTORY) != null)
			{
				return false;
			}

			rc = true;
			boolean isRescaleNormalEnabled = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL);
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 0.0F, 32.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_LIGHTING);
			short short1 = 240;
			short short2 = 240;
			RenderHelper.enableGUIStandardItemLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
			RenderItem itemRender = RenderItem.getInstance();
			itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);

			if (overlay)
			{
				itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y, txt);
			}

			GL11.glPopMatrix();

			if (isRescaleNormalEnabled)
			{
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			}
			else
			{
				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			}
		}

		if (isLightingEnabled)
		{
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		else
		{
			GL11.glDisable(GL11.GL_LIGHTING);
		}

		return rc;
	}
}