package com.kegare.caveworld.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.IRenderHandler;

public class EmptyRenderer extends IRenderHandler
{
	@Override
	@SideOnly(Side.CLIENT)
	public void render(float ticks, WorldClient world, Minecraft mc) {}
}