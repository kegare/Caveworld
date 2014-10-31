/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.block;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import shift.mceconomy2.api.MCEconomyAPI;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.world.TeleporterCaveworld;
import com.kegare.caveworld.world.WorldProviderCaveworld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPortalCaveworld extends BlockPortal implements IInventory
{
	private final ItemStack[] inventoryContents = new ItemStack[getSizeInventory()];
	private final Table<String, Integer, ChunkCoordinates> portalCoord = HashBasedTable.create();

	@SideOnly(Side.CLIENT)
	public IIcon portalIcon;

	public boolean portalDisabled;

	public BlockPortalCaveworld(String name)
	{
		super();
		this.setBlockName(name);
		this.setBlockTextureName("caveworld:caveworld_portal");
		this.setBlockUnbreakable();
		this.setLightOpacity(3);
		this.setLightLevel(0.6F);
		this.setStepSound(soundTypeGlass);
		this.disableStats();
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon(getTextureName() + "_block");
		portalIcon = iconRegister.registerIcon(getTextureName());
	}

	@Override
	public int getRenderType()
	{
		return Config.RENDER_TYPE_PORTAL;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		int metadata = func_149999_b(world.getBlockMetadata(x, y, z));

		if (metadata == 0)
		{
			if (world.getBlock(x - 1, y, z) != this && world.getBlock(x + 1, y, z) != this)
			{
				metadata = 2;
			}
			else
			{
				metadata = 1;
			}

			if (world instanceof World && !((World)world).isRemote)
			{
				((World)world).setBlockMetadataWithNotify(x, y, z, metadata, 2);
			}
		}

		float var1 = 0.15F;
		float var2 = 0.15F;

		if (metadata % 2 != 0)
		{
			var1 = 0.5F;
		}
		else
		{
			var2 = 0.5F;
		}

		setBlockBounds(0.5F - var1, 0.0F, 0.5F - var2, 0.5F + var1, 1.0F, 0.5F + var2);
	}

	@Override
	public boolean func_150000_e(World world, int x, int y, int z)
	{
		if (world.provider.dimensionId == 1)
		{
			world.newExplosion(null, x, y, z, 4.5F, true, true);

			return true;
		}

		BlockEntry frame = new BlockEntry(Blocks.mossy_cobblestone, 0);
		Size size1 = new Size(world, x, y, z, 1, frame);
		Size size2 = new Size(world, x, y, z, 2, frame);
		frame = new BlockEntry(Blocks.stonebrick, 1);
		Size size3 = new Size(world, x, y, z, 3, frame);
		Size size4 = new Size(world, x, y, z, 4, frame);

		if (size1.canCreatePortal() && size1.portalBlockCount == 0)
		{
			size1.setPortalBlocks();

			return true;
		}
		else if (size2.canCreatePortal() && size2.portalBlockCount == 0)
		{
			size2.setPortalBlocks();

			return true;
		}
		if (size3.canCreatePortal() && size3.portalBlockCount == 0)
		{
			size3.setPortalBlocks();

			return true;
		}
		else if (size4.canCreatePortal() && size4.portalBlockCount == 0)
		{
			size4.setPortalBlocks();

			return true;
		}

		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		int metadata = func_149999_b(world.getBlockMetadata(x, y, z));

		if (metadata == 1 || metadata == 2)
		{
			BlockEntry frame = new BlockEntry(Blocks.mossy_cobblestone, 0);
			Size size1 = new Size(world, x, y, z, 1, frame);
			Size size2 = new Size(world, x, y, z, 2, frame);

			if (metadata == 1 && (!size1.canCreatePortal() || size1.portalBlockCount < size1.portalWidth * size1.portalHeight))
			{
				world.setBlock(x, y, z, Blocks.air);
			}
			else if (metadata == 2 && (!size2.canCreatePortal() || size2.portalBlockCount < size2.portalWidth * size2.portalHeight))
			{
				world.setBlock(x, y, z, Blocks.air);
			}
		}
		else if (metadata == 3 || metadata == 4)
		{
			BlockEntry frame = new BlockEntry(Blocks.stonebrick, 1);
			Size size3 = new Size(world, x, y, z, 3, frame);
			Size size4 = new Size(world, x, y, z, 4, frame);

			if (metadata == 3 && (!size3.canCreatePortal() || size3.portalBlockCount < size3.portalWidth * size3.portalHeight))
			{
				world.setBlock(x, y, z, Blocks.air);
			}
			else if (metadata == 4 && (!size4.canCreatePortal() || size4.portalBlockCount < size4.portalWidth * size4.portalHeight))
			{
				world.setBlock(x, y, z, Blocks.air);
			}
		}
		else
		{
			world.setBlock(x, y, z, Blocks.air);
		}
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		int metadata = 0;

		if (world.getBlock(x, y, z) == this)
		{
			metadata = func_149999_b(world.getBlockMetadata(x, y, z));

			if (metadata == 0)
			{
				return false;
			}

			if (metadata % 2 == 0 && side != 5 && side != 4)
			{
				return false;
			}

			if (metadata % 2 != 0 && side != 3 && side != 2)
			{
				return false;
			}
		}

		boolean flag = world.getBlock(x - 1, y, z) == this && world.getBlock(x - 2, y, z) != this;
		boolean flag1 = world.getBlock(x + 1, y, z) == this && world.getBlock(x + 2, y, z) != this;
		boolean flag2 = world.getBlock(x, y, z - 1) == this && world.getBlock(x, y, z - 2) != this;
		boolean flag3 = world.getBlock(x, y, z + 1) == this && world.getBlock(x, y, z + 2) != this;
		boolean flag4 = flag || flag1 || metadata == 1;
		boolean flag5 = flag2 || flag3 || metadata == 2;

		return flag4 && side == 4 ? true : flag4 && side == 5 ? true : flag5 && side == 2 ? true : flag5 && side == 3;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote && side >= 2)
		{
			world.playSoundAtEntity(player, "random.click", 0.8F, 1.5F);

			if (MCEconomyPlugin.enabled() && MCEconomyPlugin.SHOP >= 0 && CaveworldAPI.isEntityInCaveworld(player))
			{
				if (CaveUtils.isItemPickaxe(player.getCurrentEquippedItem()))
				{
					MCEconomyAPI.openShopGui(MCEconomyPlugin.SHOP, player, world, x, y, z);

					return true;
				}
			}

			displayInventory(player, x, y, z);
		}

		return true;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (!world.isRemote && !portalDisabled && entity.isEntityAlive() && entity.dimension != CaveworldAPI.getDeepDimension() && (!CaveworldAPI.isEntityInCaveworld(entity) || !Config.hardcore))
		{
			if (entity.timeUntilPortal <= 0)
			{
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				int dimOld = entity.dimension;
				int dimNew = dimOld == CaveworldAPI.getDimension() ? entity.getEntityData().getInteger("Caveworld:LastDim") : CaveworldAPI.getDimension();
				WorldServer worldOld = server.worldServerForDimension(dimOld);
				WorldServer worldNew = server.worldServerForDimension(dimNew);

				if (worldOld == null || worldNew == null)
				{
					return;
				}

				Teleporter teleporter = new TeleporterCaveworld(worldNew);

				entity.worldObj.removeEntity(entity);
				entity.isDead = false;
				entity.timeUntilPortal = entity.getPortalCooldown();

				if (entity instanceof EntityPlayerMP)
				{
					EntityPlayerMP player = (EntityPlayerMP)entity;

					if (!player.isSneaking() && !player.isPotionActive(Potion.blindness))
					{
						worldOld.playSoundToNearExcept(player, "caveworld:caveworld_portal", 0.5F, 1.0F);

						server.getConfigurationManager().transferPlayerToDimension(player, dimNew, teleporter);

						worldNew.playSoundAtEntity(player, "caveworld:caveworld_portal", 0.75F, 1.0F);

						player.getEntityData().setInteger("Caveworld:LastDim", dimOld);
					}
				}
				else
				{
					entity.dimension = dimNew;

					server.getConfigurationManager().transferEntityToWorld(entity, dimOld, worldOld, worldNew, teleporter);

					Entity target = EntityList.createEntityByName(EntityList.getEntityString(entity), worldNew);

					if (target != null)
					{
						worldOld.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "caveworld:caveworld_portal", 0.25F, 1.15F);

						target.copyDataFrom(entity, true);
						target.forceSpawn = true;

						worldNew.spawnEntityInWorld(target);
						worldNew.playSoundAtEntity(target, "caveworld:caveworld_portal", 0.5F, 1.15F);

						target.forceSpawn = false;
						target.getEntityData().setInteger("Caveworld:LastDim", dimOld);
					}

					entity.setDead();

					worldOld.resetUpdateEntityTick();
					worldNew.resetUpdateEntityTick();
				}
			}
			else
			{
				entity.timeUntilPortal = entity.getPortalCooldown();
			}
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if (!world.isRemote && world.provider.isSurfaceWorld())
		{
			if (world.isDaytime() || !world.getGameRules().getGameRuleBooleanValue("doMobSpawning"))
			{
				return;
			}
			else if (!world.isBlockNormalCubeDefault(x, y + 1, z, false) && random.nextInt(300) < world.difficultySetting.getDifficultyId())
			{
				EntityLiving entity = CaveUtils.createEntity(EntityBat.class, world);

				if (entity != null)
				{
					entity.setLocationAndAngles(x + 0.5D, y + 0.75D, z + 0.5D, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
					entity.rotationYawHead = entity.renderYawOffset = entity.rotationYaw;
					entity.timeUntilPortal = entity.getPortalCooldown();
					entity.onSpawnWithEgg(null);

					if (world.spawnEntityInWorld(entity))
					{
						entity.playLivingSound();
						entity.getEntityData().setBoolean("Caveworld:CaveBat", true);
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
		if (random.nextInt(200) == 0)
		{
			world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "ambient.cave.cave", 0.25F, random.nextFloat() * 0.4F + 0.8F, false);
		}

		if (random.nextInt(3) == 0)
		{
			double ptX = x + random.nextFloat();
			double ptY = y + 0.5D;
			double ptZ = z + random.nextFloat();

			world.spawnParticle("reddust", ptX, ptY, ptZ, 0.5D, 1.0D, 1.0D);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Item getItem(World world, int x, int y, int z)
	{
		return Item.getItemFromBlock(this);
	}

	@Override
	public String getInventoryName()
	{
		return "inventory.caveworld.portal";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getSizeInventory()
	{
		return 18;
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
		inventoryContents[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		ChunkCoordinates coord = portalCoord.get(player.getGameProfile().getId().toString(), player.dimension);

		if (coord == null)
		{
			return false;
		}

		int x = coord.posX;
		int y = coord.posY;
		int z = coord.posZ;

		if (player.worldObj.getBlock(x, y, z) != CaveBlocks.caveworld_portal)
		{
			return false;
		}

		return player.getDistance(x, y, z) <= 6.0D;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return true;
	}

	public void displayInventory(EntityPlayer player, int x, int y, int z)
	{
		portalCoord.put(player.getGameProfile().getId().toString(), player.dimension, new ChunkCoordinates(x, y, z));

		player.displayGUIChest(this);
	}

	public void clearInventory()
	{
		Arrays.fill(inventoryContents, null);
	}

	public void loadInventoryFromDimData()
	{
		NBTTagCompound data = WorldProviderCaveworld.getDimData();

		if (!data.hasKey("PortalItems"))
		{
			return;
		}

		NBTTagList list = (NBTTagList)data.getTag("PortalItems");

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

		data.removeTag("PortalItems");
	}

	public void saveInventoryToDimData()
	{
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

		WorldProviderCaveworld.getDimData().setTag("PortalItems", list);
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	public static class Size
	{
		private final World worldObj;
		private final int portalMetadata;
		private final boolean portalDiffer;
		private final BlockEntry portalFrame;
		private final int field_150863_d;
		private final int field_150866_c;

		private ChunkCoordinates portalCoord;
		private int portalWidth;
		private int portalHeight;

		private int portalBlockCount = 0;

		public Size(World world, int x, int y, int z, int metadata, BlockEntry frame)
		{
			this.worldObj = world;
			this.portalMetadata = metadata;
			this.portalDiffer = metadata % 2 == 0;
			this.portalFrame = frame;
			int i = portalDiffer ? 2 : 1;
			this.field_150863_d = BlockPortal.field_150001_a[i][0];
			this.field_150866_c = BlockPortal.field_150001_a[i][1];

			i = y;

			while (y > i - 21 && y > 0 && isReplaceablePortal(world.getBlock(x, y - 1, z)))
			{
				--y;
			}

			i = getPortalWidth(x, y, z, field_150863_d) - 1;

			if (i >= 0)
			{
				this.portalCoord = new ChunkCoordinates(x + i * Direction.offsetX[field_150863_d], y, z + i * Direction.offsetZ[field_150863_d]);
				this.portalWidth = getPortalWidth(portalCoord.posX, portalCoord.posY, portalCoord.posZ, field_150866_c);

				if (portalWidth < 2 || portalWidth > 21)
				{
					this.portalCoord = null;
					this.portalWidth = 0;
				}
			}

			if (portalCoord != null)
			{
				this.portalHeight = getPortalHeight();
			}
		}

		protected int getPortalWidth(int x, int y, int z, int par4)
		{
			int var1 = Direction.offsetX[par4];
			int var2 = Direction.offsetZ[par4];
			int i;

			for (i = 0; i < 22; ++i)
			{
				if (!isReplaceablePortal(worldObj.getBlock(x + var1 * i, y, z + var2 * i)))
				{
					break;
				}

				if (worldObj.getBlock(x + var1 * i, y - 1, z + var2 * i) != portalFrame.getBlock() || worldObj.getBlockMetadata(x + var1 * i, y - 1, z + var2 * i) != portalFrame.getMetadata())
				{
					break;
				}
			}

			return worldObj.getBlock(x + var1 * i, y, z + var2 * i) == portalFrame.getBlock() && worldObj.getBlockMetadata(x + var1 * i, y, z + var2 * i) == portalFrame.getMetadata() ? i : 0;
		}

		protected int getPortalHeight()
		{
			int i, x, y, z;

			outside: for (portalHeight = 0; portalHeight < 21; ++portalHeight)
			{
				y = portalCoord.posY + portalHeight;

				for (i = 0; i < portalWidth; ++i)
				{
					x = portalCoord.posX + i * Direction.offsetX[field_150866_c];
					z = portalCoord.posZ + i * Direction.offsetZ[field_150866_c];
					Block block = worldObj.getBlock(x, y, z);
					int meta;

					if (!isReplaceablePortal(block))
					{
						break outside;
					}

					if (block == CaveBlocks.caveworld_portal)
					{
						++portalBlockCount;
					}

					if (i == 0)
					{
						block = worldObj.getBlock(x + Direction.offsetX[field_150863_d], y, z + Direction.offsetZ[field_150863_d]);
						meta = worldObj.getBlockMetadata(x + Direction.offsetX[field_150863_d], y, z + Direction.offsetZ[field_150863_d]);

						if (block != portalFrame.getBlock() || meta != portalFrame.getMetadata())
						{
							break outside;
						}
					}
					else if (i == portalWidth - 1)
					{
						block = worldObj.getBlock(x + Direction.offsetX[field_150866_c], y, z + Direction.offsetZ[field_150866_c]);
						meta = worldObj.getBlockMetadata(x + Direction.offsetX[field_150866_c], y, z + Direction.offsetZ[field_150866_c]);

						if (block != portalFrame.getBlock() || meta != portalFrame.getMetadata())
						{
							break outside;
						}
					}
				}
			}

			for (y = 0; y < portalWidth; ++y)
			{
				i = portalCoord.posX + y * Direction.offsetX[field_150866_c];
				x = portalCoord.posY + portalHeight;
				z = portalCoord.posZ + y * Direction.offsetZ[field_150866_c];

				if (worldObj.getBlock(i, x, z) != portalFrame.getBlock() || worldObj.getBlockMetadata(i, x, z) != portalFrame.getMetadata())
				{
					portalHeight = 0;

					break;
				}
			}

			if (portalHeight <= 21 && portalHeight >= 3)
			{
				return portalHeight;
			}

			portalCoord = null;
			portalWidth = 0;
			portalHeight = 0;

			return 0;
		}

		protected boolean isReplaceablePortal(Block block)
		{
			return block.getMaterial() == Material.air || block == CaveBlocks.caveworld_portal;
		}

		public boolean canCreatePortal()
		{
			if (portalCoord != null && portalWidth >= 2 && portalWidth <= 21 && portalHeight >= 3 && portalHeight <= 21)
			{
				for (int i = 0; i < portalWidth; ++i)
				{
					int x = portalCoord.posX + Direction.offsetX[field_150866_c] * i;
					int z = portalCoord.posZ + Direction.offsetZ[field_150866_c] * i;

					for (int j = 0; j < portalHeight; ++j)
					{
						if (portalDiffer)
						{
							if (worldObj.getBlock(x + 1, portalCoord.posY + j, z) == CaveBlocks.caveworld_portal)
							{
								return false;
							}
							else if (worldObj.getBlock(x - 1, portalCoord.posY + j, z) == CaveBlocks.caveworld_portal)
							{
								return false;
							}
						}
						else
						{
							if (worldObj.getBlock(x, portalCoord.posY + j, z + 1) == CaveBlocks.caveworld_portal)
							{
								return false;
							}
							else if (worldObj.getBlock(x, portalCoord.posY + j, z - 1) == CaveBlocks.caveworld_portal)
							{
								return false;
							}
						}
					}
				}

				return true;
			}

			return false;
		}

		public void setPortalBlocks()
		{
			int x, z;

			for (int i = 0; i < portalWidth; ++i)
			{
				x = portalCoord.posX + Direction.offsetX[field_150866_c] * i;
				z = portalCoord.posZ + Direction.offsetZ[field_150866_c] * i;

				for (int j = 0; j < portalHeight; ++j)
				{
					worldObj.setBlock(x, portalCoord.posY + j, z, CaveBlocks.caveworld_portal, portalMetadata, 2);
				}
			}
		}
	}

	public static class DispencePortal extends BehaviorDefaultDispenseItem
	{
		@Override
		public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemstack)
		{
			EnumFacing facing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
			World world = blockSource.getWorld();
			int x = blockSource.getXInt() + facing.getFrontOffsetX();
			int y = blockSource.getYInt() + facing.getFrontOffsetY();
			int z = blockSource.getZInt() + facing.getFrontOffsetZ();

			if (CaveBlocks.caveworld_portal.func_150000_e(world, x, y, z))
			{
				--itemstack.stackSize;
			}

			return itemstack;
		}

		@Override
		public void playDispenseSound(IBlockSource blockSource)
		{
			super.playDispenseSound(blockSource);

			blockSource.getWorld().playSoundEffect(blockSource.getXInt(), blockSource.getYInt(), blockSource.getZInt(), CaveBlocks.caveworld_portal.stepSound.func_150496_b(), 1.0F, 2.0F);
		}
	}
}