package caveworld.config.manager;

import caveworld.config.Config;
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