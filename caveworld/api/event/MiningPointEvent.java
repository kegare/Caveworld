package caveworld.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class MiningPointEvent extends Event
{
	public final EntityPlayer entityPlayer;
	public final int originalAmount;
	public int newAmount;

	public MiningPointEvent(EntityPlayer player, int amount)
	{
		this.entityPlayer = player;
		this.originalAmount = amount;
		this.newAmount = amount;
	}

	/**
	 * MiningPointEvent.OnBlockBreak is fired when a player gets mining point on block break.
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 */
	public static class OnBlockBreak extends MiningPointEvent
	{
		public OnBlockBreak(EntityPlayer player, int amount)
		{
			super(player, amount);
		}
	}

	/**
	 * MiningPointEvent.RankPromote is fired when a player is promote miner rank.
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 */
	public static class RankPromote extends MiningPointEvent
	{
		public RankPromote(EntityPlayer player, int amount)
		{
			super(player, amount);
		}
	}
}