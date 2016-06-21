package caveworld.entity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityTorchArrow extends EntityCaveArrow
{
	public EntityTorchArrow(World world)
	{
		super(world);
	}

	public EntityTorchArrow(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	public EntityTorchArrow(World world, EntityLivingBase player, float par3)
	{
		super(world, player, par3);
	}

	@Override
	protected void onHit(Entity entity)
	{
		if (shootingEntity != null && shootingEntity instanceof EntityLivingBase)
		{
			EntityLivingBase living = (EntityLivingBase)shootingEntity;
			ItemStack itemstack = living.getHeldItem();

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemstack) > 0)
			{
				entity.setFire(10 + rand.nextInt(3));

				return;
			}
		}

		entity.setFire(3 + rand.nextInt(2));
	}

	@Override
	protected boolean tryPlaceBlock()
	{
		if (mop == null)
		{
			return true;
		}

		if (shootingEntity != null && shootingEntity instanceof EntityPlayer)
		{
			if (new ItemStack(Blocks.torch).tryPlaceItemIntoWorld((EntityPlayer)shootingEntity, worldObj, xTile, yTile, zTile, mop.sideHit, xTile + 0.5F, yTile + 0.5F, zTile + 0.5F))
			{
				return true;
			}
		}
		else
		{
			int x = xTile;
			int y = yTile;
			int z = zTile;
			int meta = 0;

			switch (mop.sideHit)
			{
				case 0:
					break;
				case 1:
					meta = 0;
					++y;
					break;
				case 2:
					--z;
					meta = 4;
					break;
				case 3:
					++z;
					meta = 3;
					break;
				case 4:
					--x;
					meta = 2;
					break;
				case 5:
					++x;
					meta = 1;
					break;
			}

			if (Blocks.torch.canPlaceBlockAt(worldObj, x, y, z) && worldObj.isAirBlock(x, y, z))
			{
				if (worldObj.setBlock(x, y, z, Blocks.torch, meta, 3))
				{
					return true;
				}
			}
		}

		dropItem(Item.getItemFromBlock(Blocks.torch), 1);

		return true;
	}

	@Override
	protected boolean addItemStackToInventory(EntityPlayer player)
	{
		return player.inventory.addItemStackToInventory(new ItemStack(Items.arrow, 1));
	}
}