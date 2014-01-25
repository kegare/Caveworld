package kegare.caveworld.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.server.MinecraftServer;

public class CommonProxy
{
	public void registerRenderers() {}

	public void addEffect(EntityFX entityFX) {}

	public MinecraftServer getServer()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}
}