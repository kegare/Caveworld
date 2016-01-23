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
import caveworld.client.config.GuiBiomesEntry;
import caveworld.core.Caveworld;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiIngameCavernMenu extends GuiScreen
{
	private GuiButton backButton;
	private GuiButton biomeButton;
	private GuiButton veinButton;
	private GuiButton regenButton;

	@Override
	public void initGui()
	{
		backButton = new GuiButtonExt(0, width / 2 - 100, height / 4 + 8, I18n.format("menu.returnToGame"));
		biomeButton = new GuiButtonExt(1, backButton.xPosition, backButton.yPosition + backButton.height + 5, I18n.format(Caveworld.CONFIG_LANG + "biomes"));
		veinButton = new GuiButtonExt(2, biomeButton.xPosition, biomeButton.yPosition + biomeButton.height + 5, I18n.format(Caveworld.CONFIG_LANG + "veins"));
		regenButton = new GuiButtonExt(3, veinButton.xPosition, veinButton.yPosition + veinButton.height + 5, I18n.format("caveworld.regenerate.gui.title"));

		if (!mc.isSingleplayer())
		{
			biomeButton.enabled = false;
			veinButton.enabled = false;
		}

		buttonList.clear();
		buttonList.add(backButton);
		buttonList.add(biomeButton);
		buttonList.add(veinButton);
		buttonList.add(regenButton);
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
					mc.displayGuiScreen(new GuiBiomesEntry(this, CaveworldAPI.biomeCavernManager));
					break;
				case 2:
					mc.displayGuiScreen(new GuiBiomesEntry(this, CaveworldAPI.biomeCavernManager));
					break;
				case 3:
					mc.displayGuiScreen(new GuiRegeneration(true, false, true));
					break;
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, I18n.format("cavern.menu.title"), width / 2, 40, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}