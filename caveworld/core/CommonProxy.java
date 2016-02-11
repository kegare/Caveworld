/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import caveworld.client.gui.MenuType;

public class CommonProxy
{
	public void initializeConfigEntries() {}

	public void registerKeyBindings() {}

	public void registerRenderers() {}

	public int getUniqueRenderType()
	{
		return -1;
	}

	public void displayMenu(MenuType type) {}

	public void displayPortalMenu(MenuType type, int x, int y, int z) {}
}