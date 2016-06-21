package caveworld.plugin.nei;

import caveworld.config.Config;
import caveworld.plugin.ICavePlugin;
import codechicken.nei.api.API;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;

public class NEIPlugin implements ICavePlugin
{
	public static final String MODID = "NotEnoughItems";

	public static boolean pluginState = true;

	public static boolean enabled()
	{
		return pluginState && Loader.isModLoaded(MODID);
	}

	@Override
	public String getModId()
	{
		return MODID;
	}

	@Override
	public boolean getPluginState()
	{
		return pluginState;
	}

	@Override
	public boolean setPluginState(boolean state)
	{
		return pluginState = state;
	}

	@Method(modid = MODID)
	@Override
	public void invoke()
	{
		if (!Config.disableCaveniumTools)
		{
			TemplateRecipeHandler handler = new MiningPickaxeRecipeHandler();

			API.registerRecipeHandler(handler);
			API.registerUsageHandler(handler);

			handler = new LumberingAxeRecipeHandler();

			API.registerRecipeHandler(handler);
			API.registerUsageHandler(handler);

			handler = new DiggingShovelRecipeHandler();

			API.registerRecipeHandler(handler);
			API.registerUsageHandler(handler);

			handler = new FarmingHoeRecipeHandler();

			API.registerRecipeHandler(handler);
			API.registerUsageHandler(handler);
		}
	}
}