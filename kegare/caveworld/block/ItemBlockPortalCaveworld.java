package kegare.caveworld.block;

import kegare.caveworld.core.CaveBlock;
import kegare.caveworld.core.Config;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockPortalCaveworld extends ItemBlock
{
	public ItemBlockPortalCaveworld(int blockID)
	{
		super(blockID);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if ((player.dimension == 0 || player.dimension == Config.dimensionCaveworld) && world.getBlockId(x, y, z) == Block.cobblestoneMossy.blockID)
		{
			if (side == 0)
			{
				--y;
			}
			else if (side == 1)
			{
				++y;
			}
			else if (side == 2)
			{
				--z;
			}
			else if (side == 3)
			{
				++z;
			}
			else if (side == 4)
			{
				--x;
			}
			else if (side == 5)
			{
				++x;
			}

			if (player.canPlayerEdit(x, y, z, side, itemstack) && world.isAirBlock(x, y, z) && CaveBlock.portalCaveworld.tryToCreatePortal(world, x, y, z))
			{
				world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "step.stone", 1.0F, 2.0F);

				--itemstack.stackSize;
			}

			return true;
		}
		else
		{
			return false;
		}
	}
}