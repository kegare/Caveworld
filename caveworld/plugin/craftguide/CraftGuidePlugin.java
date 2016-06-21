package caveworld.plugin.craftguide;

import caveworld.config.Config;
import caveworld.item.CaveItems;
import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import net.minecraft.item.ItemStack;

public class CraftGuidePlugin implements ICavePlugin
{
	public static final String MODID = "craftguide";

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
			new CaveniumToolRecipeProvider(new ItemStack(CaveItems.mining_pickaxe));
			new CaveniumToolRecipeProvider(new ItemStack(CaveItems.lumbering_axe));
			new CaveniumToolRecipeProvider(new ItemStack(CaveItems.digging_shovel));
			new FarmingHoeRecipeProvider(new ItemStack(CaveItems.farming_hoe));
		}
	}
}