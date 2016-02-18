/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.gui;

public enum MenuType
{
	DEFAULT,
	CAVEWORLD_PORTAL(true),
	CAVERN_PORTAL(true),
	AQUA_CAVERN_PORTAL(true),
	CAVELAND_PORTAL(true),
	CAVENIA_PORTAL(true);

	private boolean portalMenu;

	private MenuType()
	{
		this(false);
	}

	private MenuType(boolean portal)
	{
		this.portalMenu = portal;
	}

	public boolean isPortalMenu()
	{
		return portalMenu;
	}
}