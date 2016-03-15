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

import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import caveworld.api.CaverAPI;
import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveBiomeManager;
import caveworld.api.ICaveVeinManager;
import caveworld.api.event.MiningPointEvent;
import caveworld.block.BlockCavePortal;
import caveworld.block.BlockPortalCaveworld;
import caveworld.block.CaveBlocks;
import caveworld.client.gui.GuiDownloadCaveTerrain;
import caveworld.client.gui.GuiLoadCaveTerrain;
import caveworld.client.gui.GuiSelectBreakable;
import caveworld.core.AquaCavernBiomeManager;
import caveworld.core.AquaCavernVeinManager;
import caveworld.core.CaveAchievementList;
import caveworld.core.CaveBiomeManager;
import caveworld.core.CaveVeinManager;
import caveworld.core.CaverManager;
import caveworld.core.CaverManager.MinerRank;
import caveworld.core.CavernBiomeManager;
import caveworld.core.CavernVeinManager;
import caveworld.core.Caveworld;
import caveworld.core.Config;
import caveworld.core.ConfigHelper;
import caveworld.entity.EntityCaveman;
import caveworld.inventory.InventoryCaverBackpack;
import caveworld.item.CaveItems;
import caveworld.item.IAquamarineTool;
import caveworld.item.ICaveniumTool;
import caveworld.item.IModeItem;
import caveworld.item.ItemCaveSword;
import caveworld.item.ItemCavenicBow;
import caveworld.item.ItemCavenicBow.BowMode;
import caveworld.item.ItemCavenium;
import caveworld.item.ItemCaverBackpack;
import caveworld.item.ItemDiggingShovel;
import caveworld.item.ItemFarmingHoe;
import caveworld.item.ItemFarmingHoe.FarmMode;
import caveworld.item.ItemLumberingAxe;
import caveworld.item.ItemMiningPickaxe;
import caveworld.item.ItemOreCompass;
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.client.BrightnessAdjustMessage;
import caveworld.network.client.CaveAdjustMessage;
import caveworld.network.client.CaveMusicMessage;
import caveworld.network.client.CaveworldMenuMessage;
import caveworld.network.client.LastMineMessage;
import caveworld.network.client.MultiBreakCountMessage;
import caveworld.network.common.VeinAdjustMessage;
import caveworld.network.server.CaveAchievementMessage;
import caveworld.plugin.enderstorage.EnderStoragePlugin;
import caveworld.plugin.sextiarysector.SextiarySectorPlugin;
import caveworld.util.CaveUtils;
import caveworld.util.Version;
import caveworld.util.Version.Status;
import caveworld.util.breaker.MultiBreakExecutor;
import caveworld.world.ChunkProviderAquaCavern;
import caveworld.world.ChunkProviderCaveland;
import caveworld.world.ChunkProviderCavenia;
import caveworld.world.ChunkProviderCavern;
import caveworld.world.ChunkProviderCaveworld;
import caveworld.world.WorldProviderAquaCavern;
import caveworld.world.WorldProviderCaveland;
import caveworld.world.WorldProviderCavenia;
import caveworld.world.WorldProviderCavern;
import caveworld.world.WorldProviderCaveworld;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
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
import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent;
import shift.sextiarysector.api.SextiarySectorAPI;

public class CaveEventHooks
{
	public static final CaveEventHooks instance = new CaveEventHooks();

	public static final Set<String> firstJoinPlayers = Sets.newHashSet();
	public static final List<ItemStack> cavebornItems = Lists.newArrayList();

	@SideOnly(Side.CLIENT)
	public static ICaveBiomeManager prevBiomeManager;
	@SideOnly(Side.CLIENT)
	public static ICaveBiomeManager prevBiomeCavernManager;
	@SideOnly(Side.CLIENT)
	public static ICaveBiomeManager prevBiomeAquaCavernManager;
	@SideOnly(Side.CLIENT)
	public static ICaveVeinManager prevVeinManager;
	@SideOnly(Side.CLIENT)
	public static ICaveVeinManager prevVeinCavernManager;
	@SideOnly(Side.CLIENT)
	public static ICaveVeinManager prevVeinAquaCavernManager;

