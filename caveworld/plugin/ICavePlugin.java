/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin;

public interface ICavePlugin
{
	public String getModId();

	public boolean getPluginState();

	public boolean setPluginState(boolean state);

	public void invoke();
}