package kegare.caveworld.item;

import kegare.caveworld.core.CaveBlock;
import kegare.caveworld.core.Config;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPortalCaveworld extends ItemBlock
{
	public ItemPortalCaveworld(int blockID)
	{
		super(blockID);
		this.setMaxStackSize(1);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
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

		if ((player.dimension == 0 || player.dimension == Config.dimensionCaveworld) && CaveBlock.portalCaveworld.tryToCreatePortal(world, x, y, z))
		{
			world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "step.stone", 1.0F, 2.0F);

			if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0)
			{
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			}

			return true;
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canPlaceItemBlockOnSide(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack itemstack)
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

		return world.getBlockId(x, y - 1, z) == Block.cobblestoneMossy.blockID && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z) && world.isAirBlock(x, y + 2, z) && world.getBlockId(x, y + 3, z) == Block.cobblestoneMossy.blockID;
	}
}