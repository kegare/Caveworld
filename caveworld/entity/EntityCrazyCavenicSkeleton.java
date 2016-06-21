package caveworld.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import caveworld.api.CaveworldAPI;
import caveworld.client.particle.EntityUniversalChestFX;
import caveworld.core.CaveAchievementList;
import caveworld.item.CaveItems;
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.client.CaveAdjustMessage;
import caveworld.plugin.mceconomy.MCEconomyPlugin;
import caveworld.util.CaveUtils;
import caveworld.world.WorldProviderCavenia;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import shift.mceconomy2.api.MCEconomyAPI;

public class EntityCrazyCavenicSkeleton extends EntityMasterCavenicSkeleton implements ICrazyMob, Comparator<Attacker>
{
	public final Map<String, Attacker> attacker = Maps.newHashMap();

	private int confusionTime = 100;
	private int confusionStart = -1;
	private int specialTime = 1000;

	public EntityCrazyCavenicSkeleton(World world)
	{
		super(world);
		this.experienceValue = 10000;
		this.setSize(0.7F, 3.0F);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();

		dataWatcher.addObject(15, Byte.valueOf((byte)1));
	}

	@Override
	protected void initCustomValues()
	{
		aiArrowAttack = new EntityAIArrowAttack(this, 1.0D, 1, 2, 12.0F);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		applyEntityAttributes(1);
	}

