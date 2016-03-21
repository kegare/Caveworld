/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import caveworld.world.WorldProviderCavern;
import net.minecraftforge.common.config.Configuration;

public class CavernVeinManager extends CaveVeinManager
{
	@Override
	public Configuration getConfig()
	{
		return Config.veinsCavernCfg;
	}

	@Override
	public int getType()
	{
		return WorldProviderCavern.TYPE;
	}
}