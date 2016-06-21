package caveworld.plugin.tconstruct;

import caveworld.plugin.ICavePlugin;
import caveworld.util.CaveUtils;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class TinkersConstructPlugin implements ICavePlugin
{
	public static final String MODID = "TConstruct";

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

	@Override
	public void invoke()
	{
		Item item = GameRegistry.findItem(MODID, "pickaxe");

		if (item != null)
		{
			CaveUtils.pickaxeItems.add(item);
		}

		item = GameRegistry.findItem(MODID, "axe");

		if (item != null)
		{
			CaveUtils.axeItems.add(item);
		}

		item = GameRegistry.findItem(MODID, "shovel");

		if (item != null)
		{
			CaveUtils.shovelItems.add(item);
		}
	}
}