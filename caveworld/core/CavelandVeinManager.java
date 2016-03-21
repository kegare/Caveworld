/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import caveworld.world.WorldProviderCaveland;
import net.minecraftforge.common.config.Configuration;

public class CavelandVeinManager extends CaveVeinManager
{
	@Override
	public Configuration getConfig()
	{
		return Config.veinsCavelandCfg;
	}

	@Override
	public int getType()
	{
		return WorldProviderCaveland.TYPE;
	}
}