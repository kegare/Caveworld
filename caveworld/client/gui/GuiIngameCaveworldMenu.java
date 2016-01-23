/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.gui;

import caveworld.core.Caveworld;
import caveworld.plugin.mceconomy.GuiShopEntry;
import caveworld.plugin.mceconomy.MCEconomyPlugin;
import caveworld.util.Version;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiIngameCaveworldMenu extends GuiScreen
{
	private GuiButton backButton;
	private GuiButton biomeButton;
	private GuiButton veinButton;
	private GuiButton shopButton;
	private GuiButton regenButton;

	@Override
	public void initGui()
	{
		backButton = new GuiButtonExt(0, width / 2 - 100, height / 4 + 8, I18n.format("menu.returnToGame"));
		biomeButton = new GuiButtonExt(1, backButton.xPosition, backButton.yPosition + backButton.height + 5, I18n.format(Caveworld.CONFIG_LANG + "biomes"));
		veinButton = new GuiButtonExt(2, biomeButton.xPosition, biomeButton.yPosition + biomeButton.height + 5, I18n.format(Caveworld.CONFIG_LANG + "veins"));
		regenButton = new GuiButtonExt(3, veinButton.xPosition, veinButton.yPosition + veinButton.height + 5, I18n.format("caveworld.regenerate.gui.title"));

		if (MCEconomyPlugin.enabled())
		{
			shopButton = new GuiButtonExt(4, veinButton.xPosition, veinButton.yPosition + veinButton.height + 5, I18n.format(Caveworld.CONFIG_LANG + "shop"));

			regenButton.xPosition = shopButton.xPosition;
			regenButton.yPosition = shopButton.yPosition + shopButton.height + 5;
		}

		if (!mc.isSingleplayer())
		{
			biomeButton.enabled = false;
			veinButton.enabled = false;

			if (shopButton != null)
			{
				shopButton.enabled = false;
			}
		}

		buttonList.clear();
		buttonList.add(backButton);
		buttonList.add(biomeButton);
		buttonList.add(veinButton);
		buttonList.add(regenButton);

		if (shopButton != null)
		{
			buttonList.add(shopButton);
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
					mc.displayGuiScreen(new GuiIngameBiomeCustomize());
					break;
				case 2:
					mc.displayGuiScreen(new GuiIngameVeinCustomize());
					break;
				case 3:
					mc.displayGuiScreen(new GuiRegeneration(true));
					break;
				case 4:
					mc.displayGuiScreen(new GuiShopEntry(this));
					break;
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, I18n.format("caveworld.menu.title"), width / 2, 40, 0xFFFFFF);
		fontRendererObj.drawString(String.format("Caveworld %s (Latest: %s)", Version.getCurrent(), Version.getLatest()), 6, height - 12, 0xBABABA);

		super.drawScreen(mouseX, mouseY, ticks);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}