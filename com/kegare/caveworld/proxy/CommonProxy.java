package com.kegare.caveworld.proxy;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class CommonProxy
{
	public void registerRenderers() {}

	public void addEffect(EntityFX entityFX) {}

	public MinecraftServer getServer()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}
}