package caveworld.item;

import caveworld.block.BlockPervertedLog;
import caveworld.block.IBlockPreverted;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemPerverted extends ItemBlockWithMetadata
{
	public ItemPerverted(Block block)
	{
		super(block, block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack)
	{
		int i = itemstack.getItemDamage();

		if (i < 0 || i >= BlockPervertedLog.types.length)
		{
			i = 0;
		}

		String name = ("" + StatCollector.translateToLocal(((IBlockPreverted)field_150939_a).getBasedBlock().getUnlocalizedName() + "." + BlockPervertedLog.types[i] + ".name")).trim();

		return ("" + StatCollector.translateToLocal(getUnlocalizedName() + ".type.name")).trim() + " " + name;
	}
}