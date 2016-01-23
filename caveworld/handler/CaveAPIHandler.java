/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.handler;

import caveworld.api.ICaveAPIHandler;
import caveworld.util.Version;
import caveworld.world.ChunkProviderCavern;
import caveworld.world.ChunkProviderCaveworld;
import net.minecraft.entity.Entity;

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
		return ChunkProviderCaveworld.dimensionId;
	}

	@Override
	public int getCavernDimension()
	{
		return ChunkProviderCavern.dimensionId;
	}

	@Override
	public boolean isEntityInCaveworld(Entity entity)
	{
		return entity != null && entity.dimension == getDimension();
	}

	@Override
	public boolean isEntityInCavern(Entity entity)
	{
		return entity != null && entity.dimension == getCavernDimension();
	}
}