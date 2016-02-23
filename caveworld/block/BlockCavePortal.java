/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.block;

import java.util.Random;

import caveworld.api.BlockEntry;
import caveworld.api.CaverAPI;
import caveworld.api.CaveworldAPI;
import caveworld.api.ICavenicMob;
import caveworld.client.gui.MenuType;
import caveworld.core.Caveworld;
import caveworld.core.Config;
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.client.PortalMenuMessage;
import caveworld.plugin.mceconomy.MCEconomyPlugin;
import caveworld.util.CaveUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

public abstract class BlockCavePortal extends BlockPortal implements IBlockPortal
{
	@SideOnly(Side.CLIENT)
	private IIcon portalIcon;

	public BlockCavePortal(String name)
	{
		super();
		this.setBlockName(name);
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

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getPortalIcon()
	{
		return portalIcon;
	}

	@Override
	public int getRenderType()
	{
		return Config.RENDER_TYPE_PORTAL;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		int metadata = world.getBlockMetadata(x, y, z);

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

		if (metadata == 5)
		{
			if (world.getBlock(x - 1, y, z) != this && world.getBlock(x + 1, y, z) != this)
			{
				metadata = 4;
			}
			else
			{
				metadata = 3;
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
		else if (size3.canCreatePortal() && size3.portalBlockCount == 0)
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
		int metadata = world.getBlockMetadata(x, y, z);

		if (metadata == 1 || metadata == 2)
		{
			BlockEntry frame = new BlockEntry(Blocks.mossy_cobblestone, 0);
			Size size1 = new Size(world, x, y, z, 1, frame);
			Size size2 = new Size(world, x, y, z, 2, frame);

			if (metadata == 1 && (!size1.canCreatePortal() || size1.portalBlockCount < size1.portalWidth * size1.portalHeight))
			{
				world.setBlockToAir(x, y, z);
			}
			else if (metadata == 2 && (!size2.canCreatePortal() || size2.portalBlockCount < size2.portalWidth * size2.portalHeight))
			{
				world.setBlockToAir(x, y, z);
			}
		}
		else if (metadata == 3 || metadata == 4)
		{
			BlockEntry frame = new BlockEntry(Blocks.stonebrick, 1);
			Size size3 = new Size(world, x, y, z, 3, frame);
			Size size4 = new Size(world, x, y, z, 4, frame);

			if (metadata == 3 && (!size3.canCreatePortal() || size3.portalBlockCount < size3.portalWidth * size3.portalHeight))
			{
				world.setBlockToAir(x, y, z);
			}
			else if (metadata == 4 && (!size4.canCreatePortal() || size4.portalBlockCount < size4.portalWidth * size4.portalHeight))
			{
				world.setBlockToAir(x, y, z);
			}
		}
		else
		{
			world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		int metadata = 0;

		if (world.getBlock(x, y, z) == this)
		{
			metadata = world.getBlockMetadata(x, y, z);

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
		if (side >= 2)
		{
			if (!world.isRemote)
			{
				world.playSoundAtEntity(player, "random.click", 0.8F, 1.5F);
			}

			if (MCEconomyPlugin.enabled() && CaveUtils.isItemPickaxe(player.getCurrentEquippedItem()))
			{
				if (!world.isRemote)
				{
					MCEconomyAPI.openShopGui(MCEconomyPlugin.SHOP, player, world, x, y, z);
				}

				return true;
			}

			if (player instanceof EntityPlayerMP)
			{
				EntityPlayerMP thePlayer = (EntityPlayerMP)player;
				MinecraftServer server = thePlayer.mcServer;

				if (server.isSinglePlayer() || server.getConfigurationManager().func_152596_g(thePlayer.getGameProfile()))
				{
					CaveNetworkRegistry.sendTo(new PortalMenuMessage(getType(), x, y, z), thePlayer);
				}
				else
				{
					onMenuUnusable(world, x, y, z, thePlayer);
				}
			}
		}

		return true;
	}

	public void onMenuUnusable(World world, int x, int y, int z, EntityPlayerMP player) {}

	public abstract int getType();

	public abstract MenuType getMenuType();

	public abstract boolean isEntityInCave(Entity entity);

	public abstract int getDimension();

	public abstract int getLastDimension(Entity entity);

	public abstract void setLastDimension(Entity entity, int dim);

	public void setLastPos(Entity entity, int dim, ChunkCoordinates coord)
	{
		CaverAPI.setLastPos(entity, dim, getType(), coord);
	}

	public boolean canPortalTeleport(WorldServer worldOld, WorldServer worldNew, int x, int y, int z, Entity entity)
	{
		return !CaveworldAPI.isHardcore() || CaveworldAPI.isCaveDimensions(worldNew.provider.dimensionId);
	}

	public abstract Teleporter getTeleporter(WorldServer worldServer, boolean brick);

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (!world.isRemote && entity.isEntityAlive() && !(entity instanceof ICavenicMob))
		{
			if (entity.timeUntilPortal <= 0)
			{
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				int dimOld = entity.dimension;
				int dimNew = isEntityInCave(entity) ? getLastDimension(entity) : getDimension();

				if (dimOld == dimNew)
				{
					dimOld = 0;
					dimNew = getDimension();
				}

				WorldServer worldOld = server.worldServerForDimension(dimOld);
				WorldServer worldNew = server.worldServerForDimension(dimNew);

				if (worldOld == null || worldNew == null || !canPortalTeleport(worldOld, worldNew, x, y, z, entity))
				{
					return;
				}

				int meta = world.getBlockMetadata(x, y, z);
				boolean brick = false;

				if (meta == 3 || meta == 4)
				{
					brick = true;
				}

				Teleporter teleporter = getTeleporter(worldNew, brick);

				entity.worldObj.removeEntity(entity);
				entity.isDead = false;
				entity.timeUntilPortal = entity.getPortalCooldown();

				if (entity instanceof EntityPlayerMP)
				{
					EntityPlayerMP player = (EntityPlayerMP)entity;

					if (!player.isSneaking() && !player.isPotionActive(Potion.blindness))
					{
						setLastPos(player, dimOld, player.getPlayerCoordinates());

						worldOld.playSoundToNearExcept(player, "caveworld:caveworld_portal", 0.5F, 1.0F);

						server.getConfigurationManager().transferPlayerToDimension(player, dimNew, teleporter);

						worldNew.playSoundAtEntity(player, "caveworld:caveworld_portal", 0.75F, 1.0F);

						setLastDimension(player, dimOld);
					}
				}
				else
				{
					entity.dimension = dimNew;

					setLastPos(entity, dimOld, new ChunkCoordinates(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY + 0.5D), MathHelper.floor_double(entity.posZ)));

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

						setLastDimension(target, dimOld);
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
	public void updateTick(World world, int x, int y, int z, Random random) {}

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

	private class Size
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

					if (block == BlockCavePortal.this)
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
			return block.getMaterial() == Material.air || block == BlockCavePortal.this;
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
							if (worldObj.getBlock(x + 1, portalCoord.posY + j, z) == BlockCavePortal.this)
							{
								return false;
							}
							else if (worldObj.getBlock(x - 1, portalCoord.posY + j, z) == BlockCavePortal.this)
							{
								return false;
							}
						}
						else
						{
							if (worldObj.getBlock(x, portalCoord.posY + j, z + 1) == BlockCavePortal.this)
							{
								return false;
							}
							else if (worldObj.getBlock(x, portalCoord.posY + j, z - 1) == BlockCavePortal.this)
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
					worldObj.setBlock(x, portalCoord.posY + j, z, BlockCavePortal.this, portalMetadata, 2);
				}
			}
		}
	}

	public class DispencePortal extends BehaviorDefaultDispenseItem
	{
		@Override
		public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemstack)
		{
			EnumFacing facing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
			World world = blockSource.getWorld();
			int x = blockSource.getXInt() + facing.getFrontOffsetX();
			int y = blockSource.getYInt() + facing.getFrontOffsetY();
			int z = blockSource.getZInt() + facing.getFrontOffsetZ();

			if (func_150000_e(world, x, y, z))
			{
				--itemstack.stackSize;
			}

			return itemstack;
		}

		@Override
		public void playDispenseSound(IBlockSource blockSource)
		{
			super.playDispenseSound(blockSource);

			blockSource.getWorld().playSoundEffect(blockSource.getXInt(), blockSource.getYInt(), blockSource.getZInt(), stepSound.func_150496_b(), 1.0F, 2.0F);
		}
	}
}