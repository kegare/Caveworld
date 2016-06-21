package caveworld.config.manager;

import caveworld.config.Config;
import caveworld.world.WorldProviderAquaCavern;
import net.minecraftforge.common.config.Configuration;

public class AquaCavernBiomeManager extends CaveBiomeManager
{
	@Override
	public Configuration getConfig()
	{
		return Config.biomesAquaCavernCfg;
	}

	@Override
	public int getType()
	{
		return WorldProviderAquaCavern.TYPE;
	}
}