	protected void applyEntityAttributes(int type)
	{
		double maxHealth = 5000.0D;
		double knockbackResistance = 5.0D;
		double movementSpeed = 0.29778D;

		switch (type)
		{
			case 1:
				maxHealth = 10000.0D;
				knockbackResistance = 6.5D;
				break;
			case 2:
				maxHealth = 20000.0D;
				knockbackResistance = 8.0D;
				break;
		}

		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealth);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(knockbackResistance);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(movementSpeed);
	}

	public Attacker getAttacker(EntityPlayer player)
	{
		String uuid = player.getUniqueID().toString();
		Attacker entry;

		if (attacker.containsKey(uuid))
		{
			entry = attacker.get(uuid);
		}
		else
		{
			entry = new Attacker(uuid);

			attacker.put(uuid, entry);
		}

		return entry;
	}

	public Collection<Attacker> getSortedAttacker()
	{
		List<Attacker> list = Lists.newArrayList(attacker.values());

		Collections.sort(list, this);

		return list;
	}

	public float getTotalDamage()
	{
		float total = 0.0F;

		for (Attacker entry : attacker.values())
		{
			total += entry.getDamage();
		}

		return total;
	}

	public int getAttackerOccupancy(Attacker attacker)
	{
		return MathHelper.clamp_int(MathHelper.ceiling_float_int(attacker.getDamage() / getTotalDamage() * 100), 0, 100);
	}

	@Override
	public int compare(Attacker o1, Attacker o2)
	{
		return Integer.compare(getAttackerOccupancy(o1), getAttackerOccupancy(o2));
	}

	@Override
	public IChatComponent func_145748_c_()
	{
		IChatComponent name = super.func_145748_c_();
		name.getChatStyle().setColor(EnumChatFormatting.DARK_PURPLE).setBold(true);

		return name;
	}

	@Override
	public int getCrazyType()
	{
		return dataWatcher.getWatchableObjectByte(15);
	}

	@Override
	public void setCrazyType(int type)
	{
		dataWatcher.updateObject(15, Byte.valueOf((byte)type));

		applyEntityAttributes(type);
	}

	@Override
	public void setSkeletonType(int type)
	{
		setSize(0.7F, 3.0F);
	}

	@Override
	protected void addRandomArmor()
	{
		setCurrentItemOrArmor(0, new ItemStack(CaveItems.cavenic_bow));
		setEquipmentDropChance(0, 2.0F);
	}

	protected boolean teleportRandomly()
	{
		double x = posX + (rand.nextDouble() - 0.5D) * 64.0D;
		double y = posY + (rand.nextInt(64) - 32);
		double z = posZ + (rand.nextDouble() - 0.5D) * 64.0D;

		return teleportTo(x, y, z);
	}

	protected boolean teleportToEntity(Entity entity)
	{
		Vec3 vec3 = Vec3.createVectorHelper(posX - entity.posX, boundingBox.minY + height / 2.0F - entity.posY + entity.getEyeHeight(), posZ - entity.posZ);
		vec3 = vec3.normalize();
		double d0 = 16.0D;
		double d1 = posX + (rand.nextDouble() - 0.5D) * 8.0D - vec3.xCoord * d0;
		double d2 = posY + (rand.nextInt(16) - 8) - vec3.yCoord * d0;
		double d3 = posZ + (rand.nextDouble() - 0.5D) * 8.0D - vec3.zCoord * d0;

		return teleportTo(d1, d2, d3);
	}

	protected boolean teleportTo(double targetX, double targetY, double targetZ)
	{
		double prevX = posX;
		double prevY = posY;
		double prevZ = posZ;
		posX = targetX;
		posY = targetY;
		posZ = targetZ;
		boolean flag = false;
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY);
		int z = MathHelper.floor_double(posZ);

		if (worldObj.blockExists(x, y, z))
		{
			boolean flag1 = false;

			while (!flag1 && y > 0)
			{
				Block block = worldObj.getBlock(x, y - 1, z);

				if (block.getMaterial().blocksMovement())
				{
					flag1 = true;
				}
				else
				{
					--posY;
					--y;
				}
			}

			if (flag1)
			{
				setPosition(posX, posY, posZ);

				if (worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox))
				{
					flag = true;
				}
			}
		}

		if (!flag)
		{
			setPosition(prevX, prevY, prevZ);

			return false;
		}
		else
		{
			short effect = 128;

			for (int i = 0; i < effect; ++i)
			{
				double d6 = i / (effect - 1.0D);
				float motionX = (rand.nextFloat() - 0.5F) * 0.2F;
				float motionY = (rand.nextFloat() - 0.5F) * 0.2F;
				float motionZ = (rand.nextFloat() - 0.5F) * 0.2F;
				double ptX = prevX + (posX - prevX) * d6 + (rand.nextDouble() - 0.5D) * width * 2.0D;
				double ptY = prevY + (posY - prevY) * d6 + rand.nextDouble() * height;
				double ptZ = prevZ + (posZ - prevZ) * d6 + (rand.nextDouble() - 0.5D) * width * 2.0D;

				worldObj.spawnParticle("portal", ptX, ptY, ptZ, motionX, motionY, motionZ);
			}

			worldObj.playSoundEffect(prevX, prevY, prevZ, "mob.endermen.portal", 1.0F, 0.5F);
			playSound("mob.endermen.portal", 1.0F, 0.5F);

			return true;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (worldObj.isRemote)
		{
			for (int i = 0; i < 3; ++i)
			{
				int var1 = rand.nextInt(2) * 2 - 1;
				int var2 = rand.nextInt(2) * 2 - 1;
				double ptX = posX + 0.25D * var1;
				double ptY = posY + 0.65D + rand.nextFloat();
				double ptZ = posZ + 0.25D * var2;
				double motionX = rand.nextFloat() * 1.0F * var1;
				double motionY = (rand.nextFloat() - 0.25D) * 0.125D;
				double motionZ = rand.nextFloat() * 1.0F * var2;
				EntityFX particle = new EntityUniversalChestFX(worldObj, ptX, ptY, ptZ, motionX, motionY, motionZ);

				FMLClientHandler.instance().getClient().effectRenderer.addEffect(particle);
			}
		}
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if (!worldObj.isRemote && getHealthScale() < 0.3F)
		{
			if (confusionTime > 0)
			{
				--confusionTime;
			}

			if (confusionTime == 0 && confusionStart == -1)
			{
				if (getCrazyType() <= 0)
				{
					confusionStart = 300;
				}
				else
				{
					confusionStart = 500;
				}
			}

			if (confusionStart > 0)
			{
				--confusionStart;

				List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(6.0D, 5.0D, 6.0D));
				Iterator iterator = list.iterator();

				while (iterator.hasNext())
				{
					Entity target = (Entity)iterator.next();

					if (target != null && target instanceof EntityPlayer && !target.isSprinting())
					{
						EntityArrow arrow = new EntityCavenicArrow(worldObj, target.posX + rand.nextDouble(), target.posY + target.getEyeHeight() * 3.0F + rand.nextDouble(), target.posZ + rand.nextDouble());

						arrow.shootingEntity = this;
						arrow.setDamage(3.0F + rand.nextGaussian() * 0.25D + worldObj.difficultySetting.getDifficultyId() * 0.12F);
						arrow.setThrowableHeading(0.0D, -1.0D, 0.0D, 1.0F, 1.0F);

						playSound("random.bow", 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
						worldObj.spawnEntityInWorld(arrow);

						if (rand.nextInt(3) == 0)
						{
							List targets = worldObj.getEntitiesWithinAABBExcludingEntity(this, arrow.boundingBox.expand(1.5D, 1.5D, 1.5D));
							Iterator targetIterator = targets.iterator();

							while (targetIterator.hasNext())
							{
								Entity arrowTarget = (Entity)targetIterator.next();

								if (arrowTarget != null && arrowTarget instanceof EntityPlayer)
								{
									arrowTarget.attackEntityFrom(DamageSource.causeArrowDamage(arrow, this), (float)arrow.getDamage());
								}
							}
						}
					}
				}
			}

			if (confusionTime == 0 && confusionStart == 0)
			{
				if (getCrazyType() <= 0)
				{
					confusionTime = 500;
				}
				else
				{
					confusionTime = 100;
				}

				confusionStart = -1;
			}

			if (specialTime > 0)
			{
				--specialTime;
			}

			if (specialTime == 0)
			{
				if (rand.nextInt(2) == 0)
				{
					worldObj.newExplosion(this, posX, posY + getEyeHeight(), posZ, 1.75F + rand.nextFloat(), false, true);
				}
				else
				{
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					double currentX = posX;
					double currentY = posY;
					double currentZ = posZ;
					float yaw = rotationYaw;
					float pitch = rotationPitch;

					for (Attacker entry : getSortedAttacker())
					{
						EntityPlayerMP player = server.getConfigurationManager().func_152612_a(entry.getName());

						if (player != null && player.dimension == dimension)
						{
							setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);

							CaveUtils.setPlayerLocation(player, currentX, currentY, currentZ, yaw, pitch);

							break;
						}
					}
				}

				specialTime = 1000;
			}
		}
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase entity, float power)
	{
		EntityArrow arrow = new EntityCavenicArrow(worldObj, this, entity, 2.0F, 1.0F);
		int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, getHeldItem());
		int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, getHeldItem());
		arrow.setDamage(power * 3.0F + rand.nextGaussian() * 0.25D + worldObj.difficultySetting.getDifficultyId() * 0.12F);

		if (i > 0)
		{
			arrow.setDamage(arrow.getDamage() + i * 0.5D + 3.0D);
		}

		arrow.setKnockbackStrength(j + 3 + getCrazyType());

		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, getHeldItem()) > 0)
		{
			arrow.setFire(100);
		}

		playSound("random.bow", 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
		worldObj.spawnEntityInWorld(arrow);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage)
	{
		if (isEntityInvulnerable())
		{
			return false;
		}

		if (source == DamageSource.inWall || source == DamageSource.fallingBlock)
		{
			for (int i = 0; i < 64; ++i)
			{
				if (teleportRandomly())
				{
					return true;
				}
			}
		}

		if (getCrazyType() > 0 && damage > 50.0F)
		{
			damage = 50.0F;
		}

		boolean result = super.attackEntityFrom(source, damage);

		if (result)
		{
			Entity entity = source.getEntity();

			if (entity == null)
			{
				entity = source.getSourceOfDamage();
			}

			if (entity != null && entity instanceof EntityArrow)
			{
				entity = ((EntityArrow)entity).shootingEntity;
			}

			if (entity != null && entity instanceof EntityCavenicSkeleton)
			{
				return false;
			}

			if (entity != null && entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)entity;
				Attacker entry = getAttacker(player);

				entry.setName(player.getCommandSenderName());
				entry.addDamage(damage);
			}

			float scale = getHealthScale();

			if (!worldObj.isRemote && rand.nextInt(scale < 0.2F ? 50 : scale < 0.5F ? 70 : 100) == 0)
			{
				List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(64.0D, 12.0D, 64.0D));
				Iterator iterator = list.iterator();

				while (iterator.hasNext())
				{
					Entity target = (Entity)iterator.next();

					if (target != null && (target instanceof EntityItem || target instanceof EntityLivingBase))
					{
						if (scale < 0.3F && target instanceof EntityPlayer || rand.nextInt(10) == 0)
						{
							double posX = target.posX + rand.nextDouble();
							double posY = target.posY + rand.nextDouble();
							double posZ = target.posZ + rand.nextDouble();

							if (scale < 0.75F && rand.nextInt(3) == 0)
							{
								worldObj.newExplosion(this, posX, posY, posZ, 1.5F, true, true);
							}
							else
							{
								EntityLightningBolt thunder = new EntityLightningBolt(worldObj, posX, posY, posZ);

								worldObj.addWeatherEffect(thunder);
							}
						}
					}
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public void onDeath(DamageSource source)
	{
		super.onDeath(source);

		Entity entity = source.getEntity();

		if (entity == null)
		{
			entity = source.getSourceOfDamage();
		}

		if (entity != null && entity instanceof EntityPlayer)
		{
			((EntityPlayer)entity).triggerAchievement(CaveAchievementList.crazyCavenicSkeletonSlayer);
		}

		if (!worldObj.isRemote)
		{
			worldObj.newExplosion(this, posX, posY + getEyeHeight(), posZ, 10.0F, false, true);

			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			List<String> names = Lists.newArrayList();
			List<IChatComponent> reports = Lists.newArrayList();

			for (Attacker entry : getSortedAttacker())
			{
				IChatComponent component = new ChatComponentTranslation("caveworld.message.crazy.report", entry.getName(), getAttackerOccupancy(entry) + "%");
				component.getChatStyle().setColor(EnumChatFormatting.GRAY);

				names.add(entry.getName());
				reports.add(component);
			}

			IChatComponent bossName = func_145748_c_();

			server.getConfigurationManager().sendChatMsg(new ChatComponentTranslation("caveworld.message.crazy.kill", bossName));
			server.getConfigurationManager().sendChatMsg(new ChatComponentText(" ").appendSibling(new ChatComponentTranslation("caveworld.message.crazy.member", Joiner.on(", ").join(names))));
			server.getConfigurationManager().sendChatMsg(new ChatComponentTranslation("caveworld.message.crazy.result", bossName));

			for (IChatComponent component : reports)
			{
				server.getConfigurationManager().sendChatMsg(new ChatComponentText(" ").appendSibling(component));
			}

			int i = 0;

			for (Attacker entry : getSortedAttacker())
			{
				++i;

				EntityPlayerMP player = server.getConfigurationManager().func_152612_a(entry.getName());

				if (player != null && entry.getDamage() > 0.0F)
				{
					int amount = 5000;

					if (i <= 1)
					{
						amount = 10000;
					}
					else if (i == 2)
					{
						amount = 8000;
					}

					switch (getCrazyType())
					{
						case 0:
							amount /= 3;
							break;
						case 1:
							amount /= 2;
							break;
					}

					player.addExperience(amount / 5);

					if (MCEconomyPlugin.enabled())
					{
						MCEconomyAPI.addPlayerMP(player, amount, false);
					}
				}
			}

			for (i = 0; i < 10; ++i)
			{
				entityDropItem(new ItemStack(CaveItems.cavenium, 64, 1), rand.nextFloat() + 0.1F);
			}

			entityDropItem(new ItemStack(Blocks.stonebrick, 14, 1), rand.nextFloat() + 0.1F);

			WorldProviderCavenia.saveHandler.setBossAlive(false);

			CaveNetworkRegistry.sendToAll(new CaveAdjustMessage(WorldProviderCavenia.TYPE, WorldProviderCavenia.saveHandler));
		}
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	public boolean getCanSpawnHere()
	{
		return CaveworldAPI.isEntityInCavenia(this);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);

		if (nbt.hasKey("Type", NBT.TAG_ANY_NUMERIC))
		{
			setCrazyType(nbt.getByte("Type"));
		}

		if (nbt.hasKey("Attacker"))
		{
			NBTTagList list = nbt.getTagList("Attacker", NBT.TAG_COMPOUND);

			attacker.clear();

			for (int i = 0; i < list.tagCount(); ++i)
			{
				NBTTagCompound data = list.getCompoundTagAt(i);
				Attacker entry = new Attacker(data);

				attacker.put(entry.getUniqueID(), entry);
			}
		}

		if (nbt.hasKey("ConfusionTime", NBT.TAG_ANY_NUMERIC))
		{
			confusionTime = nbt.getInteger("ConfusionTime");
		}

		if (nbt.hasKey("ConfusionStart", NBT.TAG_ANY_NUMERIC))
		{
			confusionStart = nbt.getInteger("ConfusionStart");
		}

		if (nbt.hasKey("SpecialTime", NBT.TAG_ANY_NUMERIC))
		{
			specialTime = nbt.getInteger("SpecialTime");
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);

		nbt.setByte("Type", (byte)getCrazyType());

		if (!attacker.isEmpty())
		{
			NBTTagList list = new NBTTagList();

			for (Attacker entry : attacker.values())
			{
				list.appendTag(entry.getNBTData());
			}

			nbt.setTag("Attacker", list);
		}

		nbt.setInteger("ConfusionTime", confusionTime);
		nbt.setInteger("ConfusionStart", confusionStart);
		nbt.setInteger("SpecialTime", specialTime);
	}
}