package caveworld.entity;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Sets;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveMob;
import caveworld.entity.ai.EntityAICollector;
import caveworld.entity.ai.EntityAIFleeSun2;
import caveworld.entity.ai.EntityAISoldier;
import caveworld.item.CaveItems;
import caveworld.item.ItemCavenium;
import caveworld.util.CaveUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.Constants.NBT;

public class EntityCaveman extends EntityCreature implements IMob, ICaveMob, IInventory
{
	public static int spawnWeight;
	public static int spawnMinHeight;
	public static int spawnMaxHeight;
	public static int spawnInChunks;
	public static int[] spawnBiomes;
	public static boolean despawn;

	public static void refreshSpawn()
	{
		BiomeGenBase[] def = CaveUtils.getBiomes().toArray(new BiomeGenBase[0]);
		BiomeGenBase[] biomes = new BiomeGenBase[0];
		BiomeGenBase biome;

		for (int i : spawnBiomes)
		{
			if (i >= 0 && i < BiomeGenBase.getBiomeGenArray().length)
			{
				biome = BiomeGenBase.getBiome(i);

				if (biome != null)
				{
					biomes = ArrayUtils.add(biomes, biome);
				}
			}
		}

		if (ArrayUtils.isEmpty(biomes))
		{
			biomes = def;
		}

		CaveEntityRegistry.removeSpawn(EntityCaveman.class, def);

		if (spawnWeight > 0)
		{
			CaveEntityRegistry.addSpawn(EntityCaveman.class, spawnWeight, 3, 3, biomes);
		}
	}

	private final ItemStack[] inventoryContents = new ItemStack[getSizeInventory()];

	private long stoppedTime;

	private EntityAIBase aiSoldier = new EntityAISoldier(this);
	private EntityAIBase aiCollecter = new EntityAICollector(this, 1.0D, 20.0F);

	public boolean isCollecting;
	public boolean inventoryFull;

	public EntityCaveman(World world)
	{
		super(world);
		this.experienceValue = 5;
		this.setSize(0.45F, 1.75F);
		this.getNavigator().setAvoidSun(true);
		this.getNavigator().setCanSwim(true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(3, new EntityAIRestrictSun(this));
		this.tasks.addTask(4, new EntityAIFleeSun2(this, 1.25D));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
		this.tasks.addTask(9, new EntityAILookIdle(this));

		if (world != null && !world.isRemote)
		{
			setCustomTask();
		}
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();

		dataWatcher.addObject(13, Byte.valueOf((byte)0));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);

		applyEntityAttributes(0);
	}

