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
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Sets;

import caveworld.api.CaveworldAPI;
import caveworld.entity.ai.EntityAICollector;
import caveworld.entity.ai.EntityAIFleeSun2;
import caveworld.entity.ai.EntityAISoldier;
import caveworld.item.CaveItems;
import caveworld.item.ItemCavenium;
import caveworld.util.CaveUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.Constants.NBT;

public class EntityCaveman extends EntityMob implements IInventory
{
	public static int spawnWeight;
	public static int spawnMinHeight;
	public static int spawnMaxHeight;
	public static int spawnInChunks;
	public static int[] spawnBiomes;

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

	public boolean isCollecting;
	public boolean inventoryFull;

	public EntityCaveman(World world)
	{
		super(world);
		this.setSize(0.45F, 1.75F);
		this.getNavigator().setAvoidSun(true);
		this.getNavigator().setCanSwim(true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(3, new EntityAIRestrictSun(this));
		this.tasks.addTask(4, new EntityAIFleeSun2(this, 1.25D));
		this.tasks.addTask(5, new EntityAISoldier(this));
		this.tasks.addTask(6, new EntityAICollector(this, 1.0D, 20.0F));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.5D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.325D);
	}

	@Override
	public boolean isAIEnabled()
	{
		return true;
	}

	@Override
	public boolean isChild()
	{
		return false;
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

	public long getStoppedTime()
	{
		return stoppedTime;
	}

	public boolean isStopped()
	{
		return onGround && getStoppedTime() > 500L;
	}

	public String getLocalizedName()
	{
		return StatCollector.translateToLocal("entity." + getEntityString() + ".name");
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
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if (isStopped())
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

		if (isStopped() && stoppedTime % 100 == 0)
		{
			heal(0.5F + rand.nextFloat() / 2);
		}

		if (!worldObj.isRemote && isEntityAlive() && !isStopped())
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
			for (ItemStack itemstack : inventoryContents)
			{
				ItemStack content = ItemStack.copyItemStack(itemstack);

				if (content != null && content.getItem() != null && content.stackSize > 0 && rand.nextInt(3) == 0)
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

		if (nbt.hasKey("Items"))
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

		if (nbt.hasKey("Stopped"))
		{
			stoppedTime = nbt.getLong("Stopped");
		}
	}

	public boolean isValidHeight()
	{
		int y = MathHelper.floor_double(boundingBox.minY);

		return y >= spawnMinHeight && y <= spawnMaxHeight;
	}

	@Override
	public boolean getCanSpawnHere()
	{
		return CaveworldAPI.isEntityInCaves(this) && isValidHeight() && super.getCanSpawnHere();
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

	private int storeItemStack(ItemStack itemstack)
	{
		for (int i = 0; i < inventoryContents.length; ++i)
		{
			ItemStack item = inventoryContents[i];

			if (item != null && item.getItem() == itemstack.getItem() && item.isStackable() && item.stackSize < item.getMaxStackSize() && item.stackSize < getInventoryStackLimit() &&
				(!item.getHasSubtypes() || item.getItemDamage() == itemstack.getItemDamage()) && ItemStack.areItemStackTagsEqual(item, itemstack))
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
		if (itemstack == null || itemstack.getItem() == null)
		{
			return false;
		}

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