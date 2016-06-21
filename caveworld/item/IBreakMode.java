package caveworld.item;

import caveworld.util.breaker.MultiBreakExecutor;
import net.minecraft.entity.player.EntityPlayer;

public interface IBreakMode
{
	public MultiBreakExecutor getExecutor(EntityPlayer player);

	public boolean clear(EntityPlayer player);
}