/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.handler;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveBiomeManager;
import caveworld.api.ICaveVeinManager;
import caveworld.api.event.MiningPointEvent;
import caveworld.block.CaveBlocks;
import caveworld.client.gui.GuiSelectBreakable;
import caveworld.core.CaveAchievementList;
import caveworld.core.CaveBiomeManager;
import caveworld.core.CaveVeinManager;
import caveworld.core.CavernBiomeManager;
import caveworld.core.CavernVeinManager;
import caveworld.core.Caveworld;
import caveworld.core.Config;
import caveworld.entity.EntityCaveman;
import caveworld.inventory.InventoryCaverBackpack;
import caveworld.item.CaveItems;
import caveworld.item.IAquamarineTool;
import caveworld.item.ICaveniumTool;
import caveworld.item.ItemCavenicBow;
import caveworld.item.ItemCavenicBow.BowMode;
import caveworld.item.ItemCavenium;
import caveworld.item.ItemCaverBackpack;
import caveworld.item.ItemDiggingShovel;
import caveworld.item.ItemLumberingAxe;
import caveworld.item.ItemMiningPickaxe;
import caveworld.network.client.BiomeAdjustMessage;
import caveworld.network.client.CavernAdjustMessage;
import caveworld.network.client.CaveworldAdjustMessage;
import caveworld.network.client.MultiBreakCountMessage;
import caveworld.network.client.PlaySoundMessage;
import caveworld.network.client.VeinAdjustMessage;
import caveworld.network.server.CaveAchievementMessage;
import caveworld.plugin.enderstorage.EnderStoragePlugin;
import caveworld.plugin.mceconomy.MCEconomyPlugin;
import caveworld.plugin.mceconomy.ProductAdjustMessage;
import caveworld.plugin.mceconomy.ShopProductManager;
import caveworld.util.CaveUtils;
import caveworld.util.Version;
import caveworld.util.Version.Status;
import caveworld.util.breaker.MultiBreakExecutor;
import caveworld.world.TeleporterCaveworld;
import caveworld.world.WorldProviderCavern;
import caveworld.world.WorldProviderCaveworld;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.OpenGlHelper;
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
import net.minecraft.network.NetworkManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent;
import shift.mceconomy2.api.MCEconomyAPI;

public class CaveEventHooks
{
	public static final CaveEventHooks instance = new CaveEventHooks();

	public static final Set<String> firstJoinPlayers = Sets.newHashSet();

	@SideOnly(Side.CLIENT)
	private ItemStack miningPointItemDefault;

