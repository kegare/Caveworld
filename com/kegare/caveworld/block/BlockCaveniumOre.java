/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.block;

import java.util.Random;

import net.minecraft.block.BlockOre;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

import com.kegare.caveworld.item.CaveItems;

public class BlockCaveniumOre extends BlockOre
{
	private final Random random = new Random();

	public BlockCaveniumOre(String name)
	{
		super();
		this.setBlockName(name);
		this.setBlockTextureName("caveworld:cavenium_ore");
		this.setHardness(3.5F);
		this.setResistance(5.0F);
		this.setStepSound(soundTypePiston);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Override
	public Item getItemDropped(int metadata, Random random, int fortune)
	{
		return CaveItems.cavenium;
	}

	@Override
	public int quantityDropped(Random random)
	{
		return random.nextInt(10) == 0 ? random.nextInt(10) == 0 ? 3 : 2 : 1;
	}

	@Override
	public int getExpDrop(IBlockAccess world, int metadata, int bonus)
	{
		return MathHelper.getRandomIntegerInRange(random, 2, 6);
	}
}