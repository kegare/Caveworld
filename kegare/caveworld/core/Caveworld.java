package kegare.caveworld.core;

import java.io.File;

import kegare.caveworld.block.BlockPortalCaveworld;
import kegare.caveworld.handler.CaveConnectionHandler;
import kegare.caveworld.handler.CaveEventHooks;
import kegare.caveworld.handler.CavePacketHandler;
import kegare.caveworld.item.ItemPortalCaveworld;
import kegare.caveworld.proxy.CommonProxy;
import kegare.caveworld.util.CaveLog;
import kegare.caveworld.util.Version;
import kegare.caveworld.world.WorldProviderCaveworld;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

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
	public static boolean versionNotify = true;
	public static boolean showDebugDim = true;

	public static BlockPortalCaveworld portalCaveworld;

	public static int dimensionCaveworld = -75;
	public static int[] genBiomes = {0, 1, 2, 3, 4, 5, 6, 7, 12, 13, 16, 17, 18, 19, 21, 22};
	public static boolean generateCaves = true;
	public static boolean generateLakes = true;
	public static boolean generateRavine = true;
	public static boolean generateMineshaft = true;
	public static boolean generateDungeon = true;

	@Metadata("kegare.caveworld")
	public static ModMetadata metadata;

	@SidedProxy(modId = "kegare.caveworld", clientSide = "kegare.caveworld.proxy.ClientProxy", serverSide = "kegare.caveworld.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration cfg = new Configuration(new File(event.getModConfigurationDirectory(), "Caveworld.cfg"));

		try
		{
			cfg.load();

			cfg.addCustomCategoryComment(Configuration.CATEGORY_BLOCK, "If multiplayer, values must match on client-side and server-side.");
			cfg.addCustomCategoryComment("caveworld", "If multiplayer, server-side only.");

			versionNotify = cfg.get("general", "versionNotify", versionNotify, "Whether or not to notify when a new Caveworld version is available. [true/false]").getBoolean(versionNotify);
			showDebugDim = cfg.get("general", "showDebugDim", showDebugDim, "Whether or not to show the dimension name to debug screen. [true/false] Note: client-side only.").getBoolean(showDebugDim);

			portalCaveworld = new BlockPortalCaveworld(cfg.getBlock("portalCaveworld", 750, "BlockID - Block of Caveworld Portal").getInt(750), "portalCaveworld");

			dimensionCaveworld = cfg.get("caveworld", "dimensionCaveworld", dimensionCaveworld, "DimensionID - Caveworld").getInt(dimensionCaveworld);
			genBiomes = cfg.get("caveworld", "genBiomes", genBiomes, "Biomes to generate in Caveworld. Specify in BiomeIDs. [0-255]").getIntList();
			generateCaves = cfg.get("caveworld", "generateCaves", generateCaves, "Whether or not to generate caves to Caveworld. [true/false]").getBoolean(generateCaves);
			generateLakes = cfg.get("caveworld", "generateLakes", generateLakes, "Whether or not to generate lakes to Caveworld. [true/false]").getBoolean(generateLakes);
			generateRavine = cfg.get("caveworld", "generateRavine", generateRavine, "Whether or not to generate ravine to Caveworld. [true/false]").getBoolean(generateRavine);
			generateMineshaft = cfg.get("caveworld", "generateMineshaft", generateMineshaft, "Whether or not to generate mineshaft to Caveworld. [true/false]").getBoolean(generateMineshaft);
			generateDungeon = cfg.get("caveworld", "generateDungeon", generateDungeon, "Whether or not to generate dungeon to Caveworld. [true/false]").getBoolean(generateDungeon);
		}
		catch (Exception e)
		{
			CaveLog.exception(e);
		}
		finally
		{
			if (cfg.hasChanged())
			{
				cfg.save();
			}
		}

		GameRegistry.registerBlock(portalCaveworld, ItemPortalCaveworld.class, "portalCaveworld");
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		DimensionManager.registerProviderType(dimensionCaveworld, WorldProviderCaveworld.class, true);
		DimensionManager.registerDimension(dimensionCaveworld, dimensionCaveworld);

		MinecraftForge.EVENT_BUS.register(new CaveEventHooks());

		proxy.registerRenderers();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandCaveworld());

		if (event.getSide().isServer() && versionNotify && Version.isOutdated())
		{
			event.getServer().logInfo("A new Caveworld version is available : " + Version.LATEST);
		}
	}
}