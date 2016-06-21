package caveworld.item;

import caveworld.block.IRope;
import caveworld.core.CaveAchievementList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class ItemRope extends ItemBlock
{
	public ItemRope(Block block)
	{
		super(block);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (side == 1)
		{
			return false;
		}

		if (world.getBlock(x, y, z) == field_150939_a)
		{
			do
			{
				--y;
			}
			while (world.getBlock(x, y, z) == field_150939_a);
		}
		else
		{
			x += Facing.offsetsXForSide[side];
			y += Facing.offsetsYForSide[side];
			z += Facing.offsetsZForSide[side];
		}

		if (itemstack.stackSize > 0 && player.canPlayerEdit(x, y, z, side, itemstack) &&
			world.isAirBlock(x, y, z) && (world.getBlock(x, y + 1, z).getMaterial().isSolid() || world.getBlock(x, y + 1, z) == field_150939_a) &&
			world.canPlaceEntityOnSide(field_150939_a, x, y, z, false, side, player, itemstack))
		{
			int meta = ((IRope)field_150939_a).getKnotMetadata(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ);

			if (!placeBlockAt(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ, meta))
			{
				return false;
			}

			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, field_150939_a.stepSound.func_150496_b(), (field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, field_150939_a.stepSound.getPitch() * 0.8F);

			--itemstack.stackSize;

			if (player.isSneaking())
			{
				for (int i = 1; itemstack.stackSize > 0 && i < itemstack.stackSize + 1; ++i)
				{
					int next = y - 6 * i;

					if (next > 0 && world.getBlock(x, next + 1, z) == field_150939_a && world.isAirBlock(x, next, z))
					{
						if (placeBlockAt(itemstack, player, world, x, next, z, side, hitX, hitY, hitZ, meta))
						{
							--itemstack.stackSize;
						}
						else break;
					}
				}
			}

			if (player instanceof EntityPlayerMP)
			{
				if (!((EntityPlayerMP)player).func_147099_x().hasAchievementUnlocked(CaveAchievementList.theRoper) && ((IRope)field_150939_a).getRopesLength(world, x, y, z) >= 100)
				{
					player.triggerAchievement(CaveAchievementList.theRoper);
				}
			}

			return true;
		}

		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack itemstack)
	{
		if (side == 1)
		{
			return false;
		}

		if (world.getBlock(x, y, z) == field_150939_a)
		{
			do
			{
				--y;
			}
			while (world.getBlock(x, y, z) == field_150939_a);
		}
		else
		{
			x += Facing.offsetsXForSide[side];
			y += Facing.offsetsYForSide[side];
			z += Facing.offsetsZForSide[side];
		}

		return world.canPlaceEntityOnSide(field_150939_a, x, y, z, false, side, null, itemstack);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		if (world.setBlock(x, y, z, field_150939_a, metadata, 3))
		{
			if (!world.isRemote)
			{
				((IRope)field_150939_a).setUnderRopes(world, x, y, z);
			}

			return true;
		}

		return false;
	}
}