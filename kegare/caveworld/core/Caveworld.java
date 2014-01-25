package kegare.caveworld.core;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import kegare.caveworld.block.CaveBlock;
import kegare.caveworld.handler.CaveConnectionHandler;
import kegare.caveworld.handler.CaveEventHooks;
import kegare.caveworld.handler.CavePacketHandler;
import kegare.caveworld.proxy.CommonProxy;
import kegare.caveworld.util.Version;
import kegare.caveworld.world.WorldProviderCaveworld;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

@Mod
(
	modid = "kegare.caveworld"
)
@NetworkMod
(
	clientSideRequired = true,
	serverSideRequired = false,
	channels = {"caveworld.sync"},
	packetHandler = CavePacketHandler.class,
	connectionHandler = CaveConnectionHandler.class
)
public class Caveworld
{
	@Metadata("kegare.caveworld")
	public static ModMetadata metadata;

	@SidedProxy(modId = "kegare.caveworld", clientSide = "kegare.caveworld.proxy.ClientProxy", serverSide = "kegare.caveworld.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Version.versionCheck();

		Config.buildConfig();
		Config.initialize();

		CaveBlock.configure();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		DimensionManager.registerProviderType(Config.dimensionCaveworld, WorldProviderCaveworld.class, true);
		DimensionManager.registerDimension(Config.dimensionCaveworld, Config.dimensionCaveworld);

		MinecraftForge.EVENT_BUS.register(new CaveEventHooks());

		proxy.registerRenderers();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		Config.saveConfig(false);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandCaveworld());

		if (event.getSide().isServer() && (!FMLForgePlugin.RUNTIME_DEOBF || Config.versionNotify && Version.isOutdated()))
		{
			event.getServer().logInfo("A new Caveworld version is available : " + Version.LATEST.or(Version.CURRENT.orNull()));
		}
	}
}