/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.block;

import java.util.List;

import caveworld.core.Caveworld;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockPervertedLog extends BlockLog implements IBlockPreverted
{
	public static final String[] types = new String[] {"oak", "spruce", "birch", "jungle"};

	public BlockPervertedLog(String name)
	{
		super();
		this.setBlockName(name);
		this.setBlockTextureName("log");
		this.setHardness(1.2F);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		field_150167_a = new IIcon[types.length];
		field_150166_b = new IIcon[types.length];

		for (int i = 0; i < field_150167_a.length; ++i)
		{
			field_150167_a[i] = iconRegister.registerIcon(getTextureName() + "_" + types[i]);
			field_150166_b[i] = iconRegister.registerIcon(getTextureName() + "_" + types[i] + "_top");
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < types.length; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public Block getBasedBlock()
	{
		return Blocks.log;
	}
}