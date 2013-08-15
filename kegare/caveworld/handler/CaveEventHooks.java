package kegare.caveworld.handler;

import kegare.caveworld.util.CaveLog;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
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
}