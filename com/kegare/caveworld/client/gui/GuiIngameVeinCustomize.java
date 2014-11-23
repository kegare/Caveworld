/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.client.config.GuiVeinsEntry;
import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIngameVeinCustomize extends GuiScreen
{
	private GuiButton backButton;
	private GuiButton caveworldButton;
	private GuiButton deepButton;

	@Override
	public void initGui()
	{
		backButton = new GuiButtonExt(0, width / 2 - 100, height / 4 + 8, I18n.format("menu.returnToGame"));
		caveworldButton = new GuiButtonExt(1, backButton.xPosition, backButton.yPosition + backButton.height + 5, "Caveworld");
		deepButton = new GuiButtonExt(2, caveworldButton.xPosition, caveworldButton.yPosition + caveworldButton.height + 5, "Deep Caveworld");

		buttonList.clear();
		buttonList.add(backButton);
		buttonList.add(caveworldButton);
		buttonList.add(deepButton);
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
					mc.displayGuiScreen(new GuiVeinsEntry(this, CaveworldAPI.veinDeepManager));
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