/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.gui;

import caveworld.api.CaveworldAPI;
import caveworld.block.CaveBlocks;
import caveworld.client.config.GuiBiomesEntry;
import caveworld.client.config.GuiVeinsEntry;
import caveworld.client.gui.GuiButtonVolume.IVolume;
import caveworld.core.Caveworld;
import caveworld.core.Config;
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.server.PortalInventoryMessage;
import caveworld.plugin.mceconomy.GuiShopEntry;
import caveworld.plugin.mceconomy.MCEconomyPlugin;
import caveworld.util.Version;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import shift.mceconomy2.api.MCEconomyAPI;

@SideOnly(Side.CLIENT)
public class GuiIngameCaveMenu extends GuiScreen implements IVolume
{
	private MenuType menuType = MenuType.DEFAULT;
	private int portalX;
	private int portalY;
	private int portalZ;

	private GuiButton prevPageButton;
	private GuiButton nextPageButton;
	private GuiButton backButton;
	private GuiButton regenButton;

	private GuiButton caveMusicButton;
	private GuiButton openInvButton;
	private GuiButton openShopButton;

	private GuiButton biomeButton;
	private GuiButton veinButton;
	private GuiButton shopButton;

	private static int pageIndex;

	public GuiIngameCaveMenu setMenuType(MenuType type)
	{
		menuType = type;

		return this;
	}

	public GuiIngameCaveMenu setPortalCoord(int x, int y, int z)
	{
		portalX = x;
		portalY = y;
		portalZ = z;

		return this;
	}

