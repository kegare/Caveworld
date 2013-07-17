package kegare.caveworld.proxy;

import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.client.FMLClientHandler;

public class ClientProxy extends CommonProxy
{
	@Override
	public MinecraftServer getServer()
	{
		return FMLClientHandler.instance().getClient().getIntegratedServer();
	}
}