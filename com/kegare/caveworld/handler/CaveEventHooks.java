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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.core.CaveAchievementList;
import com.kegare.caveworld.core.CaveBiomeManager;
import com.kegare.caveworld.core.CaveMiningPlayer;
import com.kegare.caveworld.core.CaveOreManager;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.network.BiomeSyncMessage;
import com.kegare.caveworld.network.CaveSoundMessage;
import com.kegare.caveworld.network.ConfigSyncMessage;
import com.kegare.caveworld.network.DimSyncMessage;
import com.kegare.caveworld.network.OreSyncMessage;
import com.kegare.caveworld.network.VersionNotifyMessage;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.world.TeleporterCaveworld;
import com.kegare.caveworld.world.WorldProviderCaveworld;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CaveEventHooks
{
	public static final CaveEventHooks instance = new CaveEventHooks();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.thePlayer != null && mc.thePlayer.dimension == Config.dimensionCaveworld)
		{
			if (mc.gameSettings.showDebugInfo)
			{
				event.left.add("dim: Caveworld");
			}
			else if ((mc.gameSettings.advancedItemTooltips || mc.thePlayer.isSneaking()) && CaveUtils.isItemPickaxe(mc.thePlayer.getHeldItem()))
			{
				CaveMiningPlayer data = CaveMiningPlayer.get(mc.thePlayer);
				int count = data.getMiningCount();

				if (count > 0)
				{
					int level = data.getMiningLevel();
					StringBuilder builder = new StringBuilder();

					builder.append(I18n.format("caveworld.mining.count")).append(": ").append(count);

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

			Caveworld.network.sendTo(new ConfigSyncMessage(), player);
			Caveworld.network.sendTo(new VersionNotifyMessage(), player);
			Caveworld.network.sendTo(new DimSyncMessage(), player);
			Caveworld.network.sendTo(new BiomeSyncMessage(CaveBiomeManager.getCaveBiomes()), player);
			Caveworld.network.sendTo(new OreSyncMessage(CaveOreManager.getCaveOres()), player);
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			int dim = player.dimension;

			if (dim == Config.dimensionCaveworld)
			{
				ChunkCoordinates spawn = player.getBedLocation(dim);

				if (spawn == null)
				{
					MinecraftServer server = Caveworld.proxy.getServer();
					WorldServer world = server.worldServerForDimension(0);
					Teleporter teleporter = new TeleporterCaveworld(world, false, false);

					server.getConfigurationManager().transferPlayerToDimension(player, 0, teleporter);
				}
				else if (!Config.hardcoreEnabled)
				{
					int level = CaveMiningPlayer.get(player).getMiningLevel();

					if (level <= 0 || player.getRNG().nextInt(level) == 0)
					{
						player.setSpawnChunk(null, true, dim);
					}
				}
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
					Caveworld.network.sendTo(new CaveSoundMessage("caveworld:ambient.cave"), player);
				}

				data.setLong("Caveworld:LastTeleportTime", world.getTotalWorldTime());

				player.triggerAchievement(CaveAchievementList.caveworld);
			}
		}
	}

	@SubscribeEvent
	public void onHarvestDrops(HarvestDropsEvent event)
	{
		if (event.harvester != null && event.harvester instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.harvester;

			if (player.dimension == Config.dimensionCaveworld)
			{
				ItemStack current = player.getCurrentEquippedItem();
				Block block = event.block;
				int metadata = event.blockMetadata;

				if (CaveUtils.isItemPickaxe(current) && CaveUtils.isOreBlock(block, metadata))
				{
					CaveMiningPlayer.get(player).addMiningCount(1);
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

			if (CaveUtils.isItemPickaxe(current) && CaveUtils.isOreBlock(block, metadata))
			{
				int level = CaveMiningPlayer.get(player).getMiningLevel();

				if (level > 0)
				{
					event.newSpeed *= Math.min(1.25F + 0.25F * level, 3.0F);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.entityPlayer instanceof EntityPlayerMP && event.action == Action.RIGHT_CLICK_BLOCK)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
			ItemStack current = player.getCurrentEquippedItem();
			WorldServer world = player.getServerForPlayer();
			int x = event.x;
			int y = event.y;
			int z = event.z;
			int face = event.face;

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
	public void onEntityConstructing(EntityConstructing event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entity;

			if (CaveMiningPlayer.get(player) == null)
			{
				CaveMiningPlayer.register(player);
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		Entity entity = event.entity;
		World world = event.world;

		if (entity instanceof EntityPlayerMP)
		{
			CaveMiningPlayer.loadMiningData((EntityPlayerMP)entity);
		}

		if (entity.dimension == Config.dimensionCaveworld)
		{
			if (entity instanceof EntityBat)
			{
				entity.getEntityData().setBoolean("Caveworld:CaveBat", true);
			}

			if (entity instanceof EntityLiving)
			{
				if (entity.posY >= world.provider.getActualHeight() - 1)
				{
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase living = event.entityLiving;

		if (living.dimension == Config.dimensionCaveworld && living.ticksExisted % 20 == 0)
		{
			if (living instanceof EntityPlayerMP)
			{
				EntityPlayerMP player = (EntityPlayerMP)living;

				if (player.isPlayerSleeping() && player.getSleepTimer() >= 80)
				{
					player.wakeUpPlayer(true, true, false);

					player.setSpawnChunk(player.playerLocation, true, player.dimension);
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event)
	{
		if (event.entityLiving instanceof EntityPlayerMP)
		{
			if (!Config.deathLoseMiningCount)
			{
				CaveMiningPlayer.saveMiningData((EntityPlayerMP)event.entityLiving);
			}
		}
	}

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event)
	{
		EntityLivingBase living = event.entityLiving;
		Entity entity = event.source.getEntity();

		if (entity != null && entity instanceof EntityPlayerMP)
		{
			Random random = living.getRNG();
			int looting = MathHelper.clamp_int(event.lootingLevel, 0, 3);

			if (living instanceof EntityBat && living.getEntityData().getBoolean("Caveworld:CaveBat"))
			{
				EntityItem item = new EntityItem(living.worldObj, living.posX, living.posY + 0.5D, living.posZ);
				item.delayBeforeCanPickup = 10;

				if (random.nextInt(4) <= looting)
				{
					item.setEntityItemStack(new ItemStack(Items.leather));
				}
				else
				{
					item.setEntityItemStack(new ItemStack(Items.coal, random.nextInt(3) + looting));
				}

				event.drops.add(item);
			}
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		World world = event.world;

		if (!world.isRemote && world.provider.dimensionId == Config.dimensionCaveworld)
		{
			CaveBlocks.caveworld_portal.inventory.loadInventoryFromNBT();
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
			CaveBlocks.caveworld_portal.inventory.saveInventoryToNBT();
		}
	}
}