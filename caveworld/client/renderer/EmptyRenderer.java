package caveworld.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.IRenderHandler;

@SideOnly(Side.CLIENT)
public class EmptyRenderer extends IRenderHandler
{
	public static final EmptyRenderer instance = new EmptyRenderer();

	@Override
	public void render(float ticks, WorldClient world, Minecraft mc) {}
}