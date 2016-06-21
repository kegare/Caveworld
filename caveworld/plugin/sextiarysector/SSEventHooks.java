package caveworld.plugin.sextiarysector;

import java.util.Random;

import caveworld.api.CaverAPI;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import shift.sextiarysector.api.event.player.PlayerMoistureEvent;
import shift.sextiarysector.api.event.player.PlayerStaminaEvent;

public class SSEventHooks
{
	protected static final Random rand = new Random();

	@SubscribeEvent
	public void onPlayerStaminaExhaustion(PlayerStaminaEvent.Exhaustion event)
	{
		EntityPlayer player = event.entityPlayer;

		if (player.getEntityData().getBoolean("CaveMultiBreak") && player.getCurrentEquippedItem() != null)
		{
			int rank = CaverAPI.getMinerRank(player);
			int i = Math.max(5 - rank, 1);

			if (i <= 1 || rand.nextInt(i) == 0)
			{
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerMoistureExhaustion(PlayerMoistureEvent.Exhaustion event)
	{
		EntityPlayer player = event.entityPlayer;

		if (player.getEntityData().getBoolean("CaveMultiBreak") && player.getCurrentEquippedItem() != null)
		{
			int rank = CaverAPI.getMinerRank(player);
			int i = Math.max(5 - rank, 1);

			if (i <= 1 || rand.nextInt(i) == 0)
			{
				event.setCanceled(true);
			}
		}
	}
}