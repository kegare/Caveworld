package caveworld.util.breaker;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Sets;

import caveworld.util.breaker.BreakPos.NearestBreakPosComparator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class MultiBreakExecutor
{
	public static final AtomicInteger positionsCount = new AtomicInteger(0);

	protected final EntityPlayer player;
	protected final BreakPos originPos = new BreakPos();
	protected final BreakPos currentPos = new BreakPos();
	protected final Set<BreakPos> breakPositions = Sets.newTreeSet(new NearestBreakPosComparator(originPos));

	public MultiBreakExecutor(EntityPlayer player)
	{
		this.player = player;
	}

	public MultiBreakExecutor setOriginPos(int x, int y, int z)
	{
		originPos.refresh(player.worldObj, x, y, z);
		currentPos.refresh(player.worldObj, x, y, z);

		breakPositions.clear();

		return this;
	}

	public BreakPos getOriginPos()
	{
		return originPos;
	}

	public boolean canBreak(int x, int y, int z)
	{
		if (originPos.world.isAirBlock(x, y, z))
		{
			return false;
		}

		return originPos.getCurrentBlock() == originPos.world.getBlock(x, y, z) && originPos.getCurrentMetadata() == originPos.world.getBlockMetadata(x, y, z);
	}

	public abstract MultiBreakExecutor setBreakPositions();

	public boolean offer(int x, int y, int z)
	{
		if (originPos.x == x && originPos.y == y && originPos.z == z)
		{
			return false;
		}

		if (canBreak(x, y, z))
		{
			currentPos.refresh(currentPos.world, x, y, z);

			return breakPositions.add(new BreakPos(currentPos));
		}

		return false;
	}

	public Set<BreakPos> getBreakPositions()
	{
		return breakPositions;
	}

	public void breakAll()
	{
		player.getEntityData().setBoolean("CaveMultiBreak", true);

		for (BreakPos pos : breakPositions)
		{
			ItemStack current = player.getCurrentEquippedItem();

			if (current != null && (!current.isItemStackDamageable() || current.getItemDamage() < current.getMaxDamage()) && !pos.isPlaced())
			{
				pos.doBreak(player);
			}
		}

		player.getEntityData().removeTag("CaveMultiBreak");

		breakPositions.clear();
	}

	public void clear()
	{
		originPos.clear();
		currentPos.clear();
		breakPositions.clear();
	}
}