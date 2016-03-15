/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.entity;

import java.util.List;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class EntityCaveArrow extends EntityArrow implements IThrowableEntity
{
	protected int xTile = -1;
	protected int yTile = -1;
	protected int zTile = -1;
	protected Block inTile;
	protected int inData = 0;
	protected boolean inGround = false;

	protected MovingObjectPosition mop = null;

	protected int ticksInGround;
	protected int ticksInAir = 0;
	protected double damage = 2.0D;

	protected int knockbackStrength;

	public EntityCaveArrow(World world)
	{
		super(world);
	}

	public EntityCaveArrow(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	public EntityCaveArrow(World world, EntityLivingBase player, EntityLivingBase target, float par3, float par4)
	{
		super(world, player, target, par3, par4);
	}

	public EntityCaveArrow(World world, EntityLivingBase player, float par3)
	{
		super(world, player, par3);
	}

	@Override
	public void setThrowableHeading(double x, double y, double z, float par7, float par8)
	{
		float f = MathHelper.sqrt_double(x * x + y * y + z * z);
		x /= f;
		y /= f;
		z /= f;
		x += rand.nextGaussian() * 0.007499999832361937D * par8;
		y += rand.nextGaussian() * 0.007499999832361937D * par8;
		z += rand.nextGaussian() * 0.007499999832361937D * par8;
		x *= par7;
		y *= par7;
		z *= par7;
		motionX = x;
		motionY = y;
		motionZ = z;
		f = MathHelper.sqrt_double(x * x + z * z);
		prevRotationYaw   = rotationYaw   = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
		prevRotationPitch = rotationPitch = (float)(Math.atan2(y, f) * 180.0D / Math.PI);
		ticksInGround = 0;
	}

	@Override
	public Entity getThrower()
	{
		return shootingEntity;
	}

	@Override
	public void setThrower(Entity entity)
	{
		shootingEntity = entity;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setVelocity(double x, double y, double z)
	{
		motionX = x;
		motionY = y;
		motionZ = z;

		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt_double(x * x + z * z);
			prevRotationYaw = rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float)(Math.atan2(y, f) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch;
			prevRotationYaw = rotationYaw;
			setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
			ticksInGround = 0;
		}
	}

	@Override
	public void onUpdate()
	{
		onEntityUpdate();

		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

			prevRotationYaw = rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float)(Math.atan2(motionY, f) * 180.0D / Math.PI);
		}

		Block block = worldObj.getBlock(xTile, yTile, zTile);

		if (block.getMaterial() != Material.air)
		{
			block.setBlockBoundsBasedOnState(worldObj, xTile, yTile, zTile);
			AxisAlignedBB box = block.getCollisionBoundingBoxFromPool(worldObj, xTile, yTile, zTile);

			if (box != null && box.isVecInside(Vec3.createVectorHelper(posX, posY, posZ)))
			{
				inGround = true;
			}
		}

		if (arrowShake > 0)
		{
			--arrowShake;
		}

		if (inGround)
		{
			onHit(block, worldObj.getBlockMetadata(xTile, yTile, zTile));
		}
		else
		{
			++ticksInAir;
			Vec3 posVec = Vec3.createVectorHelper(posX, posY, posZ);
			Vec3 motionVec = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
			MovingObjectPosition object = worldObj.func_147447_a(posVec, motionVec, false, true, false);
			posVec = Vec3.createVectorHelper(posX, posY, posZ);
			motionVec = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);

			if (object != null)
			{
				motionVec = Vec3.createVectorHelper(object.hitVec.xCoord, object.hitVec.yCoord, object.hitVec.zCoord);
			}

			Entity entity = null;
			List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;
			int i;
			float f;

			for (i = 0; i < list.size(); ++i)
			{
				Entity entity1 = (Entity)list.get(i);

				if (entity1.canBeCollidedWith() && (entity1 != shootingEntity || ticksInAir >= 5))
				{
					f = 0.3F;
					AxisAlignedBB box = entity1.boundingBox.expand(f, f, f);
					MovingObjectPosition calc = box.calculateIntercept(posVec, motionVec);

					if (calc != null)
					{
						double d1 = posVec.distanceTo(calc.hitVec);

						if (d1 < d0 || d0 == 0.0D)
						{
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if (entity != null)
			{
				object = new MovingObjectPosition(entity);
			}

			if (object != null && object.entityHit != null && object.entityHit instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)object.entityHit;

				if (player.capabilities.disableDamage || shootingEntity instanceof EntityPlayer && !((EntityPlayer)shootingEntity).canAttackPlayer(player))
				{
					object = null;
				}
			}

			float f1;
			float f2;

			if (object != null)
			{
				if (object.entityHit != null)
				{
					f1 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
					int dot = MathHelper.ceiling_double_int(f1 * damage);

					if (getIsCritical())
					{
						dot += rand.nextInt(dot / 2 + 2);
					}

					DamageSource source = null;

					if (shootingEntity == null)
					{
						source = DamageSource.causeArrowDamage(this, this);
					}
					else
					{
						source = DamageSource.causeArrowDamage(this, shootingEntity);
					}

					if (isBurning() && !(object.entityHit instanceof EntityEnderman))
					{
						object.entityHit.setFire(5);
					}

					if (shootingEntity != null && shootingEntity instanceof EntityMasterCavenicSkeleton && object.entityHit instanceof EntityLivingBase)
					{
						EntityLivingBase living = (EntityLivingBase)object.entityHit;

						if (!living.isPotionActive(Potion.resistance))
						{
							living.hurtResistantTime = 0;
						}
					}

					if (object.entityHit.attackEntityFrom(source, dot))
					{
						if (object.entityHit instanceof EntityLivingBase)
						{
							EntityLivingBase living = (EntityLivingBase)object.entityHit;

							if (!worldObj.isRemote)
							{
								living.setArrowCountInEntity(living.getArrowCountInEntity() + 1);
							}

							if (knockbackStrength > 0)
							{
								f2 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

								if (f2 > 0.0F)
								{
									object.entityHit.addVelocity(motionX * knockbackStrength * 0.6000000238418579D / f2, 0.1D, motionZ * knockbackStrength * 0.6000000238418579D / f2);
								}
							}

							if (shootingEntity != null && shootingEntity instanceof EntityLivingBase)
							{
								EnchantmentHelper.func_151384_a(living, shootingEntity);
								EnchantmentHelper.func_151385_b((EntityLivingBase)shootingEntity, living);
							}

							if (shootingEntity != null && object.entityHit != shootingEntity && object.entityHit instanceof EntityPlayer && shootingEntity instanceof EntityPlayerMP)
							{
								((EntityPlayerMP)shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
							}
						}

						playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));

						onHit(object.entityHit);

						if (!(object.entityHit instanceof EntityEnderman))
						{
							setDead();
						}
					}
					else
					{
						motionX *= -0.10000000149011612D;
						motionY *= -0.10000000149011612D;
						motionZ *= -0.10000000149011612D;
						rotationYaw += 180.0F;
						prevRotationYaw += 180.0F;
						ticksInAir = 0;
					}
				}
				else
				{
					xTile = object.blockX;
					yTile = object.blockY;
					zTile = object.blockZ;
					inTile = worldObj.getBlock(xTile, yTile, zTile);
					inData = worldObj.getBlockMetadata(xTile, yTile, zTile);
					motionX = (float)(object.hitVec.xCoord - posX);
					motionY = (float)(object.hitVec.yCoord - posY);
					motionZ = (float)(object.hitVec.zCoord - posZ);
					f1 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
					posX -= motionX / f1 * 0.05000000074505806D;
					posY -= motionY / f1 * 0.05000000074505806D;
					posZ -= motionZ / f1 * 0.05000000074505806D;
					playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
					inGround = true;
					arrowShake = 7;
					setIsCritical(false);
					mop = object;

					if (inTile.getMaterial() != Material.air)
					{
						inTile.onEntityCollidedWithBlock(worldObj, xTile, yTile, zTile, this);
					}
				}
			}

			if (getIsCritical())
			{
				for (i = 0; i < 4; ++i)
				{
					worldObj.spawnParticle("crit", posX + motionX * i / 4.0D, posY + motionY * i / 4.0D, posZ + motionZ * i / 4.0D, -motionX, -motionY + 0.2D, -motionZ);
				}
			}

			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			f1 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

			for (rotationPitch = (float)(Math.atan2(motionY, f1) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
			{
				;
			}

			while (rotationPitch - prevRotationPitch >= 180.0F)
			{
				prevRotationPitch += 360.0F;
			}

			while (rotationYaw - prevRotationYaw < -180.0F)
			{
				prevRotationYaw -= 360.0F;
			}

			while (rotationYaw - prevRotationYaw >= 180.0F)
			{
				prevRotationYaw += 360.0F;
			}

			rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
			rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
			float f3 = 0.99F;
			f = 0.05F;

			if (isInWater())
			{
				for (i = 0; i < 4; ++i)
				{
					f2 = 0.25F;

					worldObj.spawnParticle("bubble", posX - motionX * f2, posY - motionY * f2, posZ - motionZ * f2, motionX, motionY, motionZ);
				}

				f3 = 0.8F;
			}

			if (isWet())
			{
				extinguish();
			}

			motionX *= f3;
			motionY *= f3;
			motionZ *= f3;
			motionY -= f;
			setPosition(posX, posY, posZ);
			func_145775_I();
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		nbt.setShort("xTile", (short)xTile);
		nbt.setShort("yTile", (short)yTile);
		nbt.setShort("zTile", (short)zTile);
		nbt.setString("inTile", GameData.getBlockRegistry().getNameForObject(inTile));
		nbt.setByte("inData", (byte)inData);
		nbt.setByte("shake", (byte)arrowShake);
		nbt.setByte("inGround", (byte)(inGround ? 1 : 0));
		nbt.setByte("pickup", (byte)canBePickedUp);
		nbt.setDouble("damage", damage);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		xTile = nbt.getShort("xTile");
		yTile = nbt.getShort("yTile");
		zTile = nbt.getShort("zTile");
		inTile = GameData.getBlockRegistry().getObject(nbt.getString("inTile"));
		inData = nbt.getByte("inData") & 255;
		arrowShake = nbt.getByte("shake") & 255;
		inGround = nbt.getByte("inGround") == 1;

		if (nbt.hasKey("damage", NBT.TAG_ANY_NUMERIC))
		{
			damage = nbt.getDouble("damage");
		}

		if (nbt.hasKey("pickup", NBT.TAG_ANY_NUMERIC))
		{
			canBePickedUp = nbt.getByte("pickup");
		}
		else if (nbt.hasKey("player", NBT.TAG_ANY_NUMERIC))
		{
			canBePickedUp = nbt.getBoolean("player") ? 1 : 0;
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player)
	{
		if (!worldObj.isRemote && inGround && arrowShake <= 0)
		{
			boolean pickup = canBePickedUp == 1 || canBePickedUp == 2 && player.capabilities.isCreativeMode;

			if (canBePickedUp == 1 && !addItemStackToInventory(player))
			{
				pickup = false;
			}

			if (pickup)
			{
				worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				player.onItemPickup(this, 1);
				setDead();
			}
		}
	}

	@Override
	public void setDamage(double value)
	{
		damage = value;
	}

	@Override
	public double getDamage()
	{
		return damage;
	}

	@Override
	public void setKnockbackStrength(int strength)
	{
		knockbackStrength = strength;
	}

	protected void onHit(Block block, int metadata)
	{
		if (block == inTile && metadata == inData)
		{
			++ticksInGround;

			if (tryPlaceBlock() || ticksInGround == 1200)
			{
				setDead();
			}
		}
		else
		{
			inGround = false;
			motionX *= rand.nextFloat() * 0.2F;
			motionY *= rand.nextFloat() * 0.2F;
			motionZ *= rand.nextFloat() * 0.2F;
			ticksInGround = 0;
			ticksInAir = 0;
		}
	}

	protected void onHit(Entity entity) {}

	protected boolean tryPlaceBlock()
	{
		return true;
	}

	protected boolean addItemStackToInventory(EntityPlayer player)
	{
		return true;
	}
}