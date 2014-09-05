/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.config;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.StringEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SelectBlockEntry extends StringEntry
{
	public SelectBlockEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
	{
		super(owningScreen, owningEntryList, configElement);
	}

	@Override
	public void mouseClicked(int x, int y, int mouseEvent)
	{
		super.mouseClicked(x, y, mouseEvent);

		if (!Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && textFieldValue.isFocused())
		{
			textFieldValue.setFocused(false);

			mc.displayGuiScreen(new GuiSelectBlock(owningScreen, textFieldValue));
		}
	}
}