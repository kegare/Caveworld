package caveworld.plugin.hexagonaldia;

import caveworld.api.BlockEntry;
import caveworld.api.CaverAPI;
import caveworld.api.CaveworldAPI;
import caveworld.config.Config;
import caveworld.config.manager.CaveVeinManager.CaveVein;
import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class HexagonalDiamondPlugin implements ICavePlugin
{
	public static final String MODID = "HexagonalDia";

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
		Block block = GameRegistry.findBlock(MODID, "hexagonaldiaore");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 1, 1, 10, 1, 10));
			}

			CaverAPI.setMiningPointAmount(block, 0, 10);
		}
	}
}