	private final Random rand = new Random();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if (Caveworld.MODID.equals(event.modID))
		{
			if (event.configID == null)
			{
				Config.syncGeneralCfg();
				Config.syncMobsCfg();
				Config.syncDimensionCfg();
			}
			else switch (event.configID)
			{
				case Configuration.CATEGORY_GENERAL:
					Config.syncGeneralCfg();
					break;
				case "mobs":
					Config.syncMobsCfg();
					break;
				case "dimension":
					Config.syncDimensionCfg();
					break;
			}

			Config.saveAllConfigs();
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
					CaveNetworkRegistry.sendToServer(new CaveAchievementMessage(CaveAchievementList.caveman));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event)
	{
		if (event.type != ElementType.AIR)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();

		if (CaveworldAPI.isEntityInAquaCavern(mc.thePlayer) && CaverAPI.getMinerRank(mc.thePlayer) >= MinerRank.AQUA_MINER.getRank())
		{
			event.setCanceled(true);
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

		if (mc.thePlayer != null)
		{
			ItemStack current = mc.thePlayer.getCurrentEquippedItem();

			if (current != null && current.getItem() instanceof IModeItem)
			{
				IModeItem item = (IModeItem)current.getItem();

				if (item.getHighlightStart() > 0 && System.currentTimeMillis() - item.getHighlightStart() < Config.modeDisplayTime)
				{
					long time = System.currentTimeMillis() - item.getHighlightStart();

					if (time > Config.modeDisplayTime - 255)
					{
						time = item.getHighlightStart() + Config.modeDisplayTime - System.currentTimeMillis();
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
						item.setHighlightStart(System.currentTimeMillis());
					}
				}
			}

			if (CaveworldAPI.isEntityInCaves(mc.thePlayer) && (mc.currentScreen == null || GuiChat.class.isInstance(mc.currentScreen)) &&
				(mc.thePlayer.capabilities.isCreativeMode || mc.gameSettings.advancedItemTooltips || CaveUtils.isItemPickaxe(current) || ConfigHelper.isMiningPointValidItem(current)))
			{
				int type = Config.miningPointRenderType;

				if (type > 3)
				{
					return;
				}

				MinerRank minerRank = CaverManager.getRank(CaverAPI.getMinerRank(mc.thePlayer));
				String point = Integer.toString(CaverAPI.getMiningPoint(mc.thePlayer));
				String rank = I18n.format(minerRank.getUnlocalizedName());
				int x = event.resolution.getScaledWidth() - 20;
				int y;
				boolean left = false;
				boolean top = type == 0 || type == 2;

				if (type == 2 || type == 3)
				{
					x = 5;
					left = true;
				}

				if (top)
				{
					y = 5;
				}
				else
				{
					y = event.resolution.getScaledHeight() - 21;
				}

				int originX = x;
				int originY = y;
				boolean flag = false;

				if (CaverManager.mineHighlightStart > 0 && Minecraft.getSystemTime() - CaverManager.mineHighlightStart < 2000 && CaverManager.lastMine != null && CaverManager.lastMinePoint != 0)
				{
					ItemStack item = CaverManager.lastMine.getItemStack();

					if (item != null && item.getItem() != null)
					{
						CaveUtils.renderItemStack(mc, item, x, y, true, true, Integer.toString(CaverManager.lastMinePoint));

						flag = true;
					}
				}

				if (flag)
				{
					x += left ? 20 : -20;
				}

				CaveUtils.renderItemStack(mc, minerRank.getRenderItemStack(), x, y, true, false, null);

				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);

				FontRenderer renderer = mc.fontRenderer;

				if (left)
				{
					renderer.drawStringWithShadow(point, x + 5, y + 9, 0xCECECE);

					if (Config.showMinerRank)
					{
						x = originX;
						y = originY;

						if (top)
						{
							renderer.drawStringWithShadow(rank, x + 5, y + 19, 0xCECECE);
						}
						else
						{
							renderer.drawStringWithShadow(rank, x + 5, y - 12, 0xCECECE);
						}
					}
				}
				else
				{
					renderer.drawStringWithShadow(point, x + 17 - mc.fontRenderer.getStringWidth(point), y + 9, 0xCECECE);

					if (Config.showMinerRank)
					{
						x = originX;
						y = originY;

						if (top)
						{
							renderer.drawStringWithShadow(rank, x + 17 - mc.fontRenderer.getStringWidth(rank), y + 19, 0xCECECE);
						}
						else
						{
							renderer.drawStringWithShadow(rank, x + 17 - mc.fontRenderer.getStringWidth(rank), y - 12, 0xCECECE);
						}
					}
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
			else if (CaveworldAPI.isEntityInAquaCavern(player))
			{
				event.left.add("dim: Aqua Cavern");
			}
			else if (CaveworldAPI.isEntityInCaveland(player))
			{
				event.left.add("dim: Caveland");
			}
			else if (CaveworldAPI.isEntityInCavenia(player))
			{
				event.left.add("dim: Cavenia");
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

			if (current != null && current.getItem() instanceof ICaveniumTool)
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
	public void onKeyInput(KeyInputEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.currentScreen == null && mc.thePlayer != null && Config.keyBindAtCommand.isPressed())
		{
			mc.displayGuiScreen(new GuiChat("@"));
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
			BowMode mode = bow.getMode(using);

			if (mode != BowMode.RAPID)
			{
				boolean snipe = mode == BowMode.SNIPE;
				float f = player.getItemInUseDuration() / (snipe ? 100.0F : 20.0F);

				if (f > 1.0F)
				{
					f = 1.0F;

					Caveworld.proxy.setDebugBoundingBox(player.isSneaking());
				}
				else
				{
					f *= f;
				}

				event.newfov *= 1.0F - f * (snipe ? 0.8F : 0.15F);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogDensity(FogDensity event)
	{
		EntityLivingBase entity = event.entity;

		if (CaveworldAPI.isEntityInCaves(entity))
		{
			if (event.block.getMaterial() == Material.water && CaverAPI.getMinerRank(entity) >= MinerRank.AQUA_MINER.getRank())
			{
				GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);

				if (entity.isPotionActive(Potion.waterBreathing))
				{
					event.density = 0.005F;
				}
				else
				{
					event.density = 0.01F - EnchantmentHelper.getRespiration(entity) * 0.003F;
				}

				event.setCanceled(true);

				return;
			}

			if (CaveworldAPI.isEntityInCaveland(entity))
			{
				GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);

				event.density = (float)Math.abs(Math.pow((Math.min(entity.posY, 20) - 63) / (255 - 63), 4));
				event.setCanceled(true);
			}
			else if (CaveworldAPI.isEntityInCavenia(entity))
			{
				GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);

				event.density = 0.005F;
				event.setCanceled(true);
			}
			else if (entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)entity;
				ItemStack using = player.getItemInUse();

				if (using != null && using.getItem() instanceof ItemCavenicBow)
				{
					if (((ItemCavenicBow)using.getItem()).getMode(using) == BowMode.SNIPE)
					{
						float f = player.getItemInUseDuration() / 100.0F;

						if (f > 1.0F)
						{
							f = 1.0F;
						}
						else
						{
							f *= f;
						}

						GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);

						event.density = f / 250.0F;
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogColors(FogColors event)
	{
		EntityLivingBase entity = event.entity;
		float var1 = 0.0F;

		if (CaveworldAPI.isEntityInCaveland(entity))
		{
			var1 = 0.7F;
		}
		else if (CaveworldAPI.isEntityInCavenia(entity))
		{
			var1 = 1.0F;
		}

		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;
			ItemStack using = player.getItemInUse();

			if (using != null && using.getItem() instanceof ItemCavenicBow)
			{
				if (((ItemCavenicBow)using.getItem()).getMode(using) == BowMode.SNIPE)
				{
					var1 = 1.0F;
				}
			}
		}

		if (var1 > 0.0F)
		{
			float var2 = 1.0F / event.red;

			if (var2 > 1.0F / event.green)
			{
				var2 = 1.0F / event.green;
			}

			if (var2 > 1.0F / event.blue)
			{
				var2 = 1.0F / event.blue;
			}

			event.red = event.red * (1.0F - var1) + event.red * var2 * var1;
			event.green = event.green * (1.0F - var1) + event.green * var2 * var1;
			event.blue = event.blue * (1.0F - var1) + event.blue * var2 * var1;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (event.gui != null && GuiModList.class == event.gui.getClass())
		{
			Caveworld.metadata.description = I18n.format("caveworld.description");
		}
		else if (CaveworldAPI.isEntityInCaves(mc.thePlayer) && (mc.currentScreen == null || !(mc.currentScreen instanceof GuiSelectWorld)))
		{
			if (event.gui == null)
			{
				if (mc.currentScreen != null && GuiDownloadCaveTerrain.class == mc.currentScreen.getClass())
				{
					event.gui = new GuiLoadCaveTerrain(mc.getNetHandler());
				}
			}
			else if (GuiDownloadTerrain.class == event.gui.getClass())
			{
				event.gui = new GuiDownloadCaveTerrain(mc.getNetHandler());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlaySound(PlaySoundEvent17 event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (event.category == SoundCategory.MUSIC && CaveworldAPI.isEntityInCaves(mc.thePlayer))
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
			IChatComponent name = new ChatComponentText(Caveworld.metadata.name);
			name.getChatStyle().setColor(EnumChatFormatting.AQUA);
			IChatComponent latest = new ChatComponentText(Version.getLatest());
			latest.getChatStyle().setColor(EnumChatFormatting.YELLOW);

			IChatComponent message;

			message = new ChatComponentTranslation("caveworld.version.message", name);
			message.appendText(" : ").appendSibling(latest);
			message.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Caveworld.metadata.url));

			mc.ingameGUI.getChatGUI().printChatMessage(message);
			message = null;

			if (Version.isBeta())
			{
				message = new ChatComponentTranslation("caveworld.version.message.beta", name);
			}
			else if (Version.isAlpha())
			{
				message = new ChatComponentTranslation("caveworld.version.message.alpha", name);
			}

			if (message != null)
			{
				mc.ingameGUI.getChatGUI().printChatMessage(message);
			}
		}

		if (!mc.isIntegratedServerRunning())
		{
			prevBiomeManager = CaveworldAPI.biomeManager;
			CaveworldAPI.biomeManager = new CaveBiomeManager().setReadOnly(true);
			prevBiomeCavernManager = CaveworldAPI.biomeCavernManager;
			CaveworldAPI.biomeCavernManager = new CavernBiomeManager().setReadOnly(true);
			prevBiomeAquaCavernManager = CaveworldAPI.biomeAquaCavernManager;
			CaveworldAPI.biomeAquaCavernManager = new AquaCavernBiomeManager().setReadOnly(true);
			prevVeinManager = CaveworldAPI.veinManager;
			CaveworldAPI.veinManager = new CaveVeinManager().setReadOnly(true);
			prevVeinCavernManager = CaveworldAPI.veinCavernManager;
			CaveworldAPI.veinCavernManager = new CavernVeinManager().setReadOnly(true);
			prevVeinAquaCavernManager = CaveworldAPI.veinAquaCavernManager;
			CaveworldAPI.veinAquaCavernManager = new AquaCavernVeinManager().setReadOnly(true);
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

		if (prevBiomeAquaCavernManager != null)
		{
			CaveworldAPI.biomeAquaCavernManager = prevBiomeAquaCavernManager;
			prevBiomeAquaCavernManager = null;
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

		if (prevVeinAquaCavernManager != null)
		{
			CaveworldAPI.veinAquaCavernManager = prevVeinAquaCavernManager;
			prevVeinAquaCavernManager = null;
		}

		ItemOreCompass.resetFinder();
	}

	@SubscribeEvent
	public void onServerConnected(ServerConnectionFromClientEvent event)
	{
		NetworkManager manager = event.manager;

		if (!manager.isLocalChannel())
		{
			float[] brightness = new float[5];
			brightness[0] = ChunkProviderCaveworld.caveBrightness;
			brightness[1] = ChunkProviderCavern.caveBrightness;
			brightness[2] = ChunkProviderAquaCavern.caveBrightness;
			brightness[3] = ChunkProviderCaveland.caveBrightness;
			brightness[4] = ChunkProviderCavenia.caveBrightness;

			manager.scheduleOutboundPacket(CaveNetworkRegistry.getPacket(new BrightnessAdjustMessage(brightness)));
			manager.scheduleOutboundPacket(CaveNetworkRegistry.getPacket(new CaveAdjustMessage(WorldProviderCaveworld.TYPE, WorldProviderCaveworld.saveHandler)));
			manager.scheduleOutboundPacket(CaveNetworkRegistry.getPacket(new CaveAdjustMessage(WorldProviderCavern.TYPE, WorldProviderCavern.saveHandler)));
			manager.scheduleOutboundPacket(CaveNetworkRegistry.getPacket(new CaveAdjustMessage(WorldProviderAquaCavern.TYPE, WorldProviderAquaCavern.saveHandler)));
			manager.scheduleOutboundPacket(CaveNetworkRegistry.getPacket(new CaveAdjustMessage(WorldProviderCaveland.TYPE, WorldProviderCaveland.saveHandler)));
			manager.scheduleOutboundPacket(CaveNetworkRegistry.getPacket(new CaveAdjustMessage(WorldProviderCavenia.TYPE, WorldProviderCavenia.saveHandler)));

			if (Config.remoteConfig)
			{
				manager.scheduleOutboundPacket(CaveNetworkRegistry.getPacket(new VeinAdjustMessage(CaveworldAPI.veinManager)));
				manager.scheduleOutboundPacket(CaveNetworkRegistry.getPacket(new VeinAdjustMessage(CaveworldAPI.veinCavernManager)));
				manager.scheduleOutboundPacket(CaveNetworkRegistry.getPacket(new VeinAdjustMessage(CaveworldAPI.veinAquaCavernManager)));
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

			if (CaveworldAPI.isEntityInCaves(player))
			{
				if (MathHelper.floor_double(player.posY) >= world.getActualHeight() - 1)
				{
					if (CaveworldAPI.isEntityInAquaCavern(player))
					{
						CaveUtils.teleportPlayer(player, CaverAPI.getAquaCavernLastDimension(player));
					}
					else
					{
						CaveUtils.teleportPlayer(player, player.dimension);
					}
				}
			}
			else
			{
				if (CaveworldAPI.isCaveborn() && firstJoinPlayers.contains(player.getUniqueID().toString()))
				{
					for (ItemStack itemstack : cavebornItems)
					{
						ItemStack item = itemstack.copy();
						item.stackSize = 0;

						if (item.isStackable())
						{
							Block block = Block.getBlockFromItem(item.getItem());

							if (block != null && block != Blocks.air)
							{
								if (block instanceof BlockWorkbench || block.hasTileEntity(item.getItemDamage()))
								{
									item.stackSize = 1;
								}
							}

							if (item.stackSize <= 0)
							{
								item.stackSize = MathHelper.getRandomIntegerInRange(rand, 5, 10);
							}
						}
						else
						{
							item.stackSize = 1;
						}

						player.inventory.addItemStackToInventory(item);
					}

					int dim = 0;
					BlockCavePortal portal = null;

					switch (CaveworldAPI.getCaveborn())
					{
						case 1:
							portal = CaveBlocks.caveworld_portal;
							break;
						case 2:
							portal = CaveBlocks.cavern_portal;
							break;
						case 3:
							portal = CaveBlocks.aqua_cavern_portal;
							break;
						case 4:
							portal = CaveBlocks.caveland_portal;
							break;
					}

					if (portal != null)
					{
						dim = portal.getDimension();

						Teleporter teleporter = portal.getTeleporter(player.mcServer.worldServerForDimension(dim), false);

						player.isDead = false;
						player.forceSpawn = true;
						player.timeUntilPortal = player.getPortalCooldown();
						player.mcServer.getConfigurationManager().transferPlayerToDimension(player, dim, teleporter);
						player.addExperienceLevel(0);

						world = player.getServerForPlayer();
						world.resetUpdateEntityTick();

						int x = MathHelper.floor_double(player.posX);
						int y = MathHelper.floor_double(player.posY);
						int z = MathHelper.floor_double(player.posZ);

						outside: for (int j = x - 2; j < x + 2; ++j)
						{
							for (int k = z - 2; k < z + 2; ++k)
							{
								if (world.getBlock(j, y, k) == portal)
								{
									world.setBlockToAir(j, y, k);

									break outside;
								}
							}
						}

						world.playSoundAtEntity(player, "dig.glass", 1.0F, 0.65F);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		EntityPlayer player = event.player;

		firstJoinPlayers.remove(player.getUniqueID().toString());

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

		for (ItemFarmingHoe.FarmMode mode : ItemFarmingHoe.FarmMode.values())
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

			if (CaveworldAPI.isHardcore() && !CaveworldAPI.isEntityInCaves(player))
			{
				CaveUtils.teleportPlayer(player, event.fromDim);

				return;
			}

			String suffix = ":LastTeleportTime";

			if (CaveworldAPI.isEntityInCaveworld(player))
			{
				NBTTagCompound data = player.getEntityData();

				if (!player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.caveworld) || data.getLong("Caveworld" + suffix) + 18000L < world.getTotalWorldTime())
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage(world.rand.nextInt(3) == 0 ? "cavemusic.cave" : "cavemusic.unrest"), player);
				}

				data.setLong("Caveworld" + suffix, world.getTotalWorldTime());

				player.triggerAchievement(CaveAchievementList.caveworld);
			}
			else if (CaveworldAPI.isEntityInCavern(player))
			{
				NBTTagCompound data = player.getEntityData();

				if (!player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.cavern) || data.getLong("Cavern" + suffix) + 18000L < world.getTotalWorldTime())
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage(world.rand.nextInt(3) == 0 ? "cavemusic.cave" : "cavemusic.unrest"), player);
				}

				data.setLong("Cavern" + suffix, world.getTotalWorldTime());

				player.triggerAchievement(CaveAchievementList.cavern);
			}
			else if (CaveworldAPI.isEntityInAquaCavern(player))
			{
				NBTTagCompound data = player.getEntityData();

				if (!player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.aquaCavern) || data.getLong("AquaCavern" + suffix) + 18000L < world.getTotalWorldTime())
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage("cavemusic.aqua"), player);
				}

				data.setLong("AquaCavern" + suffix, world.getTotalWorldTime());

				player.triggerAchievement(CaveAchievementList.aquaCavern);
			}
			else if (CaveworldAPI.isEntityInCaveland(player))
			{
				NBTTagCompound data = player.getEntityData();

				if (!player.func_147099_x().hasAchievementUnlocked(CaveAchievementList.caveland) || data.getLong("Caveland" + suffix) + 18000L < world.getTotalWorldTime())
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage("cavemusic.hope"), player);
				}

				data.setLong("Caveland" + suffix, world.getTotalWorldTime());

				player.triggerAchievement(CaveAchievementList.caveland);
			}
			else if (CaveworldAPI.isEntityInCavenia(player))
			{
				player.triggerAchievement(CaveAchievementList.cavenia);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		EntityPlayer player = event.player;

		if (CaveworldAPI.isEntityInCaves(player))
		{
			player.timeUntilPortal = player.getPortalCooldown();

			if (player instanceof EntityPlayerMP && MathHelper.floor_double(player.posY) >= player.worldObj.getActualHeight() - 1)
			{
				EntityPlayerMP thePlayer = (EntityPlayerMP)player;

				if (CaveworldAPI.isEntityInAquaCavern(thePlayer))
				{
					CaveUtils.teleportPlayer(thePlayer, CaverAPI.getAquaCavernLastDimension(thePlayer));
				}
				else
				{
					CaveUtils.teleportPlayer(thePlayer, thePlayer.dimension);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerDrops(PlayerDropsEvent event)
	{
		EntityPlayer player = event.entityPlayer;

		if (CaveworldAPI.isEntityInCavenia(player))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event)
	{
		EntityPlayer player = event.entityPlayer;
		EntityPlayer old = event.original;

		if (event.wasDeath && CaveworldAPI.isEntityInCavenia(player))
		{
			if (old.getEntityData().hasKey("Cavenia:Inventory"))
			{
				player.inventory.readFromNBT(old.getEntityData().getTagList("Cavenia:Inventory", NBT.TAG_COMPOUND));
			}

			player.experienceLevel = old.experienceLevel;
			player.experienceTotal = old.experienceTotal;
			player.experience = old.experience;
			player.setScore(old.getScore());
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
		else if (itemstack.getItem() == CaveItems.gem)
		{
			if (thrower == null)
			{
				switch (itemstack.getItemDamage())
				{
					case 0:
						player.triggerAchievement(CaveAchievementList.aquamarine);
						break;
					case 3:
						player.triggerAchievement(CaveAchievementList.hexcite);
						break;
				}
			}
		}
		else if (itemstack.getItem() == Item.getItemFromBlock(CaveBlocks.perverted_log))
		{
			player.triggerAchievement(AchievementList.mineWood);
		}
	}

	@SubscribeEvent
	public void onItemSmelted(ItemSmeltedEvent event)
	{
		EntityPlayer player = event.player;
		ItemStack itemstack = event.smelting;

		if (itemstack != null)
		{
			if (itemstack.getItem() == CaveItems.gem && itemstack.getItemDamage() == 1)
			{
				player.triggerAchievement(CaveAchievementList.magnite);
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

			if (CaveworldAPI.isEntityInCaves(player))
			{
				if (ConfigHelper.isMiningPointValidItem(player.getCurrentEquippedItem()))
				{
					Block block = event.block;
					int meta = event.blockMetadata;
					int amount = CaverAPI.getMiningPointAmount(block, meta);

					MiningPointEvent.OnBlockBreak point = new MiningPointEvent.OnBlockBreak(player, amount);
					MinecraftForge.EVENT_BUS.post(point);

					amount = point.newAmount;

					if (amount != 0)
					{
						CaverAPI.addMiningPoint(player, amount);

						CaveNetworkRegistry.sendTo(new LastMineMessage(block, meta, amount), player);
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

		switch (event.action)
		{
			case LEFT_CLICK_BLOCK:
				if (!world.isRemote)
				{
					if (current != null && current.getItem() instanceof ICaveniumTool && current.getItemDamage() < current.getMaxDamage())
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
									CaveNetworkRegistry.sendTo(new MultiBreakCountMessage(executor.getBreakPositions().size()), (EntityPlayerMP)player);
								}
							}
						}
						else
						{
							tool.getMode(current).clear(player);

							CaveNetworkRegistry.sendTo(new MultiBreakCountMessage(0), (EntityPlayerMP)player);
						}
					}
					else if (current != null && current.getItem() instanceof ItemFarmingHoe)
					{
						if (player.capabilities.isCreativeMode)
						{
							Block block = world.getBlock(x, y, z);

							if (block instanceof IGrowable)
							{
								IGrowable growable = (IGrowable)block;

								if (!growable.func_149851_a(world, x, y, z, world.isRemote))
								{
									MultiBreakExecutor executor = ((ItemFarmingHoe)current.getItem()).getMode(current).getExecutor(player);

									if (executor != null)
									{
										executor.setOriginPos(x, y, z).setBreakPositions();
										executor.breakAll();
									}
								}
							}
						}
					}
					else if (current != null && current.getItem() instanceof ItemCaverBackpack && player.isSneaking())
					{
						ItemCaverBackpack item = (ItemCaverBackpack)current.getItem();
						IInventory inventory = null;
						Block block = world.getBlock(x, y, z);

						if (block == Blocks.ender_chest)
						{
							inventory = player.getInventoryEnderChest();
						}
						else if (block instanceof IInventory)
						{
							inventory = (IInventory)block;
						}
						else
						{
							TileEntity tile = world.getTileEntity(x, y, z);

							if (tile != null && tile instanceof IInventory)
							{
								inventory = (IInventory)tile;
							}
						}

						if (inventory != null)
						{
							item.carryInventory(item.getInventory(current), inventory);

							world.playSoundAtEntity(player, "random.pop", 1.0F, 0.75F);

							if (block instanceof BlockPortalCaveworld)
							{
								((BlockPortalCaveworld)block).displayInventory(player, x, y, z);
							}
							else
							{
								block.onBlockActivated(world, x, y, z, player, face, 0.0F, 0.0F, 0.0F);
							}

							event.setCanceled(true);
						}
					}
				}

				break;
			case RIGHT_CLICK_BLOCK:
				if (!world.isRemote)
				{
					if (current != null && current.getItem() instanceof ItemCaverBackpack && player.isSneaking())
					{
						ItemCaverBackpack item = (ItemCaverBackpack)current.getItem();
						IInventory inventory = null;
						Block block = world.getBlock(x, y, z);

						if (block == Blocks.ender_chest)
						{
							inventory = player.getInventoryEnderChest();
						}
						else if (block instanceof IInventory)
						{
							inventory = (IInventory)block;
						}
						else
						{
							TileEntity tile = world.getTileEntity(x, y, z);

							if (tile != null && tile instanceof IInventory)
							{
								inventory = (IInventory)tile;
							}
						}

						if (inventory != null)
						{
							item.carryInventory(inventory, item.getInventory(current));

							world.playSoundAtEntity(player, "random.pop", 1.0F, 0.75F);

							event.setCanceled(true);
						}
					}

					Item portal = null;

					if (!player.isSneaking() && current != null && (current.getItem() == Item.getItemFromBlock(Blocks.ender_chest) ||
						EnderStoragePlugin.enderChest != null && current.getItem() == Item.getItemFromBlock(EnderStoragePlugin.enderChest)))
					{
						portal = Item.getItemFromBlock(CaveBlocks.caveworld_portal);
					}
					else if (current != null && (current.getItem() == Items.emerald || CaveUtils.containsOreDict(current, "gemEmerald")))
					{
						portal = Item.getItemFromBlock(CaveBlocks.cavern_portal);
					}
					else if (current != null && (current.getItem() == CaveItems.gem && current.getItemDamage() == 0 || CaveUtils.containsOreDict(current, "gemAquamarine")))
					{
						portal = Item.getItemFromBlock(CaveBlocks.aqua_cavern_portal);
					}
					else if (current != null && current.getItem() == CaveItems.cavenium)
					{
						Block block = CaveBlocks.caveland_portal;

						if (current.getItemDamage() == 1)
						{
							block = CaveBlocks.cavenia_portal;
						}

						portal = Item.getItemFromBlock(block);
					}

					if (portal != null && portal.onItemUseFirst(current, player, world, x, y, z, face, x + 0.5F, y + 0.5F, z + 0.5F))
					{
						event.setCanceled(true);
					}

					Block block = world.getBlock(x, y, z);

					if (block == Blocks.pumpkin || block == Blocks.lit_pumpkin)
					{
						block = world.getBlock(x, y - 1, z);
						int meta = world.getBlockMetadata(x, y - 1, z);

						if (block == CaveBlocks.cavenium_ore && (meta == 2 || meta == 3))
						{
							if (block == world.getBlock(x, y - 2, z) && meta == world.getBlockMetadata(x, y - 2, z))
							{
								world.setBlock(x, y, z, Blocks.air, 0, 2);
								world.setBlock(x, y - 1, z, Blocks.air, 0, 2);
								world.setBlock(x, y - 2, z, Blocks.air, 0, 2);

								EntityCaveman entity = new EntityCaveman(world);
								int type = meta == 2 ? 1 : meta == 3 ? 2 : 0;

								entity.setCavemanType(type);
								entity.setHealth(entity.getMaxHealth());
								entity.setLocationAndAngles(x + 0.5D, y - 1.95D, z + 0.5D, 0.0F, 0.0F);

								if (current != null)
								{
									entity.setCurrentItemOrArmor(0, current);

									player.setCurrentItemOrArmor(0, null);
								}

								world.spawnEntityInWorld(entity);
								world.notifyBlockChange(x, y, z, Blocks.air);
								world.notifyBlockChange(x, y - 1, z, Blocks.air);
								world.notifyBlockChange(x, y - 2, z, Blocks.air);

								event.setCanceled(true);
							}
						}
					}
				}

				break;
			case RIGHT_CLICK_AIR:
				if (current != null && current.getItem() instanceof ItemFarmingHoe)
				{
					if (world.isRemote)
					{
						((ItemFarmingHoe)current.getItem()).setHighlightStart(System.currentTimeMillis());
					}
					else
					{
						NBTTagCompound nbt = current.getTagCompound();

						if (nbt == null)
						{
							current.setTagCompound(new NBTTagCompound());

							nbt = current.getTagCompound();
						}

						int i = nbt.getInteger("Mode");

						if (++i > FarmMode.values().length - 1)
						{
							i = 0;
						}

						nbt.setInteger("Mode", i);

						world.playSoundAtEntity(player, "random.click", 0.6F, 1.7F);
					}
				}

				break;
			default:
				break;
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		EntityPlayer player = event.entityPlayer;
		ItemStack current = player.getCurrentEquippedItem();
		boolean miner = CaveworldAPI.isEntityInCaves(player) && CaveUtils.isItemPickaxe(current);
		boolean flag = false;

		if (current != null)
		{
			if (miner && CaverAPI.getMinerRank(player) >= MinerRank.AQUA_MINER.getRank())
			{
				flag = true;
			}
			else
			{
				Item item = current.getItem();

				if (item instanceof ICaveniumTool)
				{
					item = ((ICaveniumTool)item).getBase(current);
				}

				if (item instanceof IAquamarineTool)
				{
					flag = true;
				}
				else if (item instanceof ItemTool)
				{
					if (((ItemTool)current.getItem()).func_150913_i() == CaveItems.AQUAMARINE)
					{
						flag = true;
					}
				}
			}
		}

		if (flag && player.isInWater() && !EnchantmentHelper.getAquaAffinityModifier(player))
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

		if (current != null && current.getItem() instanceof ICaveniumTool)
		{
			ICaveniumTool tool = (ICaveniumTool)current.getItem();

			if (!tool.getModeName(current).equalsIgnoreCase("NORMAL"))
			{
				int refined = tool.getRefined(current);

				if (refined >= 4 || current.getItem().getHarvestLevel(current, tool.getToolClass()) >= 3 && EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, current) >= 4)
				{
					return;
				}

				event.newSpeed = Math.min(event.newSpeed / (Caveworld.proxy.getMultiBreakCount(player) * (0.5F - refined * 0.1245F)), event.newSpeed);
			}
		}

		if (miner)
		{
			int rank = CaverAPI.getMinerRank(player);

			if (rank >= MinerRank.CRAZY_MINER.getRank())
			{
				event.newSpeed *= 2.0F;
			}
			else if (rank >= MinerRank.THE_MINER.getRank())
			{
				event.newSpeed *= 1.25F;
			}
		}
	}

	@SubscribeEvent
	public void onPlayerSleepInBed(PlayerSleepInBedEvent event)
	{
		if (event.entityPlayer instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;

			if (CaveworldAPI.isEntityInCaves(player) && !(player.capabilities.isCreativeMode || CaverAPI.getMinerRank(player) >= MinerRank.DIAMOND_MINER.getRank()))
			{
				long last = CaverAPI.getLastSleepTime(player);
				long time = player.getServerForPlayer().getTotalWorldTime();
				long require = 6000L;
				long remaining = require - (time - last);

				if (last + require > time)
				{
					event.result = EnumStatus.OTHER_PROBLEM;

					IChatComponent component = new ChatComponentTranslation("caveworld.message.sleep.still", remaining / 20 / 60 + 1);
					component.getChatStyle().setColor(EnumChatFormatting.RED);

					player.addChatMessage(component);
				}
				else
				{
					CaverAPI.setLastSleepTime(player, time);
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
		CaverAPI.getMiningPoint(event.entity);
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		Entity entity = event.entity;
		World world = event.world;

		if (entity instanceof EntityLivingBase)
		{
			CaverAPI.loadData(entity, null);
		}

		if (entity instanceof EntityLiving && CaveworldAPI.isEntityInCaves(entity))
		{
			if (entity.posY >= world.provider.getActualHeight() - 1)
			{
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent event)
	{
		EntityLivingBase target = event.entityLiving;
		DamageSource source = event.source;

		Entity entity = source.getEntity();

		if (entity != null && entity instanceof EntityLivingBase)
		{
			EntityLivingBase living = (EntityLivingBase)entity;
			ItemStack itemstack = living.getHeldItem();

			if (itemstack != null && !target.isPotionActive(Potion.resistance))
			{
				String type = Strings.nullToEmpty(source.getDamageType());

				if (type.equals("arrow"))
				{
					if (itemstack.getItem() instanceof ItemCavenicBow)
					{
						target.hurtResistantTime = 0;
					}
				}
				else
				{
					if (itemstack.getItem() instanceof ItemCaveSword)
					{
						ToolMaterial material = ((ItemCaveSword)itemstack.getItem()).getToolMaterial();

						if (material == CaveItems.MAGNITE)
						{
							target.hurtResistantTime = 0;
						}
					}
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
			CaverAPI.setMiningPoint(entity, 0);
		}

		if (CaveworldAPI.isEntityInCavenia(entity) && entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;

			player.getEntityData().setTag("Cavenia:Inventory", player.inventory.writeToNBT(new NBTTagList()));
		}

		CaverAPI.saveData(entity, null);
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase entity = event.entityLiving;

		if (entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;

			if (CaveworldAPI.isEntityInCaves(player) && player.isPlayerSleeping())
			{
				int time = ObfuscationReflectionHelper.getPrivateValue(EntityPlayer.class, player, "sleepTimer", "field_71076_b");

				if (time >= 75)
				{
					player.wakeUpPlayer(false, false, true);
				}
			}
		}

		if (CaveworldAPI.isEntityInCaves(entity) && entity.isInWater() && CaverAPI.getMinerRank(entity) >= MinerRank.AQUA_MINER.getRank())
		{
			if (!entity.canBreatheUnderwater() && entity.ticksExisted % 20 == 0)
			{
				entity.setAir(300);
			}

			if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).capabilities.isFlying)
			{
				double posY = entity.posY;
				float motion = 1.165F;

				entity.motionX *= motion;
				entity.motionZ *= motion;

				if (entity.isCollidedHorizontally && entity.isOffsetPositionInLiquid(entity.motionX, entity.motionY + 0.6000000238418579D - entity.posY + posY, entity.motionZ))
				{
					entity.motionY = 0.30000001192092896D;
				}

				if (entity.isSneaking())
				{
					entity.motionY = 0.0D;
				}
			}
		}

		if (SextiarySectorPlugin.enabled() && entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)entity;

			if (CaveworldAPI.isEntityInCaves(player) && !CaveworldAPI.isEntityInAquaCavern(player))
			{
				int i = 200;

				if (entity.isSprinting())
				{
					i /= 3;
				}

				if (player.ticksExisted % i == 0)
				{
					if (CaverAPI.getMinerRank(entity) >= MinerRank.IRON_MINER.getRank())
					{
						SextiarySectorAPI.addMoistureExhaustion(player, 2.0F);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event)
	{
		EntityLivingBase living = event.entityLiving;

		if (event.source.getDamageType().equals("player"))
		{
			if (CaveworldAPI.isEntityInCaves(living) && living instanceof EntityBat)
			{
				Random random = living.getRNG();
				int looting = MathHelper.clamp_int(event.lootingLevel, 0, 3);
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
		int dim = world.provider.dimensionId;

		if (!world.isRemote)
		{
			if (dim == CaveworldAPI.getDimension())
			{
				CaveBlocks.caveworld_portal.loadInventoryFromDimData();
			}
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
				WorldProviderCaveworld.saveHandler.writeToFile();
			}
			else if (dim == CaveworldAPI.getCavernDimension())
			{
				WorldProviderCavern.saveHandler.writeToFile();
			}
			else if (dim == CaveworldAPI.getAquaCavernDimension())
			{
				WorldProviderAquaCavern.saveHandler.writeToFile();
			}
			else if (dim == CaveworldAPI.getCavelandDimension())
			{
				WorldProviderCaveland.saveHandler.writeToFile();
			}
			else if (dim == CaveworldAPI.getCaveniaDimension())
			{
				WorldProviderCavenia.saveHandler.writeToFile();
			}
		}
	}

	@SubscribeEvent
	public void onServerChat(ServerChatEvent event)
	{
		String message = event.message;
		EntityPlayerMP player = event.player;

		if (message.equalsIgnoreCase("@caveworld") || message.equalsIgnoreCase("@cavemenu"))
		{
			CaveNetworkRegistry.sendTo(new CaveworldMenuMessage(), player);

			event.setCanceled(true);
		}
		else if (CaveworldAPI.isEntityInCaves(player))
		{
			if (message.matches("@buff|@buff ([0-9]*$|max)"))
			{
				int point = CaverAPI.getMiningPoint(player);

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
						CaverAPI.addMiningPoint(player, -point);

						player.addPotionEffect(new PotionEffect(potion.id, point * 20));
					}
				}

				event.setCanceled(true);
			}
			else if (message.equalsIgnoreCase("@cavemusic"))
			{
				if (CaveworldAPI.isEntityInAquaCavern(player))
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage("cavemusic.aqua"), player);
				}
				else if (CaveworldAPI.isEntityInCaveland(player))
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage("cavemusic.hope"), player);
				}
				else if (CaveworldAPI.isEntityInCavenia(player))
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage("cavemusic.battle" + (rand.nextInt(2) + 1)), player);
				}
				else
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage(rand.nextInt(3) == 0 ? "cavemusic.cave" : "cavemusic.unrest"), player);
				}

				event.setCanceled(true);
			}

			if (Config.showMinerRank && !event.isCanceled())
			{
				MinerRank rank = CaverManager.getRank(CaverAPI.getMinerRank(player));

				event.component = new ChatComponentTranslation("[%s] %s", new ChatComponentTranslation(rank.getUnlocalizedName()), event.component);
			}
		}
	}
}