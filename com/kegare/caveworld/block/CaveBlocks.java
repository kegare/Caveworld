/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.block;

import com.kegare.caveworld.item.ItemPortalCaveworld;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class CaveBlocks
{
	public static final BlockPortalCaveworld caveworld_portal = new BlockPortalCaveworld("portalCaveworld");

	public static void configure()
	{
		GameRegistry.registerBlock(caveworld_portal, ItemPortalCaveworld.class, "caveworld_portal");

		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(caveworld_portal), new BehaviorDefaultDispenseItem()
		{
			@Override
			public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemstack)
			{
				EnumFacing facing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
				World world = blockSource.getWorld();
				int x = blockSource.getXInt() + facing.getFrontOffsetX();
				int y = blockSource.getYInt() + facing.getFrontOffsetY();
				int z = blockSource.getZInt() + facing.getFrontOffsetZ();

				if (caveworld_portal.func_150000_e(world, x, y, z))
				{
					--itemstack.stackSize;
				}

				return itemstack;
			}

			@Override
			public void playDispenseSound(IBlockSource blockSource)
			{
				super.playDispenseSound(blockSource);

				blockSource.getWorld().playSoundEffect(blockSource.getXInt(), blockSource.getYInt(), blockSource.getZInt(), caveworld_portal.stepSound.func_150496_b(), 1.0F, 2.0F);
			}
		});
	}
}