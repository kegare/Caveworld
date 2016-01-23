package caveworld.api;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;

public class BlockEntry
{
	private Block block;
	private int metadata;

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

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof BlockEntry)
		{
			BlockEntry entry = (BlockEntry)obj;

			return getBlock() == entry.getBlock() && getMetadata() == entry.getMetadata();
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		Object[] elements = {getBlock().getUnlocalizedName(), getMetadata()};
		int result = 0;

		for (Object element : elements)
		{
			result = 31 * result + (element == null ? 0 : element.hashCode());
		}

		return result;
	}
}