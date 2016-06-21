package caveworld.plugin;

import java.util.List;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import caveworld.plugin.advancedtools.AdvancedToolsPlugin;
import caveworld.plugin.applemilktea.AppleMilkTeaPlugin;
import caveworld.plugin.bamboomod.BambooModPlugin;
import caveworld.plugin.craftguide.CraftGuidePlugin;
import caveworld.plugin.enderstorage.EnderStoragePlugin;
import caveworld.plugin.hexagonaldia.HexagonalDiamondPlugin;
import caveworld.plugin.ic2.IC2Plugin;
import caveworld.plugin.ir3.IR3Plugin;
import caveworld.plugin.mapletree.MapleTreePlugin;
import caveworld.plugin.materialarms.MaterialArmsPlugin;
import caveworld.plugin.mceconomy.MCEconomyPlugin;
import caveworld.plugin.mekanism.MekanismPlugin;
import caveworld.plugin.moreinventory.MIMPlugin;
import caveworld.plugin.nei.NEIPlugin;
import caveworld.plugin.sextiarysector.SextiarySectorPlugin;
import caveworld.plugin.tconstruct.TinkersConstructPlugin;
import caveworld.plugin.thaumcraft.ThaumcraftPlugin;
import caveworld.util.CaveLog;
import cpw.mods.fml.common.Loader;

public class CavePlugins
{
	public static final List<ICavePlugin> PLUGINS = Lists.newArrayList();
	public static final List<ICavePlugin> CLIENT_PLUGINS = Lists.newArrayList();

	public static void registerPlugins()
	{
		PLUGINS.add(new AdvancedToolsPlugin());
		PLUGINS.add(new AppleMilkTeaPlugin());
		PLUGINS.add(new BambooModPlugin());
		PLUGINS.add(new EnderStoragePlugin());
		PLUGINS.add(new HexagonalDiamondPlugin());
		PLUGINS.add(new IC2Plugin());
		PLUGINS.add(new IR3Plugin());
		PLUGINS.add(new MapleTreePlugin());
		PLUGINS.add(new MaterialArmsPlugin());
		PLUGINS.add(new MCEconomyPlugin());
		PLUGINS.add(new MekanismPlugin());
		PLUGINS.add(new MIMPlugin());
		PLUGINS.add(new SextiarySectorPlugin());
		PLUGINS.add(new TinkersConstructPlugin());
		PLUGINS.add(new ThaumcraftPlugin());
		CLIENT_PLUGINS.add(new CraftGuidePlugin());
		CLIENT_PLUGINS.add(new NEIPlugin());
	}

	public static void invokePlugins()
	{
		for (ICavePlugin plugin : PLUGINS)
		{
			try
			{
				if (plugin.getPluginState() && Loader.isModLoaded(plugin.getModId()))
				{
					plugin.invoke();
				}
			}
			catch (Throwable e)
			{
				CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: " + plugin.getClass().getSimpleName());
			}
		}
	}

	public static void invokeClientPlugins()
	{
		for (ICavePlugin plugin : CLIENT_PLUGINS)
		{
			try
			{
				if (plugin.getPluginState() && Loader.isModLoaded(plugin.getModId()))
				{
					plugin.invoke();
				}
			}
			catch (Throwable e)
			{
				CaveLog.log(Level.WARN, e, "Failed to trying invoke client plugin: " + plugin.getClass().getSimpleName());
			}
		}
	}
}