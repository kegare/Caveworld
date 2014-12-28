/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.handler;

import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.OpenGlHelper;
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
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.ServerChatEvent;
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
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.lwjgl.opengl.GL11;

import shift.mceconomy2.api.MCEconomyAPI;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.client.gui.GuiSelectBreakable;
import com.kegare.caveworld.core.CaveAchievementList;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.entity.EntityCaveman;
import com.kegare.caveworld.item.CaveItems;
import com.kegare.caveworld.item.ItemCavenium;
import com.kegare.caveworld.item.ItemMiningPickaxe;
import com.kegare.caveworld.item.ItemMiningPickaxe.BreakMode;
import com.kegare.caveworld.network.client.DimDeepSyncMessage;
import com.kegare.caveworld.network.client.DimSyncMessage;
import com.kegare.caveworld.network.client.MultiBreakCountMessage;
import com.kegare.caveworld.network.client.PlaySoundMessage;
import com.kegare.caveworld.network.server.CaveAchievementMessage;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.util.Version.Status;
import com.kegare.caveworld.util.breaker.MultiBreakExecutor;
import com.kegare.caveworld.world.WorldProviderCaveworld;
import com.kegare.caveworld.world.WorldProviderDeepCaveworld;

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
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CaveEventHooks
{
	public static final CaveEventHooks instance = new CaveEventHooks();

	public static final Set<String> firstJoinPlayers = Sets.newHashSet();

	@SideOnly(Side.CLIENT)
	private ItemStack miningPointItemDefault;

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

		if (mc.thePlayer != null && mc.pointedEntity != null)
		{
			Entity entity = mc.pointedEntity;

			if (CaveworldAPI.isEntityInCaveworld(entity) && entity instanceof EntityCaveman && !((EntityCaveman)entity).isTamed())
			{
				if (!mc.thePlayer.getStatFileWriter().hasAchievementUnlocked(CaveAchievementList.caveman))
				{
					Caveworld.network.sendToServer(new CaveAchievementMessage(CaveAchievementList.caveman));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPostRenderGameOverlay(RenderGameOverlayEvent.Post event)
	{
		if (event.type != ElementType.HOTBAR)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.thePlayer;

		if (player != null)
		{
			ItemStack current = player.getCurrentEquippedItem();

			if (current != null && current.getItem() != null && current.getItem() instanceof ItemMiningPickaxe)
			{
				ItemMiningPickaxe item = (ItemMiningPickaxe)current.getItem();

				if (item.highlightStart > 0 && System.currentTimeMillis() - item.highlightStart < Config.modeDisplayTime)
				{
					long time = System.currentTimeMillis() - item.highlightStart;

					if (time > Config.modeDisplayTime - 255)
					{
						time = item.highlightStart + Config.modeDisplayTime - System.currentTimeMillis();
					}

					int i = MathHelper.clamp_int((int)time, 0, 255);

					if (i > 0)
					{
						GL11.glPushMatrix();
						GL11.glEnable(GL11.GL_BLEND);
						OpenGlHelper.glBlendFunc(770, 771, 1, 0);
						mc.fontRenderer.drawStringWithShadow(item.getModeInfomation(current), 18, event.resolution.getScaledHeight() - 20, 16777215 + (i << 24));
						GL11.glDisable(GL11.GL_BLEND);
						GL11.glPopMatrix();
					}
				}
				else
				{
					int highlight = ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, mc.ingameGUI, "remainingHighlightTicks", "field_92017_k");

					if (highlight == 40)
					{
						item.highlightStart = System.currentTimeMillis();
					}
				}
			}

			if (CaveworldAPI.isEntityInCaveworld(player) && (mc.currentScreen == null || GuiChat.class.isInstance(mc.currentScreen)) &&
				(player.capabilities.isCreativeMode || mc.gameSettings.advancedItemTooltips || CaveUtils.isItemPickaxe(current) || CaveUtils.isMiningPointValidItem(current)))
			{
				String str = Integer.toString(CaveworldAPI.getMiningPoint(mc.thePlayer));
				int x = event.resolution.getScaledWidth() - 20;
				int y;
				boolean left = false;
				int type = Config.miningPointRenderType;

				if (type == 2 || type == 3)
				{
					x = 5;
					left = true;
				}

				if (type == 1 || type == 3)
				{
					y = event.resolution.getScaledHeight() - 21;
				}
				else
				{
					y = 5;
				}

				ItemStack icon;

				if (CaveUtils.isItemPickaxe(current))
				{
					icon = current;
				}
				else
				{
					if (miningPointItemDefault == null)
					{
						miningPointItemDefault = new ItemStack(Items.stone_pickaxe);
					}

					icon = miningPointItemDefault;
				}

				CaveUtils.renderItemStack(mc, icon, x, y, true, false, null);

				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);

				if (left)
				{
					mc.fontRenderer.drawStringWithShadow(str, x + 5, y + 9, 0xCECECE);
				}
				else
				{
					mc.fontRenderer.drawStringWithShadow(str, x + 17 - mc.fontRenderer.getStringWidth(str), y + 9, 0xCECECE);
				}

				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glPopMatrix();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderGameTextOverlay(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.thePlayer;

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
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMouse(MouseEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityClientPlayerMP player = mc.thePlayer;

		if (player != null)
		{
			ItemStack current = player.getCurrentEquippedItem();

			if (current != null && current.getItem() == CaveItems.mining_pickaxe)
			{
				ItemMiningPickaxe pickaxe = (ItemMiningPickaxe)current.getItem();

				if (pickaxe.getMode(current) != BreakMode.NORMAL)
				{
					if (event.button == java.awt.event.MouseEvent.BUTTON2)
					{
						mc.displayGuiScreen(new GuiSelectBreakable(current));

						event.setCanceled(true);
					}
				}
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

			FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().printChatMessage(component);
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
		NetworkManager manager = event.manager;

		manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new DimSyncMessage(CaveworldAPI.getDimension(), WorldProviderCaveworld.getDimData())));
		manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new DimDeepSyncMessage(CaveworldAPI.getDeepDimension(), WorldProviderDeepCaveworld.getDimData())));
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
					CaveUtils.teleportPlayer(player, player.dimension);
				}
			}
			else
			{
				if (Config.caveborn && firstJoinPlayers.contains(player.getGameProfile().getId().toString()))
				{
					List<ItemStack> bonus = Lists.newArrayList();

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

					bonus.add(new ItemStack(Blocks.crafting_table));

					for (ItemStack itemstack : bonus)
					{
						player.inventory.addItemStackToInventory(itemstack);
					}

					CaveUtils.teleportPlayer(player, CaveworldAPI.getDimension());

					if (player.getBedLocation(player.dimension) == null)
					{
						player.setSpawnChunk(player.getPlayerCoordinates(), true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		EntityPlayer player = event.player;

		firstJoinPlayers.remove(player.getGameProfile().getId().toString());

		for (BreakMode mode : BreakMode.values())
		{
			mode.clear(player);
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			WorldServer world = player.getServerForPlayer();

			if (Config.hardcore && event.toDim != CaveworldAPI.getDimension() && (!CaveworldAPI.isDeepExist() || event.toDim != CaveworldAPI.getDeepDimension()))
			{
				CaveUtils.teleportPlayer(player, event.fromDim);

				return;
			}

			if (event.toDim == CaveworldAPI.getDimension() || CaveworldAPI.isDeepExist() && event.toDim == CaveworldAPI.getDeepDimension())
			{
				NBTTagCompound data = player.getEntityData();

				if (!player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.caveworld) || data.getLong("Caveworld:LastTeleportTime") + 18000L < world.getTotalWorldTime())
				{
					String name = "ambient.unrest";

					if (world.rand.nextInt(3) == 0)
					{
						name = "ambient.cave";
					}

					Caveworld.network.sendTo(new PlaySoundMessage(new ResourceLocation("caveworld", name)), player);
				}

				data.setLong("Caveworld:LastTeleportTime", world.getTotalWorldTime());

				if (CaveworldAPI.isDeepExist() && event.toDim == CaveworldAPI.getDeepDimension())
				{
					if (player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.theMiner))
					{
						player.triggerAchievement(CaveAchievementList.deepCaves);
					}
					else
					{
						CaveUtils.teleportPlayer(player, event.fromDim);
					}
				}
				else
				{
					player.triggerAchievement(CaveAchievementList.caveworld);
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

		if (!Strings.isNullOrEmpty(entity.func_145800_j()))
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
		String uuid = event.playerUUID;

		for (String str : event.playerDirectory.list())
		{
			if (str.startsWith(uuid))
			{
				return;
			}
		}

		firstJoinPlayers.add(uuid);
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event)
	{
		if (event.getPlayer() != null && event.getPlayer() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.getPlayer();

			if (CaveworldAPI.isEntityInCaveworld(player))
			{
				ItemStack current = player.getCurrentEquippedItem();

				if (CaveUtils.isMiningPointValidItem(current))
				{
					Block block = event.block;
					int amount = CaveworldAPI.getMiningPointAmount(block, event.blockMetadata);

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

					if (pickaxe.canBreak(current, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z)))
					{
						MultiBreakExecutor executor = pickaxe.getMode(current).getExecutor(player);

						if (executor != null)
						{
							executor.setOriginPos(x, y, z).setBreakPositions();

							if (player.capabilities.isCreativeMode)
							{
								executor.breakAll();
							}
							else
							{
								int size = executor.getBreakPositions().size();

								MultiBreakExecutor.positionsCount.set(size);
								Caveworld.network.sendTo(new MultiBreakCountMessage(size), player);
							}
						}
					}
					else
					{
						pickaxe.getMode(current).clear(player);

						MultiBreakExecutor.positionsCount.set(0);
						Caveworld.network.sendTo(new MultiBreakCountMessage(0), player);
					}
				}
			}
			else if (event.action == Action.RIGHT_CLICK_BLOCK)
			{
				if (!player.isSneaking() && current != null && current.getItem() == Item.getItemFromBlock(Blocks.ender_chest))
				{
					switch (face)
					{
						case 0:
							--y;
							break;
						case 1:
							++y;
							break;
						case 2:
							--z;
							break;
						case 3:
							++z;
							break;
						case 4:
							--x;
							break;
						case 5:
							++x;
							break;
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
				else if (CaveworldAPI.isEntityInCaveworld(player) && world.getBlock(x, y, z).isBed(world, x, y, z, player))
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

			if (player.ticksExisted % 20 == 0)
			{
				if (player.isPlayerSleeping() && player.getSleepTimer() >= 80)
				{
					player.wakeUpPlayer(true, true, false);

					player.setSpawnChunk(player.playerLocation, true);
				}
			}

			if (CaveworldAPI.isDeepExist())
			{
				if (player.dimension == CaveworldAPI.getDeepDimension())
				{
					if (player.boundingBox.maxY >= player.worldObj.provider.getActualHeight())
					{
						CaveUtils.teleportPlayer(player, CaveworldAPI.getDimension());
					}
				}
				else
				{
					if (player.boundingBox.minY <= 0)
					{
						if (player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.theMiner))
						{
							CaveUtils.teleportPlayer(player, CaveworldAPI.getDeepDimension());
						}
						else
						{
							CaveUtils.teleportPlayer(player, player.dimension);
						}
					}
				}
			}

			if (player.posY <= 30.0D && !player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.underCaves))
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

		if (!world.isRemote)
		{
			if (dim == CaveworldAPI.getDimension())
			{
				CaveBlocks.caveworld_portal.saveInventoryToDimData();
				CaveBlocks.caveworld_portal.clearInventory();

				WorldProviderCaveworld.saveDimData();
			}
			else if (CaveworldAPI.isDeepExist() && dim == CaveworldAPI.getDeepDimension())
			{
				WorldProviderDeepCaveworld.saveDimData();
			}
		}
	}

	@SubscribeEvent
	public void onServerChat(ServerChatEvent event)
	{
		String message = event.message;
		EntityPlayerMP player = event.player;

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
				Potion potion = null;

				while (potion == null || potion.getEffectiveness() <= 0.5D || player.isPotionActive(potion))
				{
					potion = Potion.potionTypes[player.getRNG().nextInt(Potion.potionTypes.length)];
				}

				if (potion != null)
				{
					CaveworldAPI.addMiningPoint(player, -point);

					player.addPotionEffect(new PotionEffect(potion.id, point * 20));
				}
			}

			event.setCanceled(true);
		}
	}
}