package caveworld.config.manager;

import caveworld.config.Config;
import caveworld.world.WorldProviderAquaCavern;
import net.minecraftforge.common.config.Configuration;

public class AquaCavernVeinManager extends CaveVeinManager
{
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
}