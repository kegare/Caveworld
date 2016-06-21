package caveworld.util.farmer;

import java.util.Set;

import com.google.common.collect.Sets;

import caveworld.util.breaker.BreakPos.NearestBreakPosComparator;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public abstract class MultiFarmExecutor
{
	protected final EntityPlayer player;
	protected final FarmPos originPos = new FarmPos();
	protected final FarmPos currentPos = new FarmPos();
	protected final Set<FarmPos> farmPositions = Sets.newTreeSet(new NearestBreakPosComparator(originPos));

	public MultiFarmExecutor(EntityPlayer player)
	{
		this.player = player;
	}

	public MultiFarmExecutor setOriginPos(int x, int y, int z)
	{
		originPos.refresh(player.worldObj, x, y, z);
		currentPos.refresh(player.worldObj, x, y, z);

		farmPositions.clear();

		return this;
	}

	public FarmPos getOriginPos()
	{
		return originPos;
	}

	public boolean canFarm(int x, int y, int z)
	{
		if (originPos.world.isAirBlock(x, y, z))
		{
			return false;
		}

		Block block = originPos.world.getBlock(x, y, z);

		return block == Blocks.grass || block == Blocks.dirt;
	}

	public abstract MultiFarmExecutor setFarmPositions();

	public boolean offer(int x, int y, int z)
	{
		if (originPos.x == x && originPos.y == y && originPos.z == z)
		{
			return false;
		}

		if (canFarm(x, y, z))
		{
			currentPos.refresh(currentPos.world, x, y, z);

			return farmPositions.add(new FarmPos(currentPos));
		}

		return false;
	}

	public Set<FarmPos> getFarmPositions()
	{
		return farmPositions;
	}

	public void farmAll()
	{
		for (FarmPos pos : farmPositions)
		{
			ItemStack current = player.getCurrentEquippedItem();

			if (current != null && (!current.isItemStackDamageable() || current.getItemDamage() < current.getMaxDamage()) && !pos.isPlaced())
			{
				pos.doFarm(player);
			}
		}

		farmPositions.clear();
	}

	public void clear()
	{
		originPos.clear();
		currentPos.clear();
		farmPositions.clear();
	}
}
