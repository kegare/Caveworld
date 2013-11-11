package kegare.caveworld.handler;

import kegare.caveworld.core.Caveworld;
import kegare.caveworld.util.CaveLog;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CaveEventHooks
{
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onSoundLoad(SoundLoadEvent event)
	{
		try
		{
			event.manager.addSound("caveworld:portal/travel.ogg");
		}
		catch (Exception e)
		{
			CaveLog.exception(e);
		}
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onRenderOverlayText(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (Caveworld.showDebugDim && mc.gameSettings.showDebugInfo)
		{
			event.left.add("dim: " + mc.thePlayer.worldObj.provider.getDimensionName());
		}
	}
}