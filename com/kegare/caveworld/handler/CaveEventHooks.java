/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.handler;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.lwjgl.opengl.GL11;

import shift.mceconomy2.api.MCEconomyAPI;

import com.google.common.collect.Sets;
import com.google.common.collect.Table.Cell;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.core.CaveAchievementList;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.entity.EntityCaveman;
import com.kegare.caveworld.item.ItemCavenium;
import com.kegare.caveworld.item.ItemMiningPickaxe;
import com.kegare.caveworld.network.BuffMessage;
import com.kegare.caveworld.network.CaveAchievementMessage;
import com.kegare.caveworld.network.CaveSoundMessage;
import com.kegare.caveworld.network.DimSyncMessage;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.util.Version.Status;
import com.kegare.caveworld.util.breaker.AditBreakExecutor;
import com.kegare.caveworld.util.breaker.BreakPos;
import com.kegare.caveworld.util.breaker.MultiBreakExecutor;
import com.kegare.caveworld.util.breaker.QuickBreakExecutor;
import com.kegare.caveworld.util.breaker.RangedBreakExecutor;
import com.kegare.caveworld.world.WorldProviderCaveworld;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CaveEventHooks
{
	public static final CaveEventHooks instance = new CaveEventHooks();

	public static final Set<String> firstJoinPlayers = Sets.newHashSet();

	@SideOnly(Side.CLIENT)
	private BreakPos breakingPos;
	@SideOnly(Side.CLIENT)
	private long triggerTime;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if (Caveworld.MODID.equals(event.modID) && !event.isWorldRunning)
		{
			switch (event.configID)
			{
				case Configuration.CATEGORY_GENERAL:
					Config.syncGeneralCfg();
					break;
				case "blocks":
					Config.syncBlocksCfg();
					break;
				case "items":
					Config.syncItemsCfg();
					break;
				case "entities":
					Config.syncEntitiesCfg();
					break;
				case "dimension":
					Config.syncDimensionCfg();
					break;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)
	{
		if (event.phase != Phase.END)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.thePlayer != null && mc.objectMouseOver != null)
		{
			switch (mc.objectMouseOver.typeOfHit)
			{
				case BLOCK:
					ItemStack current = mc.thePlayer.getCurrentEquippedItem();

					if (current != null && current.getItem() != null && current.getItem() instanceof ItemMiningPickaxe && current.getItemDamage() < current.getMaxDamage())
					{
						if (mc.thePlayer.capabilities.isCreativeMode)
						{
							return;
						}

						int x = mc.objectMouseOver.blockX;
						int y = mc.objectMouseOver.blockY;
						int z = mc.objectMouseOver.blockZ;
						ItemMiningPickaxe pickaxe = (ItemMiningPickaxe)current.getItem();

						if (mc.thePlayer.isSwingInProgress && (breakingPos == null || breakingPos.x != x || breakingPos.y != y || breakingPos.z != z))
						{
							breakingPos = null;
							triggerTime = 0;

							Block block = mc.theWorld.getBlock(x, y, z);
							int meta = mc.theWorld.getBlockMetadata(x, y, z);

							if (pickaxe.canBreak(current, block, meta))
							{
								switch (pickaxe.getMode(current))
								{
									case QUICK:
										QuickBreakExecutor.getExecutor(mc.theWorld, mc.thePlayer).setOriginPos(x, y, z).setBreakable(block, meta).setBreakPositions();
										return;
									case ADIT:
										AditBreakExecutor.getExecutor(mc.theWorld, mc.thePlayer).setOriginPos(x, y, z).setBreakable(block, meta).setBreakPositions();
										return;
									case RANGED:
										RangedBreakExecutor.getExecutor(mc.theWorld, mc.thePlayer).setOriginPos(x, y, z).setBreakable(block, meta).setBreakPositions();
										return;
									default:
								}

								breakingPos = new BreakPos(mc.theWorld, x, y, z);
								triggerTime = System.nanoTime();
							}
							else
							{
								QuickBreakExecutor.getExecutor(mc.theWorld, mc.thePlayer).clear();
								RangedBreakExecutor.getExecutor(mc.theWorld, mc.thePlayer).clear();

								breakingPos = null;
								triggerTime = 0;
							}
						}

						if (breakingPos != null)
						{
							Block block = breakingPos.getCurrentBlock();

							if (block == null || breakingPos.isPlaced() || block.isAir(mc.theWorld, x, y, z))
							{
								breakingPos = null;
								triggerTime = 0;
							}
						}
					}

					if (System.nanoTime() - triggerTime >= 10000000000L)
					{
						breakingPos = null;
						triggerTime = 0;
					}

					break;
				case ENTITY:
					Entity entity = mc.objectMouseOver.entityHit;

					if (CaveworldAPI.isEntityInCaveworld(entity) && entity instanceof EntityCaveman && !((EntityCaveman)entity).isTamed())
					{
						if (!mc.thePlayer.getStatFileWriter().hasAchievementUnlocked(CaveAchievementList.caveman))
						{
							Caveworld.network.sendToServer(new CaveAchievementMessage(CaveAchievementList.caveman));
						}
					}

					break;
				default:
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event)
	{
		if (event.phase != Phase.END)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();
		ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

		if (mc.thePlayer != null)
		{
			ItemStack current = mc.thePlayer.getCurrentEquippedItem();

			if (current != null && current.getItem() != null && current.getItem() instanceof ItemMiningPickaxe)
			{
				ItemMiningPickaxe item = (ItemMiningPickaxe)current.getItem();

				if (item.highlightTicks > 0)
				{
					GL11.glPushMatrix();
					GL11.glEnable(GL11.GL_BLEND);
					OpenGlHelper.glBlendFunc(770, 771, 1, 0);
					mc.fontRenderer.drawStringWithShadow(item.getModeInfomation(current), 18, resolution.getScaledHeight() - 20, 0xEEEEEE);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glPopMatrix();

					--item.highlightTicks;
				}
				else
				{
					int highlight = ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, mc.ingameGUI, "remainingHighlightTicks", "field_92017_k");

					if (highlight == 40)
					{
						item.highlightTicks = 800;
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderGameTextOverlay(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc == null ? null : mc.thePlayer;

		if (CaveworldAPI.isEntityInCaveworld(player))
		{
			if (mc.gameSettings.showDebugInfo)
			{
				if (player.dimension == CaveworldAPI.getDimension())
				{
					event.left.add("dim: Caveworld");
				}
				else if (player.dimension == CaveworldAPI.getDeepDimension())
				{
					event.left.add("dim: Deep Caveworld");
				}
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

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientDisconnected(ClientDisconnectionFromServerEvent event)
	{
		Caveworld.tabCaveworld.tabIconItem = null;
	}

	@SubscribeEvent
	public void onServerConnected(ServerConnectionFromClientEvent event)
	{
		event.manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new DimSyncMessage(CaveworldAPI.getDimension(), WorldProviderCaveworld.getDimData())));
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			WorldServer world = player.getServerForPlayer();

			if (CaveworldAPI.isEntityInCaveworld(player))
			{
				if (MathHelper.floor_double(player.posY) >= world.getActualHeight() - 1)
				{
					CaveUtils.forceTeleport(player, player.dimension, false);
				}
			}
			else
			{
				if (Config.caveborn && firstJoinPlayers.contains(player.getGameProfile().getId().toString()))
				{
					Set<ItemStack> bonus = Sets.newHashSet();

					bonus.add(new ItemStack(Items.stone_pickaxe));
					bonus.add(new ItemStack(Items.stone_sword));
					bonus.add(new ItemStack(Blocks.torch, MathHelper.getRandomIntegerInRange(world.rand, 10, 20)));

					if (world.difficultySetting.getDifficultyId() <= EnumDifficulty.NORMAL.getDifficultyId())
					{
						bonus.add(new ItemStack(Items.apple, MathHelper.getRandomIntegerInRange(world.rand, 5, 10)));
					}

					for (int i = 0; i < 5; ++i)
					{
						bonus.add(new ItemStack(Blocks.sapling, MathHelper.getRandomIntegerInRange(world.rand, 2, 5), i));
					}

					for (ItemStack stack : bonus)
					{
						player.inventory.addItemStackToInventory(stack);
					}

					CaveUtils.forceTeleport(player, CaveworldAPI.getDimension(), false);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		EntityPlayer player = event.player;
		String key = player.getGameProfile().getId().toString();

		firstJoinPlayers.remove(key);

		for (Iterator<Cell<World, EntityPlayer, QuickBreakExecutor>> iterator = QuickBreakExecutor.executors.cellSet().iterator(); iterator.hasNext();)
		{
			if (iterator.next().getColumnKey().getGameProfile().getId().toString().equals(key))
			{
				iterator.remove();
			}
		}

		for (Iterator<Cell<World, EntityPlayer, AditBreakExecutor>> iterator = AditBreakExecutor.executors.cellSet().iterator(); iterator.hasNext();)
		{
			if (iterator.next().getColumnKey().getGameProfile().getId().toString().equals(key))
			{
				iterator.remove();
			}
		}

		for (Iterator<Cell<World, EntityPlayer, RangedBreakExecutor>> iterator = RangedBreakExecutor.executors.cellSet().iterator(); iterator.hasNext();)
		{
			if (iterator.next().getColumnKey().getGameProfile().getId().toString().equals(key))
			{
				iterator.remove();
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

			if (event.toDim == CaveworldAPI.getDimension() || event.toDim == CaveworldAPI.getDeepDimension())
			{
				NBTTagCompound data = player.getEntityData();

				if (!player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.caveworld) || data.getLong("Caveworld:LastTeleportTime") + 18000L < world.getTotalWorldTime())
				{
					String name;

					if (world.rand.nextInt(3) == 0)
					{
						name = "ambient.cave";
					}
					else
					{
						name = "ambient.unrest";
					}

					Caveworld.network.sendTo(new CaveSoundMessage(new ResourceLocation("caveworld", name)), player);
				}

				data.setLong("Caveworld:LastTeleportTime", world.getTotalWorldTime());

				player.triggerAchievement(CaveAchievementList.caveworld);
			}

			int point = CaveworldAPI.getMiningPoint(player);

			if (event.toDim == CaveworldAPI.getDeepDimension())
			{
				if (point < 100)
				{
					CaveUtils.forceTeleport(player, event.fromDim, false);
				}
				else
				{
					CaveworldAPI.addMiningPoint(player, -100);

					player.triggerAchievement(CaveAchievementList.deepCaves);
				}
			}
			else if (event.fromDim == CaveworldAPI.getDeepDimension())
			{
				ItemStack current = player.getCurrentEquippedItem();

				if (current != null && current.getItem() != null)
				{
					UniqueIdentifier unique = GameRegistry.findUniqueIdentifierFor(current.getItem());

					if (unique != null && unique.modId.equals("Wa") && unique.name.equals("magatama"))
					{
						return;
					}
				}

				int req = 10000;

				if (player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.backFromDeep))
				{
					req /= 2;
				}

				if (point < req)
				{
					CaveUtils.forceTeleport(player, event.fromDim, false);
				}
				else
				{
					CaveworldAPI.addMiningPoint(player, -req);

					player.triggerAchievement(CaveAchievementList.backFromDeep);
				}
			}

			if (Config.hardcore)
			{
				if (event.toDim != CaveworldAPI.getDimension() && event.toDim != CaveworldAPI.getDeepDimension())
				{
					CaveUtils.forceTeleport(player, event.fromDim, false);
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemPickup(ItemPickupEvent event)
	{
		EntityPlayer player = event.player;
		EntityItem entity = event.pickedUp;
		World world = entity.worldObj;
		ItemStack itemstack = entity.getEntityItem();
		EntityPlayer thrower = null;

		if (entity.func_145800_j() != null)
		{
			thrower = world.getPlayerEntityByName(entity.func_145800_j());
		}

		if (itemstack.getItem() instanceof ItemCavenium)
		{
			if (thrower == null)
			{
				player.triggerAchievement(CaveAchievementList.cavenium);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoadFromFile(PlayerEvent.LoadFromFile event)
	{
		for (String str : event.playerDirectory.list())
		{
			if (str.startsWith(event.playerUUID))
			{
				return;
			}
		}

		firstJoinPlayers.add(event.playerUUID);
	}

	@SubscribeEvent
	public void onHarvestDrops(HarvestDropsEvent event)
	{
		if (event.harvester != null && event.harvester instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.harvester;

			if (CaveworldAPI.isEntityInCaveworld(player))
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
		if (event.entityPlayer instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
			ItemStack current = player.getCurrentEquippedItem();
			WorldServer world = player.getServerForPlayer();
			int x = event.x;
			int y = event.y;
			int z = event.z;
			int face = event.face;

			if (event.action == Action.LEFT_CLICK_BLOCK)
			{
				if (current != null && current.getItem() != null && current.getItem() instanceof ItemMiningPickaxe && current.getItemDamage() < current.getMaxDamage())
				{
					ItemMiningPickaxe pickaxe = (ItemMiningPickaxe)current.getItem();
					Block block = world.getBlock(x, y, z);
					int meta = world.getBlockMetadata(x, y, z);

					if (pickaxe.canBreak(current, block, meta))
					{
						MultiBreakExecutor executor = null;

						switch (pickaxe.getMode(current))
						{
							case QUICK:
								executor = QuickBreakExecutor.getExecutor(world, player).setOriginPos(x, y, z).setBreakable(block, meta).setBreakPositions();
								break;
							case ADIT:
								executor = AditBreakExecutor.getExecutor(world, player).setOriginPos(x, y, z).setBreakable(block, meta).setBreakPositions();
								break;
							case RANGED:
								executor = RangedBreakExecutor.getExecutor(world, player).setOriginPos(x, y, z).setBreakable(block, meta).setBreakPositions();
								break;
							default:
						}

						if (player.capabilities.isCreativeMode && executor != null)
						{
							executor.breakAll();
						}
					}
					else
					{
						QuickBreakExecutor.getExecutor(world, player).clear();
						RangedBreakExecutor.getExecutor(world, player).clear();
					}
				}
			}
			else if (event.action == Action.RIGHT_CLICK_BLOCK)
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

						if (!player.capabilities.isCreativeMode && --current.stackSize <= 0)
						{
							player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
						}

						player.triggerAchievement(CaveAchievementList.portal);

						event.setCanceled(true);
					}
				}
				else if (player.dimension == CaveworldAPI.getDimension() && world.getBlock(x, y, z).isBed(world, x, y, z, player))
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
	public void onBreakSpeed(BreakSpeed event)
	{
		EntityPlayer player = event.entityPlayer;
		ItemStack current = player.getCurrentEquippedItem();

		if (current != null && current.getItem() != null && current.getItem() instanceof ItemMiningPickaxe)
		{
			ItemMiningPickaxe pickaxe = (ItemMiningPickaxe)current.getItem();
			MultiBreakExecutor executor;

			switch (pickaxe.getMode(current))
			{
				case QUICK:
					executor = QuickBreakExecutor.getExecutor(player.worldObj, player);
					break;
				case ADIT:
					executor = AditBreakExecutor.getExecutor(player.worldObj, player);
					break;
				case RANGED:
					executor = RangedBreakExecutor.getExecutor(player.worldObj, player);
					break;
				default:
					executor = null;
					break;
			}

			if (executor != null && !executor.getBreakPositions().isEmpty())
			{
				int count = executor.getBreakPositions().size();
				int raw = pickaxe.getRefined(current);

				if (raw >= 4 || pickaxe.getHarvestLevel(current, "pickaxe") >= 3 && EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, current) >= 4)
				{
					return;
				}

				float refined = raw * 0.1245F;

				event.newSpeed = Math.min(event.originalSpeed / (count * (0.5F - refined)), pickaxe.getDigSpeed(current, event.block, event.metadata));
			}
		}

		if (CaveworldAPI.isEntityInCaveworld(player))
		{
			if (event.y <= 0 || event.y >= player.worldObj.getActualHeight() - 1)
			{
				event.newSpeed /= 20.0F;
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

			if (CaveworldAPI.isEntityInCaveworld(player) && !player.capabilities.isCreativeMode)
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
		else if (entity instanceof EntityLiving && CaveworldAPI.isEntityInCaveworld(entity))
		{
			if (entity.posY >= world.provider.getActualHeight() - 1)
			{
				event.setCanceled(true);
			}

			if (entity instanceof EntityBat)
			{
				entity.getEntityData().setBoolean("Caveworld:CaveBat", true);
			}
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase living = event.entityLiving;

		if (CaveworldAPI.isEntityInCaveworld(living) && living instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)living;

			if (player.dimension == CaveworldAPI.getDeepDimension())
			{
				if (player.boundingBox.maxY >= player.worldObj.provider.getActualHeight())
				{
					if (CaveworldAPI.getMiningPoint(player) >= 10000)
					{
						CaveUtils.forceTeleport(player, CaveworldAPI.getDimension(), true);
					}
					else
					{
						CaveUtils.forceTeleport(player, player.dimension, false);
					}
				}
				else if (player.posY <= 30.0D && player.func_147099_x().canUnlockAchievement(CaveAchievementList.underCaves) && !player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.underCaves))
				{
					MovingObjectPosition pos = CaveUtils.rayTrace(player, 64.0D);

					if (pos == null || pos.typeOfHit != MovingObjectType.BLOCK)
					{
						return;
					}

					WorldServer world = player.getServerForPlayer();
					int x = pos.blockX;
					int y = pos.blockY;
					int z = pos.blockZ;

					while (!world.isAirBlock(x, ++y, z) && world.getBlock(x, y, z).getMaterial().isLiquid())
					{
						;
					}

					if (world.getBlock(x, --y, z) == Blocks.water && y <= 16)
					{
						player.triggerAchievement(CaveAchievementList.underCaves);
					}
				}
			}
			else
			{
				if (player.boundingBox.minY <= 0)
				{
					if (CaveworldAPI.getMiningPoint(player) >= 100)
					{
						CaveUtils.forceTeleport(player, CaveworldAPI.getDeepDimension(), true);
					}
					else
					{
						CaveUtils.forceTeleport(player, player.dimension, false);
					}
				}

				if (player.ticksExisted % 20 == 0)
				{
					if (player.isPlayerSleeping() && player.getSleepTimer() >= 80)
					{
						player.wakeUpPlayer(true, true, false);

						player.setSpawnChunk(player.playerLocation, true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event)
	{
		if (event.entityLiving instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;

			if (!Config.deathLoseMiningPoint)
			{
				CaveworldAPI.saveMiningData(player, null);
			}

			if (player.dimension == CaveworldAPI.getDeepDimension() && player.posY < 16.0D && player.isInWater())
			{
				CaveUtils.forceTeleport(player, CaveworldAPI.getDimension(), false);
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

			if (MCEconomyPlugin.enabled() && CaveworldAPI.isEntityInCaveworld(living) && living instanceof IMob)
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
	public void onWorldLoad(WorldEvent.Load event)
	{
		World world = event.world;

		if (!world.isRemote && world.provider.dimensionId == CaveworldAPI.getDimension())
		{
			CaveBlocks.caveworld_portal.loadInventoryFromDimData();
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event)
	{
		World world = event.world;
		int dim = world.provider.dimensionId;

		if (!world.isRemote && dim == CaveworldAPI.getDimension())
		{
			CaveBlocks.caveworld_portal.saveInventoryToDimData();
			CaveBlocks.caveworld_portal.clearInventory();

			WorldProviderCaveworld.saveDimData();
		}

		for (Iterator<Cell<World, EntityPlayer, QuickBreakExecutor>> iterator = QuickBreakExecutor.executors.cellSet().iterator(); iterator.hasNext();)
		{
			if (iterator.next().getRowKey().provider.dimensionId == dim)
			{
				iterator.remove();
			}
		}

		for (Iterator<Cell<World, EntityPlayer, AditBreakExecutor>> iterator = AditBreakExecutor.executors.cellSet().iterator(); iterator.hasNext();)
		{
			if (iterator.next().getRowKey().provider.dimensionId == dim)
			{
				iterator.remove();
			}
		}

		for (Iterator<Cell<World, EntityPlayer, RangedBreakExecutor>> iterator = RangedBreakExecutor.executors.cellSet().iterator(); iterator.hasNext();)
		{
			if (iterator.next().getRowKey().provider.dimensionId == dim)
			{
				iterator.remove();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientChat(ClientChatReceivedEvent event)
	{
		String message = event.message.getUnformattedTextForChat();
		EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();

		if (player != null && message.startsWith(String.format("<%s> ", player.getCommandSenderName())))
		{
			message = message.substring(message.indexOf(" ") + 1);
		}

		if (message.matches("@buff|@buff ([0-9]*$|max)") && CaveworldAPI.isEntityInCaveworld(player))
		{
			int point = CaveworldAPI.getMiningPoint(player);

			if (message.matches("@buff [0-9]*$"))
			{
				point = MathHelper.clamp_int(Integer.parseInt(message.substring(6)), 0, point);
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

				Caveworld.network.sendToServer(new BuffMessage(new PotionEffect(potion.id, point * 20)));
			}

			event.setCanceled(true);
		}
	}
}