	@SideOnly(Side.CLIENT)
	public static ICaveBiomeManager prevBiomeManager;
	@SideOnly(Side.CLIENT)
	public static ICaveBiomeManager prevBiomeCavernManager;
	@SideOnly(Side.CLIENT)
	public static ICaveVeinManager prevVeinManager;
	@SideOnly(Side.CLIENT)
	public static ICaveVeinManager prevVeinCavernManager;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if (Caveworld.MODID.equals(event.modID))
		{
			if (event.configID == null)
			{
				Config.syncGeneralCfg();
				Config.syncEntitiesCfg();
				Config.syncDimensionCfg();
			}
			else switch (event.configID)
			{
				case Configuration.CATEGORY_GENERAL:
					Config.syncGeneralCfg();
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
			if (mc.pointedEntity instanceof EntityCaveman)
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

			if (current != null && current.getItem() != null)
			{
				if (current.getItem() instanceof ICaveniumTool)
				{
					ICaveniumTool tool = (ICaveniumTool)current.getItem();

					if (tool.getHighlightStart() > 0 && System.currentTimeMillis() - tool.getHighlightStart() < Config.modeDisplayTime)
					{
						long time = System.currentTimeMillis() - tool.getHighlightStart();

						if (time > Config.modeDisplayTime - 255)
						{
							time = tool.getHighlightStart() + Config.modeDisplayTime - System.currentTimeMillis();
						}

						int i = MathHelper.clamp_int((int)time, 0, 255);

						if (i > 0)
						{
							GL11.glPushMatrix();
							GL11.glEnable(GL11.GL_BLEND);
							OpenGlHelper.glBlendFunc(770, 771, 1, 0);
							mc.fontRenderer.drawStringWithShadow(tool.getModeInfomation(current), 18, event.resolution.getScaledHeight() - 20, 16777215 + (i << 24));
							GL11.glDisable(GL11.GL_BLEND);
							GL11.glPopMatrix();
						}
					}
					else
					{
						int highlight = ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, mc.ingameGUI, "remainingHighlightTicks", "field_92017_k");

						if (highlight == 40)
						{
							tool.setHighlightStart(System.currentTimeMillis());
						}
					}
				}
				else if (current.getItem() instanceof ItemCavenicBow)
				{
					ItemCavenicBow bow = (ItemCavenicBow)current.getItem();

					if (bow.highlightStart > 0 && System.currentTimeMillis() - bow.highlightStart < Config.modeDisplayTime)
					{
						long time = System.currentTimeMillis() - bow.highlightStart;

						if (time > Config.modeDisplayTime - 255)
						{
							time = bow.highlightStart + Config.modeDisplayTime - System.currentTimeMillis();
						}

						int i = MathHelper.clamp_int((int)time, 0, 255);

						if (i > 0)
						{
							GL11.glPushMatrix();
							GL11.glEnable(GL11.GL_BLEND);
							OpenGlHelper.glBlendFunc(770, 771, 1, 0);
							mc.fontRenderer.drawStringWithShadow(bow.getModeInfomation(current), 18, event.resolution.getScaledHeight() - 20, 16777215 + (i << 24));
							GL11.glDisable(GL11.GL_BLEND);
							GL11.glPopMatrix();
						}
					}
					else
					{
						int highlight = ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, mc.ingameGUI, "remainingHighlightTicks", "field_92017_k");

						if (highlight == 40)
						{
							bow.highlightStart = System.currentTimeMillis();
						}
					}
				}
			}

