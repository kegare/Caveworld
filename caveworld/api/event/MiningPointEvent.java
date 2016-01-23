package caveworld.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class MiningPointEvent extends Event
{
	public final EntityPlayer entityPlayer;

	public MiningPointEvent(EntityPlayer player, int amount)
	{
		this.entityPlayer = player;
	}

	/**
	 * MiningPointEvent.OnBlockBreak is fired when a player gets mining point on block break.
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 */
	public static class OnBlockBreak extends MiningPointEvent
	{
		public final int originalAmount;
		public int newAmount;

		public OnBlockBreak(EntityPlayer player, int amount)
		{
			super(player, amount);
			this.originalAmount = amount;
			this.newAmount = amount;
		}
	}
}