	protected void applyEntityAttributes(int type)
	{
		double maxHealth = 30.0D;
		double knockbackResistance = 0.5D;
		double movementSpeed = 0.325D;

		switch (type)
		{
			case 1:
				maxHealth = 100.0D;
				knockbackResistance = 0.75D;
				break;
			case 2:
				maxHealth = 500.0D;
				knockbackResistance = 1.0D;
				break;
			case 3:
				maxHealth = 10.0D;
		}

		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealth);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(knockbackResistance);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(movementSpeed);
	}

	@Override
	public boolean isAIEnabled()
	{
		return true;
	}

	@Override
	public float getEyeHeight()
	{
		if (isStopped())
		{
			return 1.0F;
		}

		return 1.55F;
	}

	public int getCavemanType()
	{
		return dataWatcher.getWatchableObjectByte(13);
	}

	public void setCavemanType(int type)
	{
		dataWatcher.updateObject(13, Byte.valueOf((byte)type));

		applyEntityAttributes(type);
	}

	public void setCustomTask()
	{
		tasks.removeTask(aiSoldier);
		tasks.removeTask(aiCollecter);

		ItemStack item = getHeldItem();

		if (item != null && (item.getItem() instanceof ItemSword || item.getItem() instanceof ItemTool))
		{
			tasks.addTask(5, aiSoldier);
		}
		else
		{
			tasks.addTask(6, aiCollecter);
		}
	}

	public long getStoppedTime()
	{
		return stoppedTime;
	}

	public boolean isStopped()
	{
		return onGround && getStoppedTime() > 500L;
	}

	@Override
	protected String getSwimSound()
	{
		return "game.hostile.swim";
	}

	@Override
	protected String getSplashSound()
	{
		return "game.hostile.swim.splash";
	}

	@Override
	protected String getHurtSound()
	{
		return "game.hostile.hurt";
	}

	@Override
	protected String getDeathSound()
	{
		return "game.hostile.die";
	}

	@Override
	protected String func_146067_o(int value)
	{
		return value > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data)
	{
		data = super.onSpawnWithEgg(data);

		if (rand.nextInt(3) == 0)
		{
			setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
		}
		else if (rand.nextInt(4) == 0)
		{
			setCurrentItemOrArmor(0, new ItemStack(Items.stone_shovel));
		}
		else if (rand.nextInt(3) == 0)
		{
			setCurrentItemOrArmor(0, new ItemStack(Items.stone_pickaxe));
		}
		else if (rand.nextInt(3) == 0)
		{
			setCurrentItemOrArmor(0, new ItemStack(Blocks.torch));
		}
		else if (rand.nextInt(3) == 0)
		{
			setCurrentItemOrArmor(0, new ItemStack(Blocks.stone));
		}
		else if (rand.nextInt(3) == 0)
		{
			setCurrentItemOrArmor(0, new ItemStack(Blocks.cobblestone));
		}

		Set<ItemStack> items = Sets.newHashSet();

		for (int i = 0; i < rand.nextInt(3) + 1; ++i)
		{
			items.add(new ItemStack(Items.stone_pickaxe, 1, rand.nextInt(ToolMaterial.STONE.getMaxUses())));
		}

		items.add(new ItemStack(CaveItems.cavenium, 1));
		items.add(new ItemStack(Blocks.torch, MathHelper.getRandomIntegerInRange(rand, 16, 32)));
		items.add(new ItemStack(Items.bread, MathHelper.getRandomIntegerInRange(rand, 4, 16)));

		int slot = 0;

		for (ItemStack itemstack : items)
		{
			if (itemstack.stackSize > 0 && slot <= getSizeInventory())
			{
				setInventorySlotContents(slot++, itemstack);
			}
		}

		return data;
	}

	@Override
	public void setCurrentItemOrArmor(int slot, ItemStack itemstack)
	{
		super.setCurrentItemOrArmor(slot, itemstack);

		if (!worldObj.isRemote && slot == 0)
		{
			setCustomTask();
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (worldObj.isRemote && dataWatcher.hasChanges())
		{
			dataWatcher.func_111144_e();
		}
	}

	@Override
	public void onLivingUpdate()
	{
		updateArmSwingProgress();

		super.onLivingUpdate();

		if (isStopped())
		{
			setSize(0.45F, 1.2F);

			if (ticksExisted % 20 == 0)
			{
				setHealth(getHealth() + 0.5F);
			}
		}
		else
		{
			setSize(0.45F, 1.75F);
		}

		if (motionX == 0.0D && motionZ == 0.0D)
		{
			++stoppedTime;
		}
		else
		{
			stoppedTime = 0;
		}

		if (!worldObj.isRemote && isEntityAlive())
		{
			List<EntityItem> list = worldObj.getEntitiesWithinAABB(EntityItem.class, boundingBox.expand(1.0D, 0.5D, 1.0D));

			for (EntityItem item : list)
			{
				if (item != null && item.isEntityAlive())
				{
					ItemStack itemstack = item.getEntityItem();

					if (itemstack != null)
					{
						onItemPickup(item, itemstack.stackSize);
					}
				}
			}
		}

		if (!worldObj.isRemote && inventoryFull && ticksExisted % 200 == 0 && CaveUtils.getFirstEmptySlot(this) >= 0)
		{
			inventoryFull = false;
		}
	}

	@Override
	public void onItemPickup(Entity entity, int stackSize)
	{
		if (stackSize > 0 && entity.isEntityAlive() && entity instanceof EntityItem)
		{
			EntityItem item = (EntityItem)entity;

			if (CaveUtils.addItemStackToInventory(this, item.getEntityItem()))
			{
				item.setDead();

				if (!worldObj.isRemote)
				{
					worldObj.setEntityState(this, (byte)19);
				}
			}
			else if (!worldObj.isRemote)
			{
				inventoryFull = true;
			}
		}
	}

	@Override
	public void onDeath(DamageSource source)
	{
		super.onDeath(source);

		if (!worldObj.isRemote)
		{
			for (int i = 0; i < getSizeInventory(); ++i)
			{
				ItemStack item = getStackInSlotOnClosing(i);

				if (item != null && item.getItem() != null && rand.nextInt(3) == 0)
				{
					entityDropItem(item, 0.05F);

					item.animationsToGo = 5;
				}
			}
		}
	}

	@Override
	protected void attackEntity(Entity entity, float distance)
	{
		if (attackTime <= 0 && distance < 2.0F && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY)
		{
			attackTime = 20;

			attackEntityAsMob(entity);
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		float damage = (float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
		int i = 0;

		if (entity instanceof EntityLivingBase)
		{
			damage += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)entity);
			i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)entity);
		}

		if (entity.attackEntityFrom(DamageSource.causeMobDamage(this), damage))
		{
			if (i > 0)
			{
				entity.addVelocity(-MathHelper.sin(rotationYaw * (float)Math.PI / 180.0F) * i * 0.5F, 0.1D, MathHelper.cos(rotationYaw * (float)Math.PI / 180.0F) * i * 0.5F);

				motionX *= 0.6D;
				motionZ *= 0.6D;
			}

			int j = EnchantmentHelper.getFireAspectModifier(this);

			if (j > 0)
			{
				entity.setFire(j * 4);
			}

			if (entity instanceof EntityLivingBase)
			{
				EnchantmentHelper.func_151384_a((EntityLivingBase)entity, this);
			}

			EnchantmentHelper.func_151385_b(this, entity);

			return true;
		}

		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage)
	{
		if (!source.isProjectile() && !source.isMagicDamage())
		{
			Entity entity = source.getEntity();

			if (entity == null)
			{
				entity = source.getSourceOfDamage();
			}

			if (entity != null)
			{
				if (entity instanceof EntityLivingBase)
				{
					ItemStack itemstack = ((EntityLivingBase)entity).getHeldItem();

					if (itemstack == null)
					{
						return super.attackEntityFrom(source, damage * 0.5F);
					}

					if (CaveUtils.isItemPickaxe(itemstack))
					{
						return super.attackEntityFrom(source, damage * 2.0F);
					}
				}
			}
		}

		return !source.isFireDamage() && source != DamageSource.fall && super.attackEntityFrom(source, damage);
	}

	@Override
	public boolean interact(EntityPlayer player)
	{
		if (getCavemanType() > 0)
		{
			if (!worldObj.isRemote)
			{
				player.displayGUIChest(this);
			}

			return true;
		}

		ItemStack itemstack = player.inventory.getCurrentItem();

		if (itemstack != null && itemstack.getItem() instanceof ItemCavenium)
		{
			if (!worldObj.isRemote)
			{
				player.displayGUIChest(this);
			}

			if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0)
			{
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			}

			return true;
		}

		return super.interact(player);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);

		nbt.setByte("Type", (byte)getCavemanType());

		NBTTagList list = new NBTTagList();

		for (int slot = 0; slot < getSizeInventory(); ++slot)
		{
			ItemStack itemstack = getStackInSlot(slot);

			if (itemstack != null)
			{
				NBTTagCompound nbttag = new NBTTagCompound();
				nbttag.setByte("Slot", (byte)slot);
				itemstack.writeToNBT(nbttag);
				list.appendTag(nbttag);
			}
		}

		nbt.setTag("Items", list);
		nbt.setLong("Stopped", stoppedTime);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);

		if (nbt.hasKey("Type", NBT.TAG_ANY_NUMERIC))
		{
			setCavemanType(nbt.getByte("Type"));
		}

		if (nbt.hasKey("Items", NBT.TAG_COMPOUND))
		{
			NBTTagList list = nbt.getTagList("Items", NBT.TAG_COMPOUND);

			for (int slot = 0; slot < getSizeInventory(); ++slot)
			{
				setInventorySlotContents(slot, null);
			}

			for (int i = 0; i < list.tagCount(); ++i)
			{
				NBTTagCompound nbttag = list.getCompoundTagAt(i);
				int slot = nbttag.getByte("Slot") & 255;

				if (slot >= 0 && slot < getSizeInventory())
				{
					setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(nbttag));
				}
			}
		}

		if (nbt.hasKey("Stopped", NBT.TAG_ANY_NUMERIC))
		{
			stoppedTime = nbt.getLong("Stopped");
		}

		setCustomTask();
	}

	@Override
	public float getBlockPathWeight(int x, int y, int z)
	{
		return 0.5F - worldObj.getLightBrightness(x, y, z);
	}

	@Override
	protected boolean func_146066_aG()
	{
		return true;
	}

	@Override
	protected boolean canDespawn()
	{
		return despawn;
	}

	public boolean isValidHeight()
	{
		int y = MathHelper.floor_double(boundingBox.minY);

		return y >= spawnMinHeight && y <= spawnMaxHeight;
	}

	protected boolean isValidLightLevel()
	{
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(boundingBox.minY);
		int z = MathHelper.floor_double(posZ);

		if (worldObj.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) > rand.nextInt(32))
		{
			return false;
		}
		else
		{
			int light = worldObj.getBlockLightValue(x, y, z);

			if (worldObj.isThundering())
			{
				int prev = worldObj.skylightSubtracted;

				worldObj.skylightSubtracted = 10;

				light = worldObj.getBlockLightValue(x, y, z);

				worldObj.skylightSubtracted = prev;
			}

			return light <= rand.nextInt(8);
		}
	}

	@Override
	public boolean getCanSpawnHere()
	{
		return CaveworldAPI.isEntityInCaves(this) && isValidHeight() && isValidLightLevel() && super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return spawnInChunks;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleHealthUpdate(byte id)
	{
		switch (id)
		{
			case 19:
				for (int i = 0; i < 3; ++i)
				{
					double d0 = rand.nextGaussian() * 0.02D;
					double d1 = rand.nextGaussian() * 0.02D;
					double d2 = rand.nextGaussian() * 0.02D;

					worldObj.spawnParticle("note", posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, d0, d1, d2);
				}

				break;
			default:
				super.handleHealthUpdate(id);
		}
	}

	@Override
	public String getInventoryName()
	{
		String name = StatCollector.translateToLocal("entity." + getEntityString() + ".name");

		if (hasCustomNameTag())
		{
			name += ": "  + getCustomNameTag();
		}

		return name;
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return true;
	}

	@Override
	public int getSizeInventory()
	{
		return 9 * 4;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return slot >= 0 && slot < inventoryContents.length ? inventoryContents[slot] : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int stack)
	{
		if (getStackInSlot(slot) != null)
		{
			ItemStack itemstack;

			if (getStackInSlot(slot).stackSize <= stack)
			{
				itemstack = getStackInSlot(slot);
				setInventorySlotContents(slot, null);

				return itemstack;
			}

			itemstack = getStackInSlot(slot).splitStack(stack);

			if (getStackInSlot(slot).stackSize == 0)
			{
				setInventorySlotContents(slot, null);
			}

			return itemstack;
		}

		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if (getStackInSlot(slot) != null)
		{
			ItemStack itemstack = getStackInSlot(slot);
			setInventorySlotContents(slot, null);

			return itemstack;
		}

		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		ItemStack content = ItemStack.copyItemStack(itemstack);

		inventoryContents[slot] = content;

		if (content != null)
		{
			inventoryContents[slot].animationsToGo = 5;
			itemstack.stackSize = 0;

			if (content.stackSize > getInventoryStackLimit())
			{
				content.stackSize = getInventoryStackLimit();
			}
		}
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return player.getDistanceToEntity(this) <= 10.0F;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}
}