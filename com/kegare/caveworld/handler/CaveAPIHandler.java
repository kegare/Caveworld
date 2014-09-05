/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.handler;

import net.minecraft.entity.Entity;

import com.kegare.caveworld.api.ICaveAPIHandler;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.util.Version;

public class CaveAPIHandler implements ICaveAPIHandler
{
	@Override
	public String getVersion()
	{
		return Version.getCurrent();
	}

	@Override
	public int getDimension()
	{
		return Config.dimensionCaveworld;
	}

	@Override
	public boolean isEntityInCaveworld(Entity entity)
	{
		return entity == null ? false : entity.dimension == getDimension();
	}
}