package caveworld.api;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class BlockEntry
{
	private Block block;
	private int metadata;

	private ItemStack itemstack;

	public BlockEntry(Block block, int metadata)
	{
		this.block = block;
		this.metadata = metadata;
	}

	public BlockEntry(String name, int metadata)
	{
		this(Block.getBlockFromName(name), metadata);
	}

	public Block getBlock()
	{
		return block == null ? Blocks.stone : block;
	}

	public int getMetadata()
	{
		return MathHelper.clamp_int(metadata, 0, 15);
	}

	public ItemStack getItemStack()
	{
		if (itemstack == null)
		{
			itemstack = new ItemStack(getBlock(), 1, getMetadata());
		}

		return itemstack;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof BlockEntry))
		{
			return false;
		}

		BlockEntry entry = (BlockEntry)obj;

		return getBlock() == entry.getBlock() && getMetadata() == entry.getMetadata();
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	public String getString()
	{
		String name = GameData.getBlockRegistry().getNameForObject(block);

		return metadata == 0 ? name : name + ":" + metadata;
	}

	@Override
	public String toString()
	{
		return GameData.getBlockRegistry().getNameForObject(block) + ":" + metadata;
	}
}