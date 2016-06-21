package caveworld.block;

import java.util.Random;

import caveworld.core.Caveworld;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockRopeLadder extends Block implements IRope
{
	@SideOnly(Side.CLIENT)
	private IIcon sideIcon;

	public BlockRopeLadder(String name)
	{
		super(BlockRope.rope);
		this.setBlockName(name);
		this.setBlockTextureName("caveworld:rope_ladder");
		this.setHardness(0.25F);
		this.setStepSound(soundTypeCloth);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getItemIconName()
	{
		return getTextureName();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);

		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
	{
		updateBlockBounds(blockAccess.getBlockMetadata(x, y, z));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);

		AxisAlignedBB result = super.getSelectedBoundingBoxFromPool(world, x, y, z);

		if (isKnot(world.getBlockMetadata(x, y, z)))
		{
			return result == null ? null : result.expand(0.05D, 0.0D, 0.05D);
		}

		return result;
	}

	public boolean isKnot(int meta)
	{
		return meta >= 4 && meta < 8 || meta >= 12 && meta < 16;
	}

	public void updateBlockBounds(int meta)
	{
		float f1 = 0.3F;
		float f2 = 0.1F;

		if (isKnot(meta))
		{
			meta -= 4;
		}

		switch (meta)
		{
			case 0:
				setBlockBounds(0.0F, 0.0F, 1.0F - f1, 1.0F, 1.0F, 0.65F);
				break;
			case 1:
				setBlockBounds(0.0F, 0.0F, 0.35F, 1.0F, 1.0F, f1);
				break;
			case 2:
				setBlockBounds(1.0F - f1, 0.0F, 0.0F, 0.65F, 1.0F, 1.0F);
				break;
			case 3:
				setBlockBounds(0.35F, 0.0F, 0.0F, f1, 1.0F, 1.0F);
				break;
			case 8:
				setBlockBounds(0.0F, 0.0F, 1.0F - f2, 1.0F, 1.0F, 1.0F);
				break;
			case 9:
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f2);
				break;
			case 10:
				setBlockBounds(1.0F - f2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				break;
			case 11:
				setBlockBounds(0.0F, 0.0F, 0.0F, f2, 1.0F, 1.0F);
				break;
		}
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
	{
		if (side == 2 && world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH))
		{
			return 8;
		}

		if (side == 3 && world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH))
		{
			return 9;
		}

		if (side == 4 && world.isSideSolid(x + 1, y, z, ForgeDirection.WEST))
		{
			return 10;
		}

		if (side == 5 && world.isSideSolid(x - 1, y, z, ForgeDirection.EAST))
		{
			return 11;
		}

		return 0;
	}

	@Override
	public int quantityDropped(int metadata, int fortune, Random random)
	{
		return isKnot(metadata) ? 1 : 0;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
	{
		if (!isKnot(world.getBlockMetadata(x, y, z)) && world.getBlock(x, y + 1, z) != this)
		{
			world.setBlockToAir(x, y, z);
		}
		else if (world.isAirBlock(x, y + 1, z) && world.setBlockToAir(x, y, z))
		{
			dropBlockAsItem(world, x, y, z, new ItemStack(this));
		}
	}

	@Override
	public int getKnotMetadata(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (world.getBlock(x, y + 1, z) == this)
		{
			int meta = world.getBlockMetadata(x, y + 1, z);

			if (isKnot(meta))
			{
				return meta;
			}

			return meta + 4;
		}

		if (side < 2 || !player.isSneaking())
		{
			int meta;

			switch (MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3)
			{
				case 1:
					meta = 3;
					break;
				case 2:
					meta = 1;
					break;
				case 3:
					meta = 2;
					break;
				default:
					meta = 0;
					break;
			}

			return meta + 4;
		}

		return onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, 0) + 4;
	}

	@Override
	public void setUnderRopes(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if (isKnot(meta) && world.getBlock(x, y, z) == this && world.isAirBlock(x, y - 1, z) && y - 1 > 0)
		{
			for (int count = 0; count < 5 && world.isAirBlock(x, y - 1, z) && y - 1 > 0; --y)
			{
				if (world.isAirBlock(x, y - 2, z) && world.setBlock(x, y - 1, z, this, meta - 4, 3))
				{
					if (!world.isRemote)
					{
						FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendToAllNear(x, y - 1, z, 64.0D, world.provider.dimensionId, new S23PacketBlockChange(x, y - 1, z, world));
					}

					++count;
				}
				else return;
			}
		}
	}

	@Override
	public int getRopesLength(World world, int x, int y, int z)
	{
		if (world.getBlock(x, y, z) == this)
		{
			int ry = y;

			do
			{
				++ry;
			}
			while (world.getBlock(x, ry, z) == this);

			int max = ry - 1;

			ry = y;

			do
			{
				--ry;
			}
			while (world.getBlock(x, ry, z) == this);

			return max - ry + 1;
		}

		return 0;
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return !world.isAirBlock(x, y + 1, z);
	}

	@Override
	public boolean func_149698_L()
	{
		return false;
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		super.registerBlockIcons(iconRegister);

		sideIcon = iconRegister.registerIcon(getTextureName() + "_side");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if (isKnot(meta))
		{
			meta -= 4;
		}

		if (meta == 0 || meta == 1 || meta == 8 || meta == 9)
		{
			if (side == 2 || side == 3)
			{
				return super.getIcon(world, x, y, z, side);
			}

			return sideIcon;
		}

		if (meta == 2 || meta == 3 || meta == 10 || meta == 11)
		{
			if (side == 4 || side == 5)
			{
				return super.getIcon(world, x, y, z, side);
			}

			return sideIcon;
		}

		return super.getIcon(world, x, y, z, side);
	}
}