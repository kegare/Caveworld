package com.kegare.caveworld.handler;

import net.minecraft.entity.Entity;

import com.kegare.caveworld.api.ICaveAPIHandler;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.world.WorldProviderCaveworld;

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
		return entity == null ? false : entity.worldObj.provider instanceof WorldProviderCaveworld;
	}
}