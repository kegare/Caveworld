/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.google.common.collect.Sets;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.core.CaveAchievementList;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.entity.ai.EntityAICollector;
import com.kegare.caveworld.entity.ai.EntityAIFleeEntityLiving;
import com.kegare.caveworld.entity.ai.EntityAIFleeSun2;
import com.kegare.caveworld.entity.ai.EntityAISoldier;
import com.kegare.caveworld.item.ItemCavenium;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.comparator.InventoryComparator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityCaveman extends EntityTameable implements IInventory
{
	private final ItemStack[] inventoryContents = new ItemStack[getSizeInventory()];

	private long stoppedTime;
	private boolean needsSort;

	public boolean inventoryFull;

	public static final IEntitySelector fleeEntitySelector = new IEntitySelector()
	{
		@Override
		public boolean isEntityApplicable(Entity entity)
		{
			if (entity == null || entity.isDead)
			{
				return false;
			}

			if (entity instanceof EntityLivingBase)
			{
				EntityLivingBase target = (EntityLivingBase)entity;

				return CaveUtils.isItemPickaxe(target.getHeldItem());
			}

			return false;
		}
	};

	public EntityCaveman(World world)
	{
		super(world);
		this.setSize(0.45F, 1.75F);
		this.setTamed(false);
		this.setSitting(false);
		this.getNavigator().setAvoidSun(true);
		this.getNavigator().setCanSwim(true);
		this.stepHeight = 1.0F;
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIRestrictSun(this));
		this.tasks.addTask(2, new EntityAIFleeSun2(this, 1.25D));
		this.tasks.addTask(3, new EntityAIFleeEntityLiving(this, fleeEntitySelector, 1.0D));
		this.tasks.addTask(4, new EntityAISoldier(this));
		this.tasks.addTask(5, new EntityAICollector(this, 1.0D, 3.0F, 20.0F));
		this.tasks.addTask(6, new EntityAIWander(this, 0.5D)
		{
			@Override
			public boolean shouldExecute()
			{
				return !isSitting() && getStoppedTime() > 200L && super.shouldExecute();
			}
		});
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.35D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.399999988079071D);
	}

	@Override
	public boolean isAIEnabled()
	{
		return true;
	}

	@Override
	public boolean isBreedingItem(ItemStack itemstack)
	{
		return itemstack != null && itemstack.getItem() != null && itemstack.getItem() instanceof ItemCavenium;
	}

	@Override
	public boolean isChild()
	{
		return false;
	}

	@Override
	public float getEyeHeight()
	{
		if (isSittingAndStopped())
		{
			return 1.0F;
		}

		return 1.55F;
	}

	public long getStoppedTime()
	{
		return stoppedTime;
	}

	public boolean isSittingAndStopped()
	{
		return isSitting() && getStoppedTime() > 5L;
	}

	public String getLocalizedName()
	{
		return StatCollector.translateToLocal("entity." + getEntityString() + ".name");
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data)
	{
		data = super.onSpawnWithEgg(data);

		if (!isTamed())
		{
			Set<ItemStack> items = Sets.newHashSet();

			for (int i = 0; i < rand.nextInt(2) + 1; ++i)
			{
				if (rand.nextInt(3) == 0)
				{
					items.add(new ItemStack(Items.stone_pickaxe));
				}
				else
				{
					items.add(new ItemStack(Items.iron_pickaxe));
				}
			}

			items.add(new ItemStack(Items.coal, MathHelper.getRandomIntegerInRange(rand, 16, 48)));
			items.add(new ItemStack(Items.iron_ingot, MathHelper.getRandomIntegerInRange(rand, 4, 10)));
			items.add(new ItemStack(Items.gold_ingot, MathHelper.getRandomIntegerInRange(rand, 1, 3)));
			items.add(new ItemStack(Items.emerald, MathHelper.getRandomIntegerInRange(rand, 2, 5)));

			if (rand.nextInt(10) == 0)
			{
				items.add(new ItemStack(Items.diamond));
			}

			items.add(new ItemStack(Blocks.torch, MathHelper.getRandomIntegerInRange(rand, 16, 32)));
			items.add(new ItemStack(Items.bread, MathHelper.getRandomIntegerInRange(rand, 4, 16)));

			if (Config.rope)
			{
				items.add(new ItemStack(CaveBlocks.rope, MathHelper.getRandomIntegerInRange(rand, 8, 24)));
			}

			int slot = 0;

			for (ItemStack itemstack : items)
			{
				if (itemstack.stackSize > 0 && slot <= getSizeInventory())
				{
					setInventorySlotContents(slot++, itemstack);
				}
			}

			if (Config.cavemanCreatureType > 0 && rand.nextInt(5) == 0)
			{
				setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
			}
			else if (rand.nextInt(6) == 0)
			{
				setCurrentItemOrArmor(0, new ItemStack(Items.stone_pickaxe));
			}
			else if (rand.nextInt(5) == 0)
			{
				setCurrentItemOrArmor(0, new ItemStack(Items.stone_shovel));
			}
		}

		return data;
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if (isSittingAndStopped())
		{
			setSize(0.45F, 1.2F);
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

		if (isSitting() && stoppedTime > 0 && stoppedTime % 100 == 0)
		{
			heal(0.5F + rand.nextFloat() / 2);
		}

		if (!worldObj.isRemote && isEntityAlive())
		{
			List<EntityItem> list = worldObj.getEntitiesWithinAABB(EntityItem.class, boundingBox.expand(1.0D, 0.5D, 1.0D));

			for (EntityItem item : list)
			{
				onItemPickup(item, item.getEntityItem().stackSize);
			}

			if (isTamed() && !getLeashed())
			{
				EntityLivingBase owner = getOwner();

				if (owner != null && owner.isSwingInProgress && isBreedingItem(owner.getHeldItem()))
				{
					int x = MathHelper.floor_double(owner.posX) - 2;
					int y = MathHelper.floor_double(owner.boundingBox.minY);
					int z = MathHelper.floor_double(owner.posZ) - 2;
					boolean flag = false;

					for (int i = 0; !flag && i <= 4; ++i)
					{
						for (int j = 0; !flag && j <= 4; ++j)
						{
							if ((i < 1 || j < 1 || i > 3 || j > 3) && World.doesBlockHaveSolidTopSurface(worldObj, x + i, y - 1, z + j) &&
								!worldObj.getBlock(x + i, y, z + j).isNormalCube() && !worldObj.getBlock(x + i, y + 1, z + j).isNormalCube())
							{
								setLocationAndAngles(x + i + 0.5F, y, z + j + 0.5F, rotationYaw, rotationPitch);

								flag = true;
							}
						}
					}
				}
			}
		}

		if (needsSort)
		{
			sort();
		}

		if (!worldObj.isRemote && inventoryFull && ticksExisted % 200 == 0 && getFirstEmptySlot() >= 0)
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

			if (addItemStackToInventory(item.getEntityItem()))
			{
				item.setDead();

				if (!worldObj.isRemote)
				{
					worldObj.setEntityState(this, (byte)19);
				}
			}
			else
			{
				if (!worldObj.isRemote)
				{
					inventoryFull = true;
				}
			}
		}
	}

	@Override
	public void onDeath(DamageSource source)
	{
		super.onDeath(source);

		if (!worldObj.isRemote)
		{
			for (ItemStack itemstack : inventoryContents)
			{
				ItemStack content = ItemStack.copyItemStack(itemstack);

				if (content != null && content.getItem() != null && content.stackSize > 0)
				{
					entityDropItem(content, 0.0F);

					content.animationsToGo = 5;
					itemstack.stackSize = 0;
				}
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage)
	{
		Entity entity = source.getSourceOfDamage();

		if (entity != null)
		{
			if (entity instanceof EntityPlayer && isTamed() && func_152113_b().equals(((EntityPlayer)entity).getGameProfile().getId().toString()) && ((EntityPlayer)entity).isSneaking())
			{
				setSitting(!isSitting());

				return false;
			}

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

		if (source.isFireDamage())
		{
			return false;
		}

		return super.attackEntityFrom(source, damage);
	}

	@Override
	public boolean interact(EntityPlayer player)
	{
		ItemStack itemstack = player.inventory.getCurrentItem();

		if (isTamed())
		{
			if (itemstack != null && itemstack.getItem() == Items.name_tag)
			{
				return super.interact(player);
			}

			if (player.isSneaking())
			{
				player.setCurrentItemOrArmor(0, getEquipmentInSlot(0));

				setCurrentItemOrArmor(0, itemstack);
			}
			else if (!worldObj.isRemote && func_152113_b().equals(player.getGameProfile().getId().toString()))
			{
				player.displayGUIChest(this);
			}

			return true;
		}
		else if (isBreedingItem(itemstack))
		{
			if (!player.capabilities.isCreativeMode && itemstack.stackSize <= --itemstack.stackSize)
			{
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			}

			if (!worldObj.isRemote)
			{
				int point = CaveworldAPI.getMiningPoint(player);

				if (point >= 1000)
				{
					if (worldObj.rand.nextInt(5) == 0)
					{
						setTamed(true);
						setPathToEntity(null);
						setAttackTarget(null);
						func_152115_b(player.getGameProfile().getId().toString());
						playTameEffect(true);
						worldObj.setEntityState(this, (byte)7);

						CaveworldAPI.addMiningPoint(player, -1000);

						player.triggerAchievement(CaveAchievementList.compCaving);
					}
					else
					{
						playTameEffect(false);

						worldObj.setEntityState(this, (byte)6);
					}
				}
			}

			return true;
		}

		return super.interact(player);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entity)
	{
		return null;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);

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
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);

		if (nbt.hasKey("Items"))
		{
			NBTTagList list = (NBTTagList)nbt.getTag("Items");

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
	}

	@Override
	protected boolean canDespawn()
	{
		return !getLeashed() && !isTamed();
	}

	@Override
	public boolean getCanSpawnHere()
	{
		int y = MathHelper.floor_double(boundingBox.minY);

		return CaveworldAPI.isEntityInCaveworld(this) && y >= Config.cavemanSpawnMinHeight && y <= Config.cavemanSpawnMaxHeight &&
			worldObj.getEntitiesWithinAABB(getClass(), boundingBox.expand(64.0D, 64.0D, 64.0D)).isEmpty() &&
			worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return Config.cavemanSpawnInChunks;
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
					double d0 = this.rand.nextGaussian() * 0.02D;
					double d1 = this.rand.nextGaussian() * 0.02D;
					double d2 = this.rand.nextGaussian() * 0.02D;

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
		String name = getLocalizedName();

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
		return 9 * 5;
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

	private int storeItemStack(ItemStack itemstack)
	{
		for (int i = 0; i < inventoryContents.length; ++i)
		{
			if (inventoryContents[i] != null && inventoryContents[i].getItem() == itemstack.getItem() &&
				inventoryContents[i].isStackable() && inventoryContents[i].stackSize < inventoryContents[i].getMaxStackSize() &&
				inventoryContents[i].stackSize < getInventoryStackLimit() && (!inventoryContents[i].getHasSubtypes() || inventoryContents[i].getItemDamage() == itemstack.getItemDamage()) &&
				ItemStack.areItemStackTagsEqual(inventoryContents[i], itemstack))
			{
				return i;
			}
		}

		return -1;
	}

	public int getFirstEmptySlot()
	{
		for (int i = 0; i < inventoryContents.length; ++i)
		{
			if (inventoryContents[i] == null)
			{
				return i;
			}
		}

		return -1;
	}

	private int storePartialItemStack(ItemStack itemstack)
	{
		Item item = itemstack.getItem();
		int i = itemstack.stackSize;
		int j;

		if (itemstack.getMaxStackSize() == 1)
		{
			j = getFirstEmptySlot();

			if (j < 0)
			{
				return i;
			}

			if (inventoryContents[j] == null)
			{
				inventoryContents[j] = ItemStack.copyItemStack(itemstack);
			}

			return 0;
		}

		j = storeItemStack(itemstack);

		if (j < 0)
		{
			j = getFirstEmptySlot();
		}

		if (j < 0)
		{
			return i;
		}

		if (inventoryContents[j] == null)
		{
			inventoryContents[j] = new ItemStack(item, 0, itemstack.getItemDamage());

			if (itemstack.hasTagCompound())
			{
				inventoryContents[j].setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
			}
		}

		int k = i;

		if (i > inventoryContents[j].getMaxStackSize() - inventoryContents[j].stackSize)
		{
			k = inventoryContents[j].getMaxStackSize() - inventoryContents[j].stackSize;
		}

		if (k > getInventoryStackLimit() - inventoryContents[j].stackSize)
		{
			k = getInventoryStackLimit() - inventoryContents[j].stackSize;
		}

		if (k == 0)
		{
			return i;
		}

		i -= k;

		inventoryContents[j].stackSize += k;
		inventoryContents[j].animationsToGo = 5;

		return i;
	}

	public boolean addItemStackToInventory(ItemStack itemstack)
	{
		int i;

		if (itemstack.isItemDamaged())
		{
			i = getFirstEmptySlot();

			if (i >= 0)
			{
				setInventorySlotContents(i, itemstack);

				return true;
			}

			return false;
		}

		do
		{
			i = itemstack.stackSize;
			itemstack.stackSize = storePartialItemStack(itemstack);
		}
		while (itemstack.stackSize > 0 && itemstack.stackSize < i);

		needsSort = true;

		return itemstack.stackSize < i;
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

		needsSort = true;
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

	public void sort()
	{
		ItemStack[] contents = Arrays.copyOf(inventoryContents, inventoryContents.length);

		Arrays.sort(contents, new InventoryComparator());
		contents = compact(contents);

		for (int i = 0; i < contents.length; ++i)
		{
			setInventorySlotContents(i, contents[i]);
		}

		needsSort = false;
	}

	private ItemStack[] compact(ItemStack[] contents)
	{
		int count = 0;
		ItemStack[] compact = new ItemStack[contents.length];

		for (int i = 0; i < contents.length && contents[i] != null; ++i)
		{
			while (contents[i] != null)
			{
				if (compact[count] == null)
				{
					compact[count] = contents[i];
					contents[i] = null;
				}
				else if (CaveUtils.canMerge(compact[count], contents[i]))
				{
					int trans = Math.min(compact[count].getMaxStackSize() - compact[count].stackSize, contents[i].stackSize);

					compact[count].stackSize += trans;
					contents[i].stackSize -= trans;

					if (contents[i].stackSize == 0)
					{
						contents[i] = null;
					}

					if (compact[count].stackSize == compact[count].getMaxStackSize())
					{
						count++;
					}
				}
				else
				{
					count++;
				}
			}
		}

		return compact;
	}
}