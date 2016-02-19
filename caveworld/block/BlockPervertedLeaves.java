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
import java.util.Random;

import caveworld.core.Caveworld;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;

public class BlockPervertedLeaves extends BlockLeaves implements IBlockPreverted
{
	public static final String[][] types = new String[][] {{"leaves_oak", "leaves_spruce", "leaves_birch", "leaves_jungle"}, {"leaves_oak_opaque", "leaves_spruce_opaque", "leaves_birch_opaque", "leaves_jungle_opaque"}};

	public BlockPervertedLeaves(String name)
	{
		super();
		this.setBlockName(name);
		this.setBlockTextureName("leaves");
		this.setHardness(0.1F);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		for (int i = 0; i < types.length; ++i)
		{
			this.field_150129_M[i] = new IIcon[types[i].length];

			for (int j = 0; j < types[i].length; ++j)
			{
				this.field_150129_M[i][j] = iconRegister.registerIcon(types[i][j]);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		GameSettings options = RenderManager.instance.options;

		if (options != null)
		{
			setGraphicsLevel(options.fancyGraphics);
		}

		return (metadata & 3) == 1 ? field_150129_M[field_150127_b][1] : (metadata & 3) == 3 ? field_150129_M[field_150127_b][3] : (metadata & 3) == 2 ? field_150129_M[field_150127_b][2] : field_150129_M[field_150127_b][0];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderColor(int p_149741_1_)
	{
		return (p_149741_1_ & 3) == 1 ? ColorizerFoliage.getFoliageColorPine() : (p_149741_1_ & 3) == 2 ? ColorizerFoliage.getFoliageColorBirch() : super.getRenderColor(p_149741_1_);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z)
	{
		int meta = blockAccess.getBlockMetadata(x, y, z);

		return (meta & 3) == 1 ? ColorizerFoliage.getFoliageColorPine() : (meta & 3) == 2 ? ColorizerFoliage.getFoliageColorBirch() : super.colorMultiplier(blockAccess, x, y, z);
	}

	@Override
	public Block getBasedBlock()
	{
		return Blocks.leaves;
	}

	@Override
	public Item getItemDropped(int metadata, Random random, int fortune)
	{
		return Item.getItemFromBlock(CaveBlocks.perverted_sapling);
	}

	@Override
	protected int func_150123_b(int metadata)
	{
		int chance = super.func_150123_b(metadata);

		if ((metadata & 3) == 3)
		{
			chance = 40;
		}

		return chance;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < BlockPervertedLog.types.length; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String[] func_150125_e()
	{
		return BlockPervertedLog.types;
	}
}
