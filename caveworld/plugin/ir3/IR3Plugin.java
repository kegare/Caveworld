package caveworld.plugin.ir3;

import caveworld.block.CaveBlocks;
import caveworld.item.CaveItems;
import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import jp.plusplus.ir2.api.IR3RecipeAPI;
import jp.plusplus.ir2.api.IR3RecipeAPI.CrushPair;
import net.minecraft.item.ItemStack;

public class IR3Plugin implements ICavePlugin
{
	public static final String MODID = "jp-plusplus-ir2";

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
		IR3RecipeAPI.AddCrushing(new ItemStack(CaveBlocks.gem_ore, 1, 3), new CrushPair(1.0F, new ItemStack(CaveItems.gem, 2, 2)));
		IR3RecipeAPI.AddCrushing(new ItemStack(CaveBlocks.gem_ore, 1, 7), new CrushPair(1.0F, new ItemStack(CaveItems.gem, 1, 5)), new CrushPair(0.35F, new ItemStack(CaveItems.gem, 1, 5)));
	}
}