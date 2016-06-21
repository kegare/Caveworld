package caveworld.block;

import java.util.ArrayList;
import java.util.Random;

import caveworld.core.CaveAchievementList;
import caveworld.item.CaveItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAcresia extends BlockCrops
{
	@SideOnly(Side.CLIENT)
	private IIcon[] blockIcons;

	public BlockAcresia(String name)
	{
		super();
		this.setBlockName(name);
		this.setBlockTextureName("caveworld:acresia");
	}

	@Override
	protected boolean canPlaceBlockOn(Block block)
	{
		return block != Blocks.bedrock && (block.isNormalCube() || block instanceof BlockFarmland);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		checkAndDropBlock(world, x, y, z);

		int meta = world.getBlockMetadata(x, y, z);

		if (meta < 4)
		{
			float f = getGrowChance(world, x, y, z);

			if (random.nextInt((int)(25.0F / f) + 1) == 0)
			{
				++meta;

				world.setBlockMetadataWithNotify(x, y, z, meta, 2);
			}
		}
	}

	private float getGrowChance(World world, int x, int y, int z)
	{
		float ret = 1.0F;
		Block block = world.getBlock(x, y, z - 1);
		Block block1 = world.getBlock(x, y, z + 1);
		Block block2 = world.getBlock(x - 1, y, z);
		Block block3 = world.getBlock(x + 1, y, z);
		Block block4 = world.getBlock(x - 1, y, z - 1);
		Block block5 = world.getBlock(x + 1, y, z - 1);
		Block block6 = world.getBlock(x + 1, y, z + 1);
		Block block7 = world.getBlock(x - 1, y, z + 1);
		boolean flag = block2 == this || block3 == this;
		boolean flag1 = block == this || block1 == this;
		boolean flag2 = block4 == this || block5 == this || block6 == this || block7 == this;

		for (int blockX = x - 1; blockX <= x + 1; ++blockX)
		{
			for (int blockZ = z - 1; blockZ <= z + 1; ++blockZ)
			{
				float rate = 0.0F;

				if (world.getBlock(blockX, y - 1, blockZ).canSustainPlant(world, blockX, y - 1, blockZ, ForgeDirection.UP, this))
				{
					rate = 4.0F;

					if (world.getBlock(blockX, y - 1, blockZ).isFertile(world, blockX, y - 1, blockZ))
					{
						rate = 8.0F;
					}
				}

				if (blockX != x || blockZ != z)
				{
					rate /= 4.0F;
				}

				ret += rate;
			}
		}

		if (flag2 || flag && flag1)
		{
			ret /= 2.0F;
		}

		return ret;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!func_149851_a(world, x, y, z, world.isRemote))
		{
			ItemStack current = player.getCurrentEquippedItem();

			if (current != null && current.getItem() instanceof ItemShears)
			{
				if (!world.isRemote)
				{
					int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, current);
					ItemStack itemstack = new ItemStack(func_149865_P(), 4 + world.rand.nextInt(3) + fortune, 1);
					EntityItem entityItem = new EntityItem(world, x + 0.5D, y + 0.25D, z + 0.5D, itemstack);

					entityItem.delayBeforeCanPickup = 10;

					world.spawnEntityInWorld(entityItem);
					world.setBlockMetadataWithNotify(x, y, z, 2, 2);

					current.damageItem(1, player);
				}

				world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "mob.sheep.shear", 1.0F, 1.0F);

				player.triggerAchievement(CaveAchievementList.acresia);

				return true;
			}
		}

		return false;
	}

	@Override
	protected Item func_149866_i()
	{
		return CaveItems.acresia;
	}

	@Override
	protected Item func_149865_P()
	{
		return CaveItems.acresia;
	}

	@Override
	public void func_149853_b(World world, Random random, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z) + MathHelper.getRandomIntegerInRange(world.rand, 2, 5);

		if (meta > 4)
		{
			meta = 4;
		}

		world.setBlockMetadataWithNotify(x, y, z, meta, 2);
	}

	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean flag)
	{
		return world.getBlockMetadata(x, y, z) != 4;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune)
	{
		return meta == 4 ? func_149865_P() : func_149866_i();
	}

	@Override
	public int damageDropped(int meta)
	{
		return meta == 4 ? 1 : 0;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> ret = super.getDrops(world, x, y, z, metadata, fortune);

		if (metadata >= 4)
		{
			for (int i = 0; i < 3 + fortune; ++i)
			{
				if (world.rand.nextInt(15) <= metadata)
				{
					ret.add(new ItemStack(func_149866_i(), 1, 0));
				}
			}
		}

		return ret;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		blockIcons = new IIcon[5];

		for (int i = 0; i < blockIcons.length; ++i)
		{
			blockIcons[i] = iconRegister.registerIcon(getTextureName() + "_stage_" + i);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta)
	{
		if (meta < 0 || meta > 4)
		{
			meta = 4;
		}

		return blockIcons[meta];
	}
}