/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.config.impl;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;

public class NumberSliderEntry extends cpw.mods.fml.client.config.GuiConfigEntries.NumberSliderEntry
{
	public NumberSliderEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement<?> configElement)
	{
		super(owningScreen, owningEntryList, configElement);
	}
}