			if ((CaveworldAPI.isEntityInCaveworld(player) || CaveworldAPI.isEntityInCavern(player)) && (mc.currentScreen == null || GuiChat.class.isInstance(mc.currentScreen)) &&
				(player.capabilities.isCreativeMode || mc.gameSettings.advancedItemTooltips || CaveUtils.isItemPickaxe(current) || CaveUtils.isMiningPointValidItem(current)))
			{
				String str = Integer.toString(CaveworldAPI.getMiningPoint(mc.thePlayer));
				int x = event.resolution.getScaledWidth() - 20;
				int y;
				boolean left = false;
				int type = Config.miningPointRenderType;

				if (type > 3)
				{
					return;
				}

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

		if (mc.gameSettings.showDebugInfo)
		{
			if (CaveworldAPI.isEntityInCaveworld(player))
			{
				event.left.add("dim: Caveworld");
			}
			else if (CaveworldAPI.isEntityInCavern(player))
			{
				event.left.add("dim: Cavern");
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMouse(MouseEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.thePlayer;

		if (player != null)
		{
			ItemStack current = player.getCurrentEquippedItem();

			if (current != null && current.getItem() != null && current.getItem() instanceof ICaveniumTool)
			{
				if (!((ICaveniumTool)current.getItem()).getModeName(current).equalsIgnoreCase("NORMAL"))
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
	public void onFOVUpdate(FOVUpdateEvent event)
	{
		EntityPlayer player = event.entity;
		ItemStack using = player.getItemInUse();

		if (using != null && using.getItem() instanceof ItemCavenicBow)
		{
			ItemCavenicBow bow = (ItemCavenicBow)using.getItem();

			if (bow.getMode(using) != BowMode.RAPID)
			{
				float f = player.getItemInUseDuration() / 20.0F;

				if (f > 1.0F)
				{
					 f = 1.0F;
				}
				else
				{
					 f *= f;
				}

				event.newfov *= 1.0F - f * 0.15F;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlaySound(PlaySoundEvent17 event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (event.category == SoundCategory.MUSIC && (CaveworldAPI.isEntityInCaveworld(mc.thePlayer) || CaveworldAPI.isEntityInCavern(mc.thePlayer)))
		{
			event.result = null;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientConnected(ClientConnectedToServerEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (Version.getStatus() == Status.PENDING || Version.getStatus() == Status.FAILED)
		{
			Version.versionCheck();
		}
		else if (Version.DEV_DEBUG || Version.getStatus() == Status.AHEAD || Config.versionNotify && Version.isOutdated())
		{
			IChatComponent message;
			String name = Caveworld.metadata.name;

			message = new ChatComponentTranslation("caveworld.version.message", EnumChatFormatting.AQUA + name + EnumChatFormatting.RESET);
			message.appendText(" : " + EnumChatFormatting.YELLOW + Version.getLatest());
			message.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Caveworld.metadata.url));

			mc.ingameGUI.getChatGUI().printChatMessage(message);
			message = null;

			if (StringUtils.endsWithIgnoreCase(Version.getCurrent(), "beta"))
			{
				message = new ChatComponentTranslation("caveworld.version.message.beta", EnumChatFormatting.AQUA + name + EnumChatFormatting.RESET);
			}
			else if (StringUtils.endsWithIgnoreCase(Version.getCurrent(), "alpha"))
			{
				message = new ChatComponentTranslation("caveworld.version.message.alpha", EnumChatFormatting.AQUA + name + EnumChatFormatting.RESET);
			}

			if (message != null)
			{
				mc.ingameGUI.getChatGUI().printChatMessage(message);
			}
		}

		if (!mc.isIntegratedServerRunning())
		{
			prevBiomeManager = CaveworldAPI.biomeManager;
			CaveworldAPI.biomeManager = new CaveBiomeManager();
			prevBiomeCavernManager = CaveworldAPI.biomeCavernManager;
			CaveworldAPI.biomeCavernManager = new CavernBiomeManager();
			prevVeinManager = CaveworldAPI.veinManager;
			CaveworldAPI.veinManager = new CaveVeinManager();
			prevVeinCavernManager = CaveworldAPI.veinCavernManager;
			CaveworldAPI.veinCavernManager = new CavernVeinManager();

			if (MCEconomyPlugin.enabled())
			{
				MCEconomyPlugin.prevProductManager = MCEconomyPlugin.productManager;
				MCEconomyPlugin.productManager = new ShopProductManager();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientDisconnected(ClientDisconnectionFromServerEvent event)
	{
		Config.syncDimensionCfg();

		if (prevBiomeManager != null)
		{
			CaveworldAPI.biomeManager = prevBiomeManager;
			prevBiomeManager = null;
		}

		if (prevBiomeCavernManager != null)
		{
			CaveworldAPI.biomeCavernManager = prevBiomeCavernManager;
			prevBiomeCavernManager = null;
		}

		if (prevVeinManager != null)
		{
			CaveworldAPI.veinManager = prevVeinManager;
			prevVeinManager = null;
		}

		if (prevVeinCavernManager != null)
		{
			CaveworldAPI.veinCavernManager = prevVeinCavernManager;
			prevVeinCavernManager = null;
		}

		if (MCEconomyPlugin.prevProductManager != null)
		{
			MCEconomyPlugin.productManager = MCEconomyPlugin.prevProductManager;
			MCEconomyPlugin.prevProductManager = null;
		}

		CaveItems.ore_compass.resetFinder();
	}

	@SubscribeEvent
	public void onServerConnected(ServerConnectionFromClientEvent event)
	{
		NetworkManager manager = event.manager;

		if (!manager.isLocalChannel())
		{
			manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new CaveworldAdjustMessage(CaveworldAPI.getDimension(), WorldProviderCaveworld.getDimData())), new GenericFutureListener[0]);
			manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new CavernAdjustMessage(CaveworldAPI.getCavernDimension(), WorldProviderCavern.getDimData())), new GenericFutureListener[0]);
			manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new BiomeAdjustMessage(CaveworldAPI.biomeManager)), new GenericFutureListener[0]);
			manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new BiomeAdjustMessage(CaveworldAPI.biomeCavernManager)), new GenericFutureListener[0]);
			manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new VeinAdjustMessage(CaveworldAPI.veinManager)), new GenericFutureListener[0]);
			manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new VeinAdjustMessage(CaveworldAPI.veinCavernManager)), new GenericFutureListener[0]);

			if (MCEconomyPlugin.enabled())
			{
				manager.scheduleOutboundPacket(Caveworld.network.getPacketFrom(new ProductAdjustMessage(MCEconomyPlugin.productManager)), new GenericFutureListener[0]);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			WorldServer world = player.getServerForPlayer();

			if (CaveworldAPI.isEntityInCaveworld(player) || CaveworldAPI.isEntityInCavern(player))
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
					bonus.add(new ItemStack(Items.dye, MathHelper.getRandomIntegerInRange(world.rand, 3, 5), 15));

					for (ItemStack itemstack : bonus)
					{
						player.inventory.addItemStackToInventory(itemstack);
					}

					int dim = CaveworldAPI.getDimension();

					player.isDead = false;
					player.forceSpawn = true;
					player.timeUntilPortal = player.getPortalCooldown();
					player.mcServer.getConfigurationManager().transferPlayerToDimension(player, dim, new TeleporterCaveworld(player.mcServer.worldServerForDimension(dim)));
					player.addExperienceLevel(0);

					world = player.getServerForPlayer();
					world.resetUpdateEntityTick();

					if (player.getBedLocation(player.dimension) == null)
					{
						player.setSpawnChunk(player.getPlayerCoordinates(), true);
					}

					int x = MathHelper.floor_double(player.posX);
					int y = MathHelper.floor_double(player.posY);
					int z = MathHelper.floor_double(player.posZ);

					outside: for (int j = x - 2; j < x + 2; ++j)
					{
						for (int k = z - 2; k < z + 2; ++k)
						{
							if (world.getBlock(j, y, k) == CaveBlocks.caveworld_portal)
							{
								world.setBlockToAir(j, y, k);
								break outside;
							}
						}
					}

					world.playSoundAtEntity(player, "dig.glass", 1.0F, 0.65F);
					player.addChatMessage(new ChatComponentTranslation("caveworld.message.caveborn"));
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		EntityPlayer player = event.player;

		firstJoinPlayers.remove(player.getGameProfile().getId().toString());

		for (ItemMiningPickaxe.BreakMode mode : ItemMiningPickaxe.BreakMode.values())
		{
			mode.clear(player);
		}

		for (ItemLumberingAxe.BreakMode mode : ItemLumberingAxe.BreakMode.values())
		{
			mode.clear(player);
		}

		for (ItemDiggingShovel.BreakMode mode : ItemDiggingShovel.BreakMode.values())
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

			if (Config.hardcore && event.toDim != CaveworldAPI.getDimension() && event.toDim != CaveworldAPI.getCavernDimension())
			{
				CaveUtils.teleportPlayer(player, event.fromDim);

				return;
			}

			if (event.toDim == CaveworldAPI.getDimension())
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

				player.triggerAchievement(CaveAchievementList.caveworld);
			}
			else if (event.toDim == CaveworldAPI.getCavernDimension())
			{
				NBTTagCompound data = player.getEntityData();

				if (!player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.cavern) || data.getLong("Cavern:LastTeleportTime") + 18000L < world.getTotalWorldTime())
				{
					String name = "ambient.unrest";

					if (world.rand.nextInt(3) == 0)
					{
						name = "ambient.cave";
					}

					Caveworld.network.sendTo(new PlaySoundMessage(new ResourceLocation("caveworld", name)), player);
				}

				data.setLong("Cavern:LastTeleportTime", world.getTotalWorldTime());

				player.triggerAchievement(CaveAchievementList.cavern);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		EntityPlayer player = event.player;

		if (CaveworldAPI.isEntityInCaveworld(player) || CaveworldAPI.isEntityInCavern(player))
		{
			player.timeUntilPortal = player.getPortalCooldown();

			if (player instanceof EntityPlayerMP && MathHelper.floor_double(player.posY) >= player.worldObj.getActualHeight() - 1)
			{
				CaveUtils.teleportPlayer((EntityPlayerMP)player, player.dimension);
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

		if (!Strings.isNullOrEmpty(entity.getCommandSenderName()))
		{
			thrower = world.getPlayerEntityByName(entity.getCommandSenderName());
		}

		if (itemstack.getItem() instanceof ItemCavenium)
		{
			if (thrower == null)
			{
				player.triggerAchievement(CaveAchievementList.cavenium);
			}
		}
		else if (itemstack.getItem() == CaveItems.gem && itemstack.getItemDamage() == 0)
		{
			if (thrower == null)
			{
				player.triggerAchievement(CaveAchievementList.aquamarine);
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

			if (CaveworldAPI.isEntityInCaveworld(player) || CaveworldAPI.isEntityInCavern(player))
			{
				ItemStack current = player.getCurrentEquippedItem();

				if (CaveUtils.isMiningPointValidItem(current))
				{
					Block block = event.block;
					int meta = event.blockMetadata;
					int amount = CaveworldAPI.getMiningPointAmount(block, meta);

					MiningPointEvent.OnBlockBreak pointEvent = new MiningPointEvent.OnBlockBreak(player, amount);
					MinecraftForge.EVENT_BUS.post(pointEvent);

					amount = pointEvent.newAmount;

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
		EntityPlayer player = event.entityPlayer;
		World world = event.world;
		int x = event.x;
		int y = event.y;
		int z = event.z;
		int face = event.face;
		ItemStack current = player.getCurrentEquippedItem();

		if (!world.isRemote)
		{
			switch (event.action)
			{
				case LEFT_CLICK_BLOCK:
					if (current != null && current.getItem() != null && current.getItem() instanceof ICaveniumTool && current.getItemDamage() < current.getMaxDamage())
					{
						ICaveniumTool tool = (ICaveniumTool)current.getItem();

						if (tool.canBreak(current, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z)))
						{
							MultiBreakExecutor executor = tool.getMode(current).getExecutor(player);

							if (executor != null)
							{
								executor.setOriginPos(x, y, z).setBreakPositions();

								if (player.capabilities.isCreativeMode)
								{
									executor.breakAll();
								}
								else
								{
									Caveworld.network.sendTo(new MultiBreakCountMessage(executor.getBreakPositions().size()), (EntityPlayerMP)player);
								}
							}
						}
						else
						{
							tool.getMode(current).clear(player);

							Caveworld.network.sendTo(new MultiBreakCountMessage(0), (EntityPlayerMP)player);
						}
					}

					break;
				case RIGHT_CLICK_BLOCK:
					if (!player.isSneaking() && current != null && (current.getItem() == Item.getItemFromBlock(Blocks.ender_chest) ||
						EnderStoragePlugin.enderChest != null && current.getItem() == Item.getItemFromBlock(EnderStoragePlugin.enderChest)))
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
					else if (current != null && current.getItem() == Items.emerald)
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

						if (CaveBlocks.cavern_portal.func_150000_e(world, x, y, z))
						{
							world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, CaveBlocks.cavern_portal.stepSound.func_150496_b(), 1.0F, 2.0F);

							if (!player.capabilities.isCreativeMode && --current.stackSize <= 0)
							{
								player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
							}

							event.setCanceled(true);
						}
					}

					break;
				default:
			}
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		EntityPlayer player = event.entityPlayer;
		ItemStack current = player.getCurrentEquippedItem();

		if (current != null && (current.getItem() instanceof IAquamarineTool ||
			current.getItem() instanceof ICaveniumTool && ((ICaveniumTool)current.getItem()).getBase(current) instanceof IAquamarineTool))
		{
			if (player.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(player))
			{
				if (!player.onGround)
				{
					event.newSpeed = event.originalSpeed * 10.0F;
				}
				else
				{
					event.newSpeed = event.originalSpeed * 5.0F;
				}
			}
			else
			{
				event.newSpeed = event.originalSpeed;
			}
		}
		else
		{
			event.newSpeed = event.originalSpeed;
		}

		if (current != null && current.getItem() != null && current.getItem() instanceof ICaveniumTool)
		{
			ICaveniumTool tool = (ICaveniumTool)current.getItem();

			if (!tool.getModeName(current).equalsIgnoreCase("NORMAL"))
			{
				int refined = tool.getRefined(current);

				if (refined >= 4 || current.getItem().getHarvestLevel(current, tool.getToolClass()) >= 3 && EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, current) >= 4)
				{
					return;
				}

				int count;

				if (player.worldObj.isRemote)
				{
					count = MultiBreakExecutor.positionsCount.get();
				}
				else
				{
					count = tool.getMode(current).getExecutor(player).getBreakPositions().size();
				}

				event.newSpeed = Math.min(event.newSpeed / (count * (0.5F - refined * 0.1245F)), event.newSpeed);
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

			if ((CaveworldAPI.isEntityInCaveworld(player) || CaveworldAPI.isEntityInCavern(player)) && !player.capabilities.isCreativeMode)
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
	public void onPlayerDestroyItem(PlayerDestroyItemEvent event)
	{
		EntityPlayer player = event.entityPlayer;

		if (!player.worldObj.isRemote)
		{
			ItemStack itemstack = event.original;

			outside: for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
			{
				ItemStack item = player.inventory.getStackInSlot(i);

				if (item != null && item.getItem() instanceof ItemCaverBackpack)
				{
					InventoryCaverBackpack inventory = new InventoryCaverBackpack(item);

					for (int j = 0; j < inventory.getSizeInventory(); ++j)
					{
						ItemStack stack = inventory.getStackInSlot(j);

						if (stack != null && itemstack.getItem() == stack.getItem())
						{
							if (itemstack.isItemStackDamageable() && stack.isItemStackDamageable() || itemstack.getItemDamage() == stack.getItemDamage())
							{
								player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);

								inventory.setInventorySlotContents(j, null);
								inventory.markDirty();

								break outside;
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event)
	{
		CaveworldAPI.getMiningPoint(event.entity);
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		Entity entity = event.entity;
		World world = event.world;

		if (entity instanceof EntityLivingBase)
		{
			CaveworldAPI.loadData(entity, null);
		}

		if (entity instanceof EntityLiving && (CaveworldAPI.isEntityInCaveworld(entity) || CaveworldAPI.isEntityInCavern(entity)))
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
	public void onLivingAttack(LivingAttackEvent event)
	{
		if (event.source.getDamageType().equals("arrow"))
		{
			Entity entity = event.source.getEntity();

			if (entity != null && entity instanceof EntityLivingBase)
			{
				EntityLivingBase living = (EntityLivingBase)entity;

				if (living.getHeldItem() != null && living.getHeldItem().getItem() == CaveItems.cavenic_bow)
				{
					event.entityLiving.hurtResistantTime = 0;
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event)
	{
		EntityLivingBase entity = event.entityLiving;

		if (Config.deathLoseMiningPoint)
		{
			CaveworldAPI.setMiningPoint(entity, 0);
		}

		CaveworldAPI.saveData(entity, null);
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

			if (MCEconomyPlugin.enabled() && (CaveworldAPI.isEntityInCaveworld(living) || CaveworldAPI.isEntityInCavern(entity)) && living instanceof IMob)
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

		if (!world.isRemote && world.provider.dimensionId == 0)
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
			if (dim == 0)
			{
				CaveBlocks.caveworld_portal.saveInventoryToDimData();
				CaveBlocks.caveworld_portal.clearInventory();
			}
			else if (dim == CaveworldAPI.getDimension())
			{
				WorldProviderCaveworld.saveDimData();
			}
			else if (dim == CaveworldAPI.getCavernDimension())
			{
				WorldProviderCavern.saveDimData();
			}
		}
	}

	@SubscribeEvent
	public void onServerChat(ServerChatEvent event)
	{
		String message = event.message;
		EntityPlayerMP player = event.player;

		if (message.matches("@buff|@buff ([0-9]*$|max)") && (CaveworldAPI.isEntityInCaveworld(player) || CaveworldAPI.isEntityInCavern(player)))
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