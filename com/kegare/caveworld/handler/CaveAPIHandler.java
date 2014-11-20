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
import net.minecraftforge.common.DimensionManager;

import com.kegare.caveworld.api.ICaveAPIHandler;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.world.ChunkProviderCaveworld;
import com.kegare.caveworld.world.ChunkProviderDeepCaveworld;

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
	public int getDeepDimension()
	{
		return ChunkProviderDeepCaveworld.dimensionId;
	}

	@Override
	public boolean isDeepExist()
	{
		return getDimension() != 0 && getDimension() != getDeepDimension() && DimensionManager.isDimensionRegistered(getDeepDimension());
	}

	@Override
	public boolean isEntityInCaveworld(Entity entity)
	{
		return entity != null && (entity.dimension == getDimension() || entity.dimension == getDeepDimension());
	}
}