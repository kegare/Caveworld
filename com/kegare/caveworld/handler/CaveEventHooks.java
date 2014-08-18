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
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
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
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.WorldEvent;
import shift.mceconomy2.api.MCEconomyAPI;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.core.CaveAchievementList;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.network.BiomesSyncMessage;
import com.kegare.caveworld.network.CaveSoundMessage;
import com.kegare.caveworld.network.ConfigSyncMessage;
import com.kegare.caveworld.network.DimSyncMessage;
import com.kegare.caveworld.network.VeinsSyncMessage;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.util.Version.Status;
import com.kegare.caveworld.world.TeleporterCaveworld;
import com.kegare.caveworld.world.WorldProviderCaveworld;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CaveEventHooks
{
	public static final CaveEventHooks instance = new CaveEventHooks();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if (Caveworld.MODID.equals(event.modID) && !event.isWorldRunning)
		{
			if (Configuration.CATEGORY_GENERAL.equals(event.configID))
			{
				Config.syncGeneralCfg();
			}
			else if ("blocks".equals(event.configID))
			{
				Config.syncBlocksCfg();
			}
			else if ("dimension".equals(event.configID))
			{
				Config.syncDimensionCfg();
			}
			else if ("biomes".equals(event.configID))
			{
				Config.syncBiomesCfg();
			}
			else if ("veins".equals(event.configID))
			{
				Config.syncVeinsCfg();
			}
			else if (MCEconomyPlugin.enabled() && "shop".equals(event.configID))
			{
				MCEconomyPlugin.syncShopCfg();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderGameTextOverlay(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc == null ? null : mc.thePlayer;

		if (player != null && player.dimension == Config.dimensionCaveworld)
		{
			if (mc.gameSettings.showDebugInfo)
			{
				event.left.add("dim: Caveworld");
			}
			else if (mc.gameSettings.advancedItemTooltips || CaveUtils.isItemPickaxe(player.getHeldItem()))
			{
				event.right.add(I18n.format("caveworld.mining.point") + ": " + CaveworldAPI.getMiningPoint(player));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientConnected(ClientConnectedToServerEvent event)
	{
		if (Version.getStatus() == Status.PENDING || Version.getStatus() == Status.FAILED)
		{
			Version.versionCheck();
		}
		else if (Version.DEV_DEBUG || Config.versionNotify && Version.isOutdated())
		{
			IChatComponent component = new ChatComponentTranslation("caveworld.version.message", EnumChatFormatting.AQUA + "Caveworld" + EnumChatFormatting.RESET);
			component.appendText(" : " + EnumChatFormatting.YELLOW + Version.getLatest());
			component.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Caveworld.metadata.url));

			event.handler.handleChat(new S02PacketChat(component));
		}
	}

	@SubscribeEvent
	public void onServerConnected(ServerConnectionFromClientEvent event)
	{
		NetworkManager manager = event.manager;

		manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new ConfigSyncMessage()));
		manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new DimSyncMessage(WorldProviderCaveworld.getDimData())));
		manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new BiomesSyncMessage(CaveworldAPI.getCaveBiomes())));
		manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new VeinsSyncMessage(CaveworldAPI.getCaveVeins())));
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
				if (player.getBedLocation(dim) == null || player.posY >= player.worldObj.getActualHeight())
				{
					dim = player.getEntityData().getInteger("Caveworld:LastDim");
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					WorldServer world = server.worldServerForDimension(dim);
					Teleporter teleporter = new TeleporterCaveworld(world, false, false);

					server.getConfigurationManager().transferPlayerToDimension(player, dim, teleporter);
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
					Caveworld.network.sendTo(new CaveSoundMessage(new ResourceLocation("caveworld", "ambient.cave")), player);
				}

				data.setLong("Caveworld:LastTeleportTime", world.getTotalWorldTime());

				if (player.func_147099_x().canUnlockAchievement(CaveAchievementList.caveworld))
				{
					player.triggerAchievement(CaveAchievementList.caveworld);
				}
			}
			else if (Config.hardcore && event.fromDim == Config.dimensionCaveworld)
			{
				MinecraftServer server = world.func_73046_m();
				WorldServer worldNew = server.worldServerForDimension(event.fromDim);
				Teleporter teleporter = new TeleporterCaveworld(worldNew);

				player.worldObj.removeEntity(player);
				player.isDead = false;
				player.timeUntilPortal = player.getPortalCooldown();

				server.getConfigurationManager().transferPlayerToDimension(player, event.fromDim, teleporter);

				player.attackEntityFrom(DamageSource.outOfWorld, 999.0F);
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

				if (CaveUtils.isItemPickaxe(current))
				{
					int amount = CaveworldAPI.getMiningPointAmount(block, metadata);

					if (amount == 0)
					{
						if (block instanceof BlockOre || block instanceof BlockRedstoneOre)
						{
							amount = 1;
						}
					}

					if (amount != 0)
					{
						CaveworldAPI.addMiningPoint(player, amount);
					}
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

					if (!player.capabilities.isCreativeMode && --current.stackSize <= 0)
					{
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
					}

					player.triggerAchievement(CaveAchievementList.portal);

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
			CaveworldAPI.getMiningPoint((EntityPlayer)event.entity);
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		Entity entity = event.entity;
		World world = event.world;

		if (entity instanceof EntityPlayerMP)
		{
			CaveworldAPI.loadMiningData((EntityPlayerMP)entity, null);
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
			if (!Config.deathLoseMiningPoint)
			{
				CaveworldAPI.saveMiningData((EntityPlayerMP)event.entityLiving, null);
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
			EntityPlayerMP player = (EntityPlayerMP)entity;
			Random random = living.getRNG();
			int looting = MathHelper.clamp_int(event.lootingLevel, 0, 3);

			if (MCEconomyPlugin.enabled() && living.dimension == Config.dimensionCaveworld && living instanceof IMob)
			{
				if (!MCEconomyAPI.ShopManager.hasEntityPurchase(living))
				{
					MCEconomyAPI.addPlayerMP(player, random.nextInt(3) + Math.max(looting, 1), false);
				}
			}

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
	public void onWorldUnload(WorldEvent.Unload event)
	{
		World world = event.world;

		if (!world.isRemote && world.provider.dimensionId == Config.dimensionCaveworld)
		{
			CaveBlocks.caveworld_portal.saveInventoryToDimData();

			WorldProviderCaveworld.saveDimData();
		}
	}

	@SubscribeEvent
	public void onServerChat(ServerChatEvent event)
	{
		EntityPlayerMP player = event.player;
		String message = event.message.trim();

		if (message.matches("@buff|@buff ([0-9]*$|max)") && player.dimension == Config.dimensionCaveworld)
		{
			int point = CaveworldAPI.getMiningPoint(player);

			if (message.matches("@buff [0-9]*$"))
			{
				point = MathHelper.clamp_int(Integer.valueOf(message.substring(6)), 0, point);
			}
			else if (!message.endsWith("max") && point > 30)
			{
				point = 30;
			}

			if (point > 0)
			{
				Random random = new Random();
				Potion potion = null;

				while (potion == null || potion.isBadEffect() || player.isPotionActive(potion))
				{
					potion = Potion.potionTypes[random.nextInt(Potion.potionTypes.length)];
				}

				CaveworldAPI.addMiningPoint(player, -point);

				player.addPotionEffect(new PotionEffect(potion.id, point * 20));
			}

			event.setCanceled(true);
		}
	}
}