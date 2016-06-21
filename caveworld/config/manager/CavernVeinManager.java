package caveworld.config.manager;

import caveworld.config.Config;
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