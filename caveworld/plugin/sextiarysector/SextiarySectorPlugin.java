package caveworld.plugin.sextiarysector;

import caveworld.api.BlockEntry;
import caveworld.api.CaverAPI;
import caveworld.api.CaveworldAPI;
import caveworld.config.Config;
import caveworld.config.manager.CaveVeinManager.CaveVein;
import caveworld.item.CaveItems;
import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import shift.sextiarysector.api.recipe.RecipeAPI;

public class SextiarySectorPlugin implements ICavePlugin
{
	public static final String MODID = "SextiarySector";

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
		if (FMLLaunchHandler.side().isClient())
		{
			SSTabs.registerTabs();
		}

		RecipeAPI.pulverizer.add("oreMagnite", new ItemStack(CaveItems.gem, 2, 2));

		MinecraftForge.EVENT_BUS.register(new SSEventHooks());

		Block block = GameRegistry.findBlock(MODID, "CoalLargeOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 15, 20, 100, 0, 255));
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 15, 20, 100, 200, 255));
			}

			CaverAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "IronLargeOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 10, 25, 100, 0, 255));
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 10, 25, 100, 200, 255));
			}

			CaverAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "GoldLargeOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 7, 3, 100, 0, 255));
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 7, 3, 100, 200, 255));
			}

			CaverAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "BlueStoneOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 7, 8, 100, 0, 255));
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 7, 8, 100, 200, 255));
			}

			CaverAPI.setMiningPointAmount(block, 0, 2);
		}

		block = GameRegistry.findBlock(MODID, "YellowStoneOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 7, 8, 100, 0, 255));
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 7, 8, 100, 200, 255));
			}

			CaverAPI.setMiningPointAmount(block, 0, 2);
		}

		block = GameRegistry.findBlock(MODID, "OrichalcumOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 6, 3, 100, 0, 255));
				CaveworldAPI.addCavesVein(new CaveVein(new BlockEntry(block, 0), 6, 3, 100, 200, 255));
			}

			CaverAPI.setMiningPointAmount(block, 0, 2);
		}
	}
}