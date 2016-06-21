package caveworld.item;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemUniversalChest extends ItemBlock
{
	public ItemUniversalChest(Block block)
	{
		super(block);
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack)
	{
		return ItemCavenium.cavenium;
	}
}