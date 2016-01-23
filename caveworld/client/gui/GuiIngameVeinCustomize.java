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
import caveworld.client.config.GuiVeinsEntry;
import caveworld.core.Caveworld;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiIngameVeinCustomize extends GuiScreen
{
	private GuiButton backButton;
	private GuiButton caveworldButton, cavernButton;

	@Override
	public void initGui()
	{
		if (backButton == null)
		{
			backButton = new GuiButtonExt(0, 0, 0, I18n.format("menu.returnToGame"));
		}

		backButton.xPosition = width / 2 - 100;
		backButton.yPosition = height / 4 + 8;

		if (caveworldButton == null)
		{
			caveworldButton = new GuiButtonExt(1, 0, 0, "Caveworld");
		}

		caveworldButton.xPosition = backButton.xPosition;
		caveworldButton.yPosition = backButton.yPosition + backButton.height + 5;

		if (cavernButton == null)
		{
			cavernButton = new GuiButtonExt(2, 0, 0, "Cavern");
		}

		cavernButton.xPosition = caveworldButton.xPosition;
		cavernButton.yPosition = caveworldButton.yPosition + caveworldButton.height + 5;

		buttonList.clear();
		buttonList.add(backButton);
		buttonList.add(caveworldButton);
		buttonList.add(cavernButton);
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
					mc.displayGuiScreen(new GuiVeinsEntry(this, CaveworldAPI.veinManager));
					break;
				case 2:
					mc.displayGuiScreen(new GuiBiomesEntry(this, CaveworldAPI.biomeCavernManager));
					break;
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "veins"), width / 2, 40, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}