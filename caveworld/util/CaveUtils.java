/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.util;

import java.io.File;
import java.io.FilenameFilter;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import caveworld.api.BlockEntry;
import caveworld.api.CaveworldAPI;
import caveworld.core.Caveworld;
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.common.RegenerateMessage;
import caveworld.world.TeleporterDummy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockWood;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;

public class CaveUtils
{
	private static ForkJoinPool pool;

	public static ForkJoinPool getPool()
	{
		if (pool == null || pool.isShutdown())
		{
			pool = new ForkJoinPool();
		}

		return pool;
	}

	public static final Comparator<Block> blockComparator = new Comparator<Block>()
	{
		@Override
		public int compare(Block o1, Block o2)
		{
			int i = compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				UniqueIdentifier unique1 = GameRegistry.findUniqueIdentifierFor(o1);
				UniqueIdentifier unique2 = GameRegistry.findUniqueIdentifierFor(o2);

				i = compareWithNull(unique1, unique2);

				if (i == 0 && unique1 != null && unique2 != null)
				{
					i = (unique1.modId.equals("minecraft") ? 0 : 1) - (unique2.modId.equals("minecraft") ? 0 : 1);

					if (i == 0)
					{
						i = unique1.modId.compareTo(unique2.modId);

						if (i == 0)
						{
							i = unique1.name.compareTo(unique1.name);
						}
					}
				}
			}

			return i;
		}
	};

	public static final Comparator<BiomeGenBase> biomeComparator = new Comparator<BiomeGenBase>()
	{
		@Override
		public int compare(BiomeGenBase o1, BiomeGenBase o2)
		{
			int i = compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = Integer.compare(o1.biomeID, o2.biomeID);

				if (i == 0)
				{
					i = compareWithNull(o1.biomeName, o2.biomeName);

					if (i == 0 && o1.biomeName != null && o2.biomeName != null)
					{
						i = o1.biomeName.compareTo(o2.biomeName);

						if (i == 0)
						{
							i = Float.compare(o1.temperature, o2.temperature);

							if (i == 0)
							{
								i = Float.compare(o1.rainfall, o2.rainfall);

								if (i == 0)
								{
									i = blockComparator.compare(o1.topBlock, o2.topBlock);

									if (i == 0)
									{
										i = Integer.compare(o1.field_150604_aj, o2.field_150604_aj);

										if (i == 0)
										{
											i = blockComparator.compare(o1.fillerBlock, o2.fillerBlock);

											if (i == 0)
											{
												i = Integer.compare(o1.field_76754_C, o2.field_76754_C);
											}
										}
									}
								}
							}
						}
					}
				}
			}

			return i;
		}
	};

	public static final Set<Item>
	pickaxeItems = Sets.newLinkedHashSet(),
	axeItems = Sets.newLinkedHashSet(),
	shovelItems = Sets.newLinkedHashSet(),
	hoeItems = Sets.newLinkedHashSet(),
	excludeItems = Sets.newHashSet();

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

	public static boolean isItemPickaxe(Item item)
	{
		if (item != null)
		{
			if (pickaxeItems.contains(item))
			{
				return true;
			}

			if (excludeItems.contains(item))
			{
				pickaxeItems.remove(item);

				return false;
			}

			if (item instanceof ItemPickaxe)
			{
				pickaxeItems.add(item);

				return true;
			}

			if (item.getToolClasses(new ItemStack(item)).contains("pickaxe"))
			{
				pickaxeItems.add(item);

				return true;
			}
		}

		return false;
	}

	public static boolean isItemPickaxe(ItemStack itemstack)
	{
		if (itemstack != null && itemstack.getItem() != null && itemstack.stackSize > 0)
		{
			Item item = itemstack.getItem();

			if (pickaxeItems.contains(item))
			{
				return true;
			}

			if (excludeItems.contains(item))
			{
				pickaxeItems.remove(item);

				return false;
			}

			if (item instanceof ItemPickaxe)
			{
				pickaxeItems.add(item);

				return true;
			}

			if (item.getToolClasses(itemstack).contains("pickaxe"))
			{
				pickaxeItems.add(item);

				return true;
			}
		}

		return false;
	}

	public static boolean isItemAxe(Item item)
	{
		if (item != null)
		{
			if (axeItems.contains(item))
			{
				return true;
			}

			if (excludeItems.contains(item))
			{
				axeItems.remove(item);

				return false;
			}

			if (item instanceof ItemAxe)
			{
				axeItems.add(item);

				return true;
			}

			if (item.getToolClasses(new ItemStack(item)).contains("axe"))
			{
				axeItems.add(item);

				return true;
			}
		}

		return false;
	}

	public static boolean isItemAxe(ItemStack itemstack)
	{
		if (itemstack != null && itemstack.getItem() != null && itemstack.stackSize > 0)
		{
			Item item = itemstack.getItem();

			if (axeItems.contains(item))
			{
				return true;
			}

			if (excludeItems.contains(item))
			{
				axeItems.remove(item);

				return false;
			}

			if (item instanceof ItemAxe)
			{
				axeItems.add(item);

				return true;
			}

			if (item.getToolClasses(itemstack).contains("axe"))
			{
				axeItems.add(item);

				return true;
			}
		}

		return false;
	}

	public static boolean isItemShovel(Item item)
	{
		if (item != null)
		{
			if (shovelItems.contains(item))
			{
				return true;
			}

			if (excludeItems.contains(item))
			{
				shovelItems.remove(item);

				return false;
			}

			if (item instanceof ItemSpade)
			{
				shovelItems.add(item);

				return true;
			}

			if (item.getToolClasses(new ItemStack(item)).contains("shovel"))
			{
				shovelItems.add(item);

				return true;
			}
		}

		return false;
	}

	public static boolean isItemShovel(ItemStack itemstack)
	{
		if (itemstack != null && itemstack.getItem() != null && itemstack.stackSize > 0)
		{
			Item item = itemstack.getItem();

			if (shovelItems.contains(item))
			{
				return true;
			}

			if (excludeItems.contains(item))
			{
				shovelItems.remove(item);

				return false;
			}

			if (item instanceof ItemSpade)
			{
				shovelItems.add(item);

				return true;
			}

			if (item.getToolClasses(itemstack).contains("shovel"))
			{
				shovelItems.add(item);

				return true;
			}
		}

		return false;
	}

	public static boolean isItemHoe(Item item)
	{
		if (item != null)
		{
			if (hoeItems.contains(item))
			{
				return true;
			}

			if (excludeItems.contains(item))
			{
				hoeItems.remove(item);

				return false;
			}

			if (item instanceof ItemHoe)
			{
				hoeItems.add(item);

				return true;
			}
		}

		return false;
	}

	public static boolean isItemHoe(ItemStack itemstack)
	{
		if (itemstack != null && itemstack.getItem() != null && itemstack.stackSize > 0)
		{
			Item item = itemstack.getItem();

			if (hoeItems.contains(item))
			{
				return true;
			}

			if (excludeItems.contains(item))
			{
				hoeItems.remove(item);

				return false;
			}

			if (item instanceof ItemHoe)
			{
				hoeItems.add(item);

				return true;
			}
		}

		return false;
	}

	public static boolean isWood(Block block, int meta)
	{
		if (Strings.nullToEmpty(block.getHarvestTool(meta)).equalsIgnoreCase("axe") || block instanceof BlockLog || block instanceof BlockWood)
		{
			return true;
		}

		ItemStack itemstack = new ItemStack(block, 1, meta);

		if (CaveUtils.containsOreDict(itemstack, "logWood") || CaveUtils.containsOreDict(itemstack, "plankWood") || CaveUtils.containsOreDict(itemstack, "slabWood") || CaveUtils.containsOreDict(itemstack, "stairWood"))
		{
			return true;
		}

		itemstack.setItemDamage(OreDictionary.WILDCARD_VALUE);

		if (CaveUtils.containsOreDict(itemstack, "logWood") || CaveUtils.containsOreDict(itemstack, "plankWood") || CaveUtils.containsOreDict(itemstack, "slabWood") || CaveUtils.containsOreDict(itemstack, "stairWood"))
		{
			return true;
		}

		return false;
	}

	public static String getEntityLocalizedName(String name)
	{
		String key = "entity." + name + ".name";
		String localized = StatCollector.translateToLocal(key);

		if (key.equals(localized))
		{
			localized = name;
		}

		return localized;
	}

	public static Set<BiomeGenBase> getBiomes()
	{
		Set<BiomeGenBase> result = Sets.newLinkedHashSet();

		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
		{
			if (biome != null)
			{
				result.add(biome);
			}
		}

		return result;
	}

	public static Set<Potion> getPotions()
	{
		Set<Potion> result = Sets.newLinkedHashSet();

		for (Potion potion : Potion.potionTypes)
		{
			if (potion != null)
			{
				result.add(potion);
			}
		}

		return result;
	}

	public static void setPlayerLocation(EntityPlayerMP player, double posX, double posY, double posZ)
	{
		setPlayerLocation(player, posX, posY, posZ, player.rotationYaw, player.rotationPitch);
	}

	public static void setPlayerLocation(EntityPlayerMP player, double posX, double posY, double posZ, float yaw, float pitch)
	{
		player.mountEntity(null);
		player.playerNetServerHandler.setPlayerLocation(posX, posY, posZ, yaw, pitch);
	}

	public static boolean transferPlayer(EntityPlayerMP player, int dim)
	{
		if (dim != player.dimension)
		{
			if (!DimensionManager.isDimensionRegistered(dim))
			{
				return false;
			}

			player.isDead = false;
			player.forceSpawn = true;
			player.timeUntilPortal = player.getPortalCooldown();
			player.mcServer.getConfigurationManager().transferPlayerToDimension(player, dim, new TeleporterDummy(player.mcServer.worldServerForDimension(dim)));
			player.addExperienceLevel(0);

			if (player.dimension == 1 || CaveworldAPI.isEntityInCavenia(player))
			{
				setPlayerLocation(player, 0, 0, 0);
			}

			return true;
		}

		return false;
	}

	public static boolean teleportPlayer(EntityPlayerMP player, int dim)
	{
		transferPlayer(player, dim);

		WorldServer world = player.getServerForPlayer();
		int originX = MathHelper.floor_double(player.posX);
		int originZ = MathHelper.floor_double(player.posZ);
		int range = 16;
		int x, y, z;

		for (x = originX - range; x < originX + range; ++x)
		{
			for (z = originZ - range; z < originZ + range; ++z)
			{
				for (y = 1; y < world.getActualHeight() - 5; ++y)
				{
					if (world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
					{
						while (y > 1 && world.isAirBlock(x, y - 1, z))
						{
							--y;
						}

						if (!world.isAirBlock(x, y - 1, z) && !world.getBlock(x, y - 1, z).getMaterial().isLiquid())
						{
							setPlayerLocation(player, x + 0.5D, y + 0.5D, z + 0.5D);

							return true;
						}
					}
				}
			}
		}

		x = 0;
		y = 30;
		z = 0;
		setPlayerLocation(player, x + 0.5D, y + 0.5D, z + 0.5D);
		world.setBlockToAir(x, y, z);
		world.setBlockToAir(x, y + 1, z);
		world.setBlock(x, y - 1, z, Blocks.stone, 0, 2);

		return false;
	}

	public static boolean regenerateDimension(int dim, boolean backup, boolean ret)
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		WorldServer world = server.worldServerForDimension(dim);
		File dir = new File(DimensionManager.getCurrentSaveRootDirectory(), world.provider.getSaveFolder());
		String name = world.provider.getDimensionName();
		Set<EntityPlayerMP> teleportedPlayers = Sets.newHashSet();

		for (Object obj : server.getConfigurationManager().playerEntityList)
		{
			EntityPlayerMP player = (EntityPlayerMP)obj;

			if (player.dimension == dim)
			{
				teleportPlayer(player, 0);

				teleportedPlayers.add(player);
			}
		}

		IChatComponent component;

		component = new ChatComponentTranslation("caveworld.regenerate.regenerating", name);
		component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
		server.getConfigurationManager().sendChatMsg(component);

		if (server.isSinglePlayer())
		{
			CaveNetworkRegistry.sendToAll(new RegenerateMessage(backup));
		}

		CaveNetworkRegistry.sendToAll(new RegenerateMessage.ProgressNotify(0));

		try
		{
			world.saveAllChunks(true, null);
		}
		catch (MinecraftException e)
		{
			return false;
		}

		world.flush();

		MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(world));

		DimensionManager.setWorld(dim, null);

		if (dir.exists())
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
							int i = compareWithNull(o1, o2);

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

					try
					{
						FileUtils.forceDelete(files[0]);
					}
					catch (IOException e) {}
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

				component = new ChatComponentTranslation("caveworld.regenerate.backingup", name);
				component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
				server.getConfigurationManager().sendChatMsg(component);

				CaveNetworkRegistry.sendToAll(new RegenerateMessage.ProgressNotify(1));

				if (archiveDirZip(dir, bak))
				{
					ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_FILE, FilenameUtils.normalize(bak.getParentFile().getPath()));

					component = new ChatComponentTranslation("caveworld.regenerate.backedup", name);
					component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true).setChatClickEvent(click);
					server.getConfigurationManager().sendChatMsg(component);
				}
				else
				{
					component = new ChatComponentTranslation("caveworld.regenerate.backup.failed", name);
					component.getChatStyle().setColor(EnumChatFormatting.RED).setItalic(true);
					server.getConfigurationManager().sendChatMsg(component);
				}
			}

			try
			{
				FileUtils.deleteDirectory(dir);
			}
			catch (IOException e)
			{
				return false;
			}
		}

		if (DimensionManager.shouldLoadSpawn(dim))
		{
			world = server.worldServerForDimension(dim);

			try
			{
				world.saveAllChunks(true, null);
			}
			catch (MinecraftException e)
			{
				return false;
			}

			world.flush();
		}

		component = new ChatComponentTranslation("caveworld.regenerate.regenerated", name);
		component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
		server.getConfigurationManager().sendChatMsg(component);

		CaveNetworkRegistry.sendToAll(new RegenerateMessage.ProgressNotify(2));

		if (ret)
		{
			for (EntityPlayerMP player : teleportedPlayers)
			{
				if (player.dimension != dim)
				{
					teleportPlayer(player, dim);
				}
			}
		}

		return true;
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
	public static boolean renderItemStack(Minecraft mc, ItemStack itemstack, int x, int y, boolean raw, boolean overlay, String txt)
	{
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		boolean isLightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
		boolean rc = false;

		if (itemstack != null && itemstack.getItem() != null)
		{
			Block block = Block.getBlockFromItem(itemstack.getItem());
			String name = GameData.getBlockRegistry().getNameForObject(block);

			if (!Strings.isNullOrEmpty(name) && name.startsWith("EnderIO") && !block.renderAsNormalBlock())
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

			itemRender.zLevel += 100.0F;
			boolean rendered = false;

			try
			{
				if (!raw)
				{
					rendered = ForgeHooksClient.renderInventoryItem(RenderBlocks.getInstance(), mc.getTextureManager(), itemstack, itemRender.renderWithColor, itemRender.zLevel, x, y);
				}
			}
			catch (Throwable e)
			{
				rendered = false;
			}

			if (!rendered)
			{
				try
				{
					itemRender.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y, true);
				}
				catch (Throwable e) {}
			}

			if (overlay)
			{
				try
				{
					itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y, txt);
				}
				catch (Throwable e) {}
			}

			itemRender.zLevel -= 100.0F;

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

	public static boolean containsIgnoreCase(String s1, String s2)
	{
		if (Strings.isNullOrEmpty(s1) || Strings.isNullOrEmpty(s2))
		{
			return false;
		}

		return Pattern.compile(Pattern.quote(s2), Pattern.CASE_INSENSITIVE).matcher(s1).find();
	}

	public static boolean containsOreDict(ItemStack itemstack, String oredict)
	{
		int[] ids = OreDictionary.getOreIDs(itemstack);

		for (int i = 0; i < ids.length; ++i)
		{
			String name = OreDictionary.getOreName(ids[i]);

			if (!name.equalsIgnoreCase("Unknown") && StringUtils.contains(name, oredict))
			{
				return true;
			}
		}

		return false;
	}

	public static boolean blockFilter(BlockEntry entry, String filter)
	{
		if (entry == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		Block block = entry.getBlock();

		try
		{
			if (containsIgnoreCase(GameData.getBlockRegistry().getNameForObject(block), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		ItemStack itemstack = new ItemStack(block, 1, entry.getMetadata());

		if (itemstack.getItem() == null)
		{
			try
			{
				if (containsIgnoreCase(block.getUnlocalizedName(), filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}

			try
			{
				if (containsIgnoreCase(block.getLocalizedName(), filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}
		}
		else
		{
			try
			{
				if (containsIgnoreCase(itemstack.getUnlocalizedName(), filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}

			try
			{
				if (containsIgnoreCase(itemstack.getDisplayName(), filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}

			try
			{
				if (itemstack.getItem().getToolClasses(itemstack).contains(filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}
		}

		return false;
	}

	public static boolean itemFilter(ItemEntry entry, String filter)
	{
		if (entry == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		try
		{
			if (containsIgnoreCase(GameData.getItemRegistry().getNameForObject(entry.item), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		ItemStack itemstack = entry.getItemStack();

		try
		{
			if (containsIgnoreCase(itemstack.getUnlocalizedName(), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			if (containsIgnoreCase(itemstack.getDisplayName(), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			if (itemstack.getItem().getToolClasses(itemstack).contains(filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		return false;
	}

	public static boolean itemFilter(ItemStack itemstack, String filter)
	{
		if (itemstack == null || itemstack.getItem() == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		try
		{
			if (containsIgnoreCase(GameData.getItemRegistry().getNameForObject(itemstack.getItem()), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			if (containsIgnoreCase(itemstack.getUnlocalizedName(), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			if (containsIgnoreCase(itemstack.getDisplayName(), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			if (itemstack.getItem().getToolClasses(itemstack).contains(filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		return false;
	}

	public static boolean biomeFilter(BiomeGenBase biome, String filter)
	{
		if (biome == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		if (biome.biomeID == NumberUtils.toInt(filter, -1) || containsIgnoreCase(biome.biomeName, filter))
		{
			return true;
		}

		try
		{
			if (BiomeDictionary.isBiomeOfType(biome, Type.valueOf(filter.toUpperCase())))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			if (blockFilter(new BlockEntry(biome.topBlock, biome.field_150604_aj), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			if (blockFilter(new BlockEntry(biome.fillerBlock, biome.field_76754_C), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			BiomeType type = BiomeType.valueOf(filter.toUpperCase());

			if (type != null)
			{
				for (BiomeEntry entry : BiomeManager.getBiomes(type))
				{
					if (entry.biome.biomeID == biome.biomeID)
					{
						return true;
					}
				}
			}
		}
		catch (Throwable e) {}

		return false;
	}
}