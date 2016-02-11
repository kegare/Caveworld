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
import caveworld.core.Config;
import caveworld.util.Version;
import caveworld.world.ChunkProviderAquaCavern;
import caveworld.world.ChunkProviderCaveland;
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
	public int getAquaCavernDimension()
	{
		return ChunkProviderAquaCavern.dimensionId;
	}

	@Override
	public int getCavelandDimension()
	{
		return ChunkProviderCaveland.dimensionId;
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

	@Override
	public boolean isEntityInAquaCavern(Entity entity)
	{
		return entity != null && entity.dimension == getAquaCavernDimension();
	}

	@Override
	public boolean isEntityInCaveland(Entity entity)
	{
		return entity != null && entity.dimension == getCavelandDimension();
	}

	@Override
	public boolean isEntityInCaves(Entity entity)
	{
		return isEntityInCaveworld(entity) || isEntityInCavern(entity) || isEntityInAquaCavern(entity) || isEntityInCaveland(entity);
	}

	@Override
	public boolean isHardcore()
	{
		return Config.hardcore;
	}

	@Override
	public boolean isCaveborn()
	{
		return Config.caveborn;
	}
}