package caveworld.item;

import java.util.List;
import java.util.Map.Entry;

import caveworld.core.Caveworld;
import caveworld.entity.CaveEntityRegistry;
import caveworld.entity.CaveEntityRegistry.EggInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemCaveMobPlacer extends ItemMonsterPlacer
{
	public ItemCaveMobPlacer()
	{
		this.setUnlocalizedName("monsterPlacer");
		this.setTextureName("spawn_egg");
		this.setHasSubtypes(true);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int pass)
	{
		EggInfo info = CaveEntityRegistry.mobs.get(itemstack.getItemDamage());

		return info == null ? 0xFFFFFF : pass == 0 ? info.primaryColor : info.secondaryColor;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack)
	{
		String name = ("" + StatCollector.translateToLocal(getUnlocalizedName() + ".name")).trim();
		Class entityClass = CaveEntityRegistry.entities.get(itemstack.getItemDamage());
		String entityName = (String)EntityList.classToStringMapping.get(entityClass);

		if (entityName != null)
		{
			name = name + " " + StatCollector.translateToLocal("entity." + entityName + ".name");
		}

		return name;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			return true;
		}
		else
		{
			Block block = world.getBlock(x, y, z);
			x += Facing.offsetsXForSide[side];
			y += Facing.offsetsYForSide[side];
			z += Facing.offsetsZForSide[side];
			double spawnY = 0.0D;

			if (side == 1 && block.getRenderType() == 11)
			{
				spawnY = 0.5D;
			}

			EntityLiving entity = spawnCreature(world, itemstack.getItemDamage(), x + 0.5D, y + spawnY, z + 0.5D);

			if (entity != null)
			{
				if (itemstack.hasDisplayName())
				{
					entity.setCustomNameTag(itemstack.getDisplayName());
				}

				if (!player.capabilities.isCreativeMode)
				{
					--itemstack.stackSize;
				}
			}

			return true;
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (world.isRemote)
		{
			return itemstack;
		}
		else
		{
			MovingObjectPosition pos = getMovingObjectPositionFromPlayer(world, player, true);

			if (pos == null)
			{
				return itemstack;
			}
			else
			{
				if (pos.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
				{
					int i = pos.blockX;
					int j = pos.blockY;
					int k = pos.blockZ;

					if (!world.canMineBlock(player, i, j, k))
					{
						return itemstack;
					}

					if (!player.canPlayerEdit(i, j, k, pos.sideHit, itemstack))
					{
						return itemstack;
					}

					if (world.getBlock(i, j, k) instanceof BlockLiquid)
					{
						EntityLiving entity = spawnCreature(world, itemstack.getItemDamage(), i, j, k);

						if (entity != null)
						{
							if (itemstack.hasDisplayName())
							{
								entity.setCustomNameTag(itemstack.getDisplayName());
							}

							if (!player.capabilities.isCreativeMode)
							{
								--itemstack.stackSize;
							}
						}
					}
				}

				return itemstack;
			}
		}
	}

	public static EntityLiving spawnCreature(World world, int id, double posX, double posY, double posZ)
	{
		Class<? extends Entity> entityClass = CaveEntityRegistry.entities.get(id);
		EntityLiving entity = null;

		try
		{
			entity = (EntityLiving)entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
			entity.setLocationAndAngles(posX, posY, posZ, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
			entity.rotationYawHead = entity.rotationYaw;
			entity.renderYawOffset = entity.rotationYaw;
			entity.onSpawnWithEgg((IEntityLivingData)null);
			world.spawnEntityInWorld(entity);
			entity.playLivingSound();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return entity;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (Entry<Integer, EggInfo> mob : CaveEntityRegistry.mobs.entrySet())
		{
			if (mob.getValue() != null)
			{
				list.add(new ItemStack(item, 1, mob.getKey()));
			}
		}
	}

	public class DispenceEgg extends BehaviorDefaultDispenseItem
	{
		@Override
		public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemstack)
		{
			EnumFacing facing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
			double x = blockSource.getX() + facing.getFrontOffsetX();
			double y = blockSource.getYInt() + 0.2F;
			double z = blockSource.getZ() + facing.getFrontOffsetZ();
			EntityLiving entity = spawnCreature(blockSource.getWorld(), itemstack.getItemDamage(), x, y, z);

			if (entity != null)
			{
				if (itemstack.hasDisplayName())
				{
					entity.setCustomNameTag(itemstack.getDisplayName());
				}

				itemstack.splitStack(1);
			}

			return itemstack;
		}
	}
}