	@Override
	public void initGui()
	{
		GuiButton prev;

		prevPageButton = prev = new GuiButton(101, width / 2 - 100, 35, 20, 20, "<");
		nextPageButton = new GuiButton(102, prev.xPosition + 180, prev.yPosition, 20, 20, ">");

		backButton = prev = new GuiButtonExt(0, prev.xPosition, height / 4 + 8, I18n.format("menu.returnToGame"));

		switch (pageIndex)
		{
			case 1:
				if (menuType != MenuType.CAVENIA_PORTAL)
				{
					if (menuType != MenuType.CAVELAND_PORTAL)
					{
						biomeButton = prev = new GuiButtonExt(1, prev.xPosition, prev.yPosition + prev.height + 5, I18n.format(Caveworld.CONFIG_LANG + "biomes"));
					}

					veinButton = prev = new GuiButtonExt(2, prev.xPosition, prev.yPosition + prev.height + 5, I18n.format(Caveworld.CONFIG_LANG + "veins"));
				}

				if (MCEconomyPlugin.enabled())
				{
					shopButton = prev = new GuiButtonExt(4, prev.xPosition, prev.yPosition + prev.height + 5, I18n.format(Caveworld.CONFIG_LANG + "shop"));
				}

				caveMusicButton = openInvButton = openShopButton = null;
				break;
			default:
				if (!menuType.isPortalMenu())
				{
					caveMusicButton = prev = new GuiButtonVolume(7, prev.xPosition, prev.yPosition + prev.height + 5, I18n.format(Caveworld.CONFIG_LANG + Configuration.CATEGORY_GENERAL + ".caveMusicVolume"), Config.caveMusicVolume, this);
				}

				if (menuType == MenuType.CAVEWORLD_PORTAL)
				{
					openInvButton = prev = new GuiButtonExt(5, prev.xPosition, prev.yPosition + prev.height + 5, I18n.format(CaveBlocks.caveworld_portal.getInventoryName()));
				}

				if (menuType.isPortalMenu() && MCEconomyPlugin.enabled())
				{
					openShopButton = prev = new GuiButtonExt(6, prev.xPosition, prev.yPosition + prev.height + 5, I18n.format(MCEconomyPlugin.productManager.getShopName(mc.theWorld, mc.thePlayer)));
				}

				biomeButton = veinButton = shopButton = null;
				break;
		}

		regenButton = prev = new GuiButtonExt(3, prev.xPosition, prev.yPosition + prev.height + 5, I18n.format("caveworld.regenerate.gui.title"));

		buttonList.clear();
		buttonList.add(prevPageButton);
		buttonList.add(nextPageButton);
		buttonList.add(backButton);

		if (caveMusicButton != null)
		{
			buttonList.add(caveMusicButton);
		}

		if (biomeButton != null)
		{
			buttonList.add(biomeButton);
		}

		if (veinButton != null)
		{
			buttonList.add(veinButton);
		}

		buttonList.add(regenButton);

		if (shopButton != null)
		{
			buttonList.add(shopButton);
		}

		if (openInvButton != null)
		{
			buttonList.add(openInvButton);
		}

		if (openShopButton != null)
		{
			buttonList.add(openShopButton);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					mc.displayGuiScreen(null);
					mc.setIngameFocus();
					break;
				case 1:
					switch (menuType)
					{
						case CAVEWORLD_PORTAL:
							mc.displayGuiScreen(new GuiBiomesEntry(this, CaveworldAPI.biomeManager));
							break;
						case CAVERN_PORTAL:
							mc.displayGuiScreen(new GuiBiomesEntry(this, CaveworldAPI.biomeCavernManager));
							break;
						case AQUA_CAVERN_PORTAL:
							mc.displayGuiScreen(new GuiBiomesEntry(this, CaveworldAPI.biomeAquaCavernManager));
							break;
						default:
							mc.displayGuiScreen(new GuiIngameBiomeCustomize());
							break;
					}

					break;
				case 2:
					switch (menuType)
					{
						case CAVEWORLD_PORTAL:
							mc.displayGuiScreen(new GuiVeinsEntry(this, CaveworldAPI.veinManager));
							break;
						case CAVERN_PORTAL:
							mc.displayGuiScreen(new GuiVeinsEntry(this, CaveworldAPI.veinCavernManager));
							break;
						case AQUA_CAVERN_PORTAL:
							mc.displayGuiScreen(new GuiVeinsEntry(this, CaveworldAPI.veinAquaCavernManager));
							break;
						case CAVELAND_PORTAL:
							mc.displayGuiScreen(new GuiVeinsEntry(this, CaveworldAPI.veinCavelandManager));
							break;
						default:
							mc.displayGuiScreen(new GuiIngameVeinCustomize());
							break;
					}

					break;
				case 3:
					switch (menuType)
					{
						case CAVEWORLD_PORTAL:
							mc.displayGuiScreen(new GuiRegeneration(true, true, false, false, false, false));
							break;
						case CAVERN_PORTAL:
							mc.displayGuiScreen(new GuiRegeneration(true, false, true, false, false, false));
							break;
						case AQUA_CAVERN_PORTAL:
							mc.displayGuiScreen(new GuiRegeneration(true, false, false, true, false, false));
							break;
						case CAVELAND_PORTAL:
							mc.displayGuiScreen(new GuiRegeneration(true, false, false, false, true, false));
							break;
						case CAVENIA_PORTAL:
							mc.displayGuiScreen(new GuiRegeneration(true, false, false, false, false, true));
							break;
						default:
							mc.displayGuiScreen(new GuiRegeneration(true));
							break;
					}

					break;
				case 4:
					if (MCEconomyPlugin.enabled())
					{
						mc.displayGuiScreen(new GuiShopEntry(this, MCEconomyPlugin.productManager));
					}

					break;
				case 5:
					CaveNetworkRegistry.sendToServer(new PortalInventoryMessage(portalX, portalY, portalZ));
					break;
				case 6:
					if (MCEconomyPlugin.enabled())
					{
						MCEconomyAPI.openShopGui(MCEconomyPlugin.SHOP, mc.thePlayer, mc.theWorld, 0, 0, 0);
					}

					break;
				case 101:
					if (--pageIndex < 0)
					{
						pageIndex = 1;
					}

					initGui();
					break;
				case 102:
					if (++pageIndex > 1)
					{
						pageIndex = 0;
					}

					initGui();
					break;
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		drawGradientRect(0, 0, width, height, Integer.MIN_VALUE, Integer.MAX_VALUE);

		switch (menuType)
		{
			case CAVERN_PORTAL:
				drawCenteredString(fontRendererObj, I18n.format("cavern.menu.title"), width / 2, 40, 0xFFFFFF);
				break;
			case AQUA_CAVERN_PORTAL:
				drawCenteredString(fontRendererObj, I18n.format("aquacavern.menu.title"), width / 2, 40, 0xFFFFFF);
				break;
			case CAVELAND_PORTAL:
				drawCenteredString(fontRendererObj, I18n.format("caveland.menu.title"), width / 2, 40, 0xFFFFFF);
				break;
			case CAVENIA_PORTAL:
				drawCenteredString(fontRendererObj, I18n.format("cavenia.menu.title"), width / 2, 40, 0xFFFFFF);
				break;
			default:
				drawCenteredString(fontRendererObj, I18n.format("caveworld.menu.title"), width / 2, 40, 0xFFFFFF);
				break;
		}

		if (!menuType.isPortalMenu())
		{
			fontRendererObj.drawStringWithShadow(I18n.format("caveworld.menu.version", Version.getCurrent(), Version.getLatest()), 6, height - 12, 0xCCCCCC);
		}

		super.drawScreen(mouseX, mouseY, ticks);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void onVolumeChanged(int id, float volume)
	{
		Config.caveMusicVolume = volume;
		Config.generalCfg.getCategory(Configuration.CATEGORY_GENERAL).get("caveMusicVolume").set(Float.toString(volume));
	}

	@Override
	public void onGuiClosed()
	{
		Config.saveConfig(Config.generalCfg);
	}
}