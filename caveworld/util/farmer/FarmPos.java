/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.util.farmer;

import caveworld.util.breaker.BreakPos;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class FarmPos extends BreakPos
{
	public FarmPos() {}

	public FarmPos(World world, int x, int y, int z)
	{
		super(world, x, y, z);
	}

	public FarmPos(BreakPos pos)
	{
		super(pos);
	}

	public void doFarm(EntityPlayer player)
	{
		Block block = getCurrentBlock();

		if (world.getBlock(x, y + 1, z).isAir(world, x, y + 1, z) && (block == Blocks.grass || block == Blocks.dirt))
		{
			Block farm = Blocks.farmland;

			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, farm.stepSound.getStepResourcePath(), (farm.stepSound.getVolume() + 1.0F) / 2.0F, farm.stepSound.getPitch() * 0.8F);

			if (!world.isRemote)
			{
				world.setBlock(x, y, z, farm);

				player.getCurrentEquippedItem().damageItem(1, player);
			}
		}
	}
}