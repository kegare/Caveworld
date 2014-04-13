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

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;

import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.item.ItemPortalCaveworld;
import com.kegare.caveworld.item.ItemRope;

import cpw.mods.fml.common.registry.GameRegistry;

public class CaveBlocks
{
	public static BlockPortalCaveworld caveworld_portal;
	public static BlockRope rope;

	public static void initialize()
	{
		caveworld_portal = new BlockPortalCaveworld("portalCaveworld");

		if (Config.rope)
		{
			rope = new BlockRope("rope");
		}
	}

	public static void register()
	{
		GameRegistry.registerBlock(caveworld_portal, ItemPortalCaveworld.class, "caveworld_portal");

		OreDictionary.registerOre("portalCaveworld", caveworld_portal);

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

		if (rope != null)
		{
			GameRegistry.registerBlock(rope, ItemRope.class, "rope");

			GameRegistry.addShapelessRecipe(new ItemStack(rope), Items.string, Items.string, Items.string, Items.leather);

			Blocks.fire.setFireInfo(rope, 15, 100);

			OreDictionary.registerOre("rope", new ItemStack(rope));

			Item item = Item.getItemFromBlock(rope);
			ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST, new WeightedRandomChestContent(item, 0, 2, 5, 10));
			ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(item, 0, 3, 6, 10));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(item, 0, 3, 6, 10));

			BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(rope), new BehaviorDefaultDispenseItem()
			{
				@Override
				public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemstack)
				{
					EnumFacing facing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
					World world = blockSource.getWorld();
					int x = blockSource.getXInt() + facing.getFrontOffsetX();
					int y = blockSource.getYInt() + facing.getFrontOffsetY();
					int z = blockSource.getZInt() + facing.getFrontOffsetZ();

					if (world.isAirBlock(x, y, z) && world.setBlock(x, y, z, rope, 1, 3))
					{
						--itemstack.stackSize;

						for (int i = 1; itemstack.stackSize > 0 && i < itemstack.stackSize + 1; ++i)
						{
							int next = y - 5 * i;

							if (world.getBlock(x, next, z) == rope && world.isAirBlock(x, --next, z) && next > 0)
							{
								if (world.setBlock(x, next, z, rope, 1, 3))
								{
									--itemstack.stackSize;
								}
								else break;
							}
						}
					}

					return itemstack;
				}

				@Override
				public void playDispenseSound(IBlockSource blockSource)
				{
					super.playDispenseSound(blockSource);

					blockSource.getWorld().playSoundEffect(blockSource.getXInt(), blockSource.getYInt(), blockSource.getZInt(), rope.stepSound.func_150496_b(), 1.0F, 2.0F);
				}
			});
		}
	}
}