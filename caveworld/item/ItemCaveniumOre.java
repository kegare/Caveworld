package caveworld.item;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemCaveniumOre extends ItemBlockWithMetadata
{
	public ItemCaveniumOre(Block block)
	{
		super(block, block);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		switch (itemstack.getItemDamage())
		{
			case 0:
				return "tile.oreCavenium";
			case 1:
				return "tile.oreCavenium.refined";
			case 2:
				return "tile.blockCavenium";
			case 3:
				return "tile.blockCavenium.refined";
		}

		return super.getUnlocalizedName(itemstack);
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack)
	{
		int damage = itemstack.getItemDamage();

		return damage == 1 || damage == 3 ? EnumRarity.rare : super.getRarity(itemstack);
	}
}