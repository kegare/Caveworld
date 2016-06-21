package caveworld.config.manager;

import caveworld.config.Config;
import caveworld.world.WorldProviderCavern;
import net.minecraftforge.common.config.Configuration;

public class CavernBiomeManager extends CaveBiomeManager
{
	@Override
	public Configuration getConfig()
	{
		return Config.biomesCavernCfg;
	}

	@Override
	public int getType()
	{
		return WorldProviderCavern.TYPE;
	}
}