/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import java.util.List;

import com.google.common.collect.Lists;

import caveworld.api.ICaveVein;
import caveworld.world.WorldProviderAquaCavern;
import net.minecraftforge.common.config.Configuration;

public class AquaCavernVeinManager extends CaveVeinManager
{
	private final List<ICaveVein> CAVE_VEINS = Lists.newArrayList();

	@Override
	public Configuration getConfig()
	{
		return Config.veinsAquaCavernCfg;
	}

	@Override
	public int getType()
	{
		return WorldProviderAquaCavern.TYPE;
	}

	@Override
	public List<ICaveVein> getCaveVeins()
	{
		return CAVE_VEINS;
	}
}