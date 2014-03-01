/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.proxy;

import com.kegare.caveworld.renderer.RenderPortalCaveworld;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.server.MinecraftServer;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerBlockHandler(new RenderPortalCaveworld());
	}

	@Override
	public int getUniqueRenderType()
	{
		return RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public void addEffect(EntityFX entityFX)
	{
		FMLClientHandler.instance().getClient().effectRenderer.addEffect(entityFX);
	}

	@Override
	public MinecraftServer getServer()
	{
		return FMLClientHandler.instance().getServer();
	}
}