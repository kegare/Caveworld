package caveworld.plugin.advancedtools;

import caveworld.plugin.ICavePlugin;
import caveworld.util.CaveUtils;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class AdvancedToolsPlugin implements ICavePlugin
{
	public static final String MODID = "AdvancedTools";

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
		Item item = GameRegistry.findItem(MODID, "ugwoodpickaxe");

		if (item != null)
		{
			CaveUtils.pickaxeItems.add(item);
		}

		item = GameRegistry.findItem(MODID, "ugstonepickaxe");

		if (item != null)
		{
			CaveUtils.pickaxeItems.add(item);
		}

		item = GameRegistry.findItem(MODID, "ugironpickaxe");

		if (item != null)
		{
			CaveUtils.pickaxeItems.add(item);
		}

		item = GameRegistry.findItem(MODID, "ugdiamondpickaxe");

		if (item != null)
		{
			CaveUtils.pickaxeItems.add(item);
		}

		item = GameRegistry.findItem(MODID, "uggoldpickaxe");

		if (item != null)
		{
			CaveUtils.pickaxeItems.add(item);
		}
	}
}