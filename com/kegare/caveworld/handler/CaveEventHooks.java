/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.handler;

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.core.CaveAchievementList;
import com.kegare.caveworld.core.CaveOreManager;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.packet.CaveBiomeSyncPacket;
import com.kegare.caveworld.packet.CaveOreSyncPacket;
import com.kegare.caveworld.packet.ConfigSyncPacket;
import com.kegare.caveworld.packet.DataSyncPacket;
import com.kegare.caveworld.packet.MiningCountPacket;
import com.kegare.caveworld.packet.PlayCaveSoundPacket;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.world.WorldProviderCaveworld;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.security.SecureRandom;
import java.util.Random;

public class CaveEventHooks
{
	public static final CaveEventHooks instance = new CaveEventHooks();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderGameOverlay(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.thePlayer != null && mc.thePlayer.dimension == Config.dimensionCaveworld)
		{
			ItemStack current = mc.thePlayer.getCurrentEquippedItem();

			if (mc.gameSettings.showDebugInfo)
			{
				event.left.add("dim: Caveworld");
			}
			else if (mc.thePlayer.isSneaking() && current != null && current.getItem().getToolClasses(current).contains("pickaxe"))
			{
				int count = CaveUtils.getMiningCount(mc.thePlayer);

				if (count > 0)
				{
					int level = CaveUtils.getMiningLevel(mc.thePlayer);
					StringBuilder builder = new StringBuilder();

					builder.append("Mining Count: ").append(count);

					if (level > 0)
					{
						builder.append(" (").append(level).append(')');
					}

					event.right.add(builder.toString());
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;

			Caveworld.packetPipeline.sendPacketToPlayer(new ConfigSyncPacket(), player);
			Caveworld.packetPipeline.sendPacketToPlayer(new DataSyncPacket(), player);

			if (!player.mcServer.isSinglePlayer())
			{
				Caveworld.packetPipeline.sendPacketToPlayer(new CaveBiomeSyncPacket(), player);
				Caveworld.packetPipeline.sendPacketToPlayer(new CaveOreSyncPacket(), player);
			}

			Caveworld.packetPipeline.sendPacketToPlayer(new MiningCountPacket(player), player);

			if (Version.DEV_DEBUG || Config.versionNotify && Version.isOutdated())
			{
				ChatStyle style = new ChatStyle();
				style.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Caveworld.metadata.url));

				player.addChatMessage(new ChatComponentText(StatCollector.translateToLocalFormatted("caveworld.version.message", EnumChatFormatting.AQUA + "Caveworld" + EnumChatFormatting.RESET) + " : " + EnumChatFormatting.YELLOW + Version.getLatest()).setChatStyle(style));
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;

			if (player.dimension == Config.dimensionCaveworld)
			{
				player.setSpawnChunk(null, true, player.dimension);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			WorldServer world = player.getServerForPlayer();

			if (event.toDim == Config.dimensionCaveworld)
			{
				NBTTagCompound data = player.getEntityData();

				if (!player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.caveworld) || data.getLong("Caveworld:LastTeleportTime") + 18000L < world.getTotalWorldTime())
				{
					Caveworld.packetPipeline.sendPacketToPlayer(new PlayCaveSoundPacket("ambient.cave"), player);
				}

				player.triggerAchievement(CaveAchievementList.caveworld);

				data.setLong("Caveworld:LastTeleportTime", world.getTotalWorldTime());
			}
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event)
	{
		if (event.getPlayer() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.getPlayer();

			if (player.dimension == Config.dimensionCaveworld && (Version.DEV_DEBUG || !player.capabilities.isCreativeMode))
			{
				ItemStack current = player.getCurrentEquippedItem();
				Block block = event.block;
				int metadata = event.blockMetadata;

				if (current != null && current.getItem().getToolClasses(current).contains("pickaxe") && CaveOreManager.containsBlock(block, metadata, true))
				{
					NBTTagCompound data = player.getEntityData();
					int count = data.getInteger("Caveworld:MiningCount");

					data.setInteger("Caveworld:MiningCount", ++count);

					if (count > 500 && count % 500 == 0)
					{
						player.triggerAchievement(CaveAchievementList.miner);
					}

					Caveworld.packetPipeline.sendPacketToPlayer(new MiningCountPacket(count), player);
				}
			}
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		EntityPlayer player = event.entityPlayer;

		if (player != null && player.dimension == Config.dimensionCaveworld && player.isSneaking())
		{
			ItemStack current = player.getCurrentEquippedItem();
			Block block = event.block;
			int metadata = event.metadata;

			if (current != null && current.getItem().getToolClasses(current).contains("pickaxe") && CaveOreManager.containsBlock(block, metadata, true))
			{
				int level = CaveUtils.getMiningLevel(player);

				if (level > 2)
				{
					event.newSpeed *= 2.0F;
				}
				else if (level > 0)
				{
					event.newSpeed *= 1.75F;
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.entityPlayer instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
			WorldServer world = player.getServerForPlayer();
			int x = event.x;
			int y = event.y;
			int z = event.z;
			int face = event.face;
			ItemStack current = player.getCurrentEquippedItem();

			if (event.action == Action.RIGHT_CLICK_BLOCK)
			{
				if (!player.isSneaking() && current != null && current.getItem() == Item.getItemFromBlock(Blocks.ender_chest))
				{
					if (face == 0)
					{
						--y;
					}
					else if (face == 1)
					{
						++y;
					}
					else if (face == 2)
					{
						--z;
					}
					else if (face == 3)
					{
						++z;
					}
					else if (face == 4)
					{
						--x;
					}
					else if (face == 5)
					{
						++x;
					}

					if (CaveBlocks.caveworld_portal.func_150000_e(world, x, y, z))
					{
						world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, CaveBlocks.caveworld_portal.stepSound.func_150496_b(), 1.0F, 2.0F);

						event.setCanceled(true);
					}
				}
				else if (player.dimension == Config.dimensionCaveworld && world.getBlock(x, y, z).isBed(world, x, y, z, player))
				{
					int metadata = world.getBlockMetadata(x, y, z);

					if (!BlockBed.isBlockHeadOfBed(metadata))
					{
						int var1 = BlockBed.getDirection(metadata);
						x += BlockBed.field_149981_a[var1][0];
						z += BlockBed.field_149981_a[var1][1];

						if (!world.getBlock(x, y, z).isBed(world, x, y, z, player))
						{
							return;
						}

						metadata = world.getBlockMetadata(x, y, z);
					}

					if (BlockBed.func_149976_c(metadata))
					{
						for (Object obj : world.playerEntities)
						{
							EntityPlayer target = (EntityPlayer)obj;

							if (target.isPlayerSleeping())
							{
								ChunkCoordinates coord = target.playerLocation;

								if (coord.posX == x && coord.posY == y && coord.posZ == z)
								{
									player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.occupied"));

									return;
								}
							}
						}

						BlockBed.func_149979_a(world, x, y, z, false);
					}

					EnumStatus status = player.sleepInBedAt(x, y, z);

					if (status == EnumStatus.OK)
					{
						BlockBed.func_149979_a(world, x, y, z, true);
					}
					else
					{
						if (status == EnumStatus.NOT_POSSIBLE_NOW)
						{
							player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.noSleep"));
						}
						else if (status == EnumStatus.NOT_SAFE)
						{
							player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe"));
						}
					}

					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerSleepInBed(PlayerSleepInBedEvent event)
	{
		if (event.entityPlayer instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
			WorldServer world = player.getServerForPlayer();

			if (player.dimension == Config.dimensionCaveworld && !player.capabilities.isCreativeMode)
			{
				NBTTagCompound data = player.getEntityData();
				ChunkCoordinates spawn = player.getBedLocation(player.dimension);

				if (data.getLong("Caveworld:LastSleepTime") + 6000L > world.getTotalWorldTime())
				{
					event.result = EnumStatus.OTHER_PROBLEM;
				}
				else if (spawn != null && player.getDistance(spawn.posX, spawn.posY, spawn.posZ) <= 32.0D)
				{
					event.result = EnumStatus.OTHER_PROBLEM;
				}

				if (event.result == null)
				{
					data.setLong("Caveworld:LastSleepTime", world.getTotalWorldTime());
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		World world = event.world;
		Entity entity = event.entity;

		if (!world.isRemote && entity.dimension == Config.dimensionCaveworld)
		{
			if (entity instanceof EntityLiving && MathHelper.floor_double(entity.posY) >= world.provider.getActualHeight())
			{
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		if (event.entityLiving.dimension == Config.dimensionCaveworld && event.entityLiving.ticksExisted % 20 == 0)
		{
			if (event.entityLiving instanceof EntityPlayerMP)
			{
				EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;

				if (player.isPlayerSleeping() && player.getSleepTimer() >= 80)
				{
					player.wakeUpPlayer(true, true, false);

					player.setSpawnChunk(player.playerLocation, true, player.dimension);
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event)
	{
		Random random = new SecureRandom();
		EntityLivingBase living = event.entityLiving;
		World world = living.worldObj;
		double posX = living.posX;
		double posY = living.posY;
		double posZ = living.posZ;
		int looting = event.lootingLevel;

		if (!world.isRemote && living.dimension == Config.dimensionCaveworld)
		{
			if (living instanceof EntityBat)
			{
				event.drops.add(new EntityItem(world, posX, posY + 0.5D, posZ, new ItemStack(Items.coal, random.nextInt(3) + Math.min(looting, 3))));
			}
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event)
	{
		World world = event.world;

		if (!world.isRemote && world.provider.dimensionId == Config.dimensionCaveworld)
		{
			WorldProviderCaveworld.writeDimData();
			WorldProviderCaveworld.clearDimData();
		}
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event)
	{
		World world = event.world;

		if (!world.isRemote && world.provider.dimensionId == Config.dimensionCaveworld)
		{
			CaveBlocks.caveworld_portal.getInventory().saveInventoryToNBT();
		}
	}

	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event)
	{
		if (event.side.isServer() && event.phase == Phase.END)
		{
			World world = event.world;
			int dimension = world.provider.dimensionId;

			if (dimension == Config.dimensionCaveworld && world.getTotalWorldTime() % 15000L == 0L && world.rand.nextBoolean())
			{
				Caveworld.packetPipeline.sendPacketToAllInDimension(new PlayCaveSoundPacket("ambient.cave"), dimension);
			}
		}
	}
}