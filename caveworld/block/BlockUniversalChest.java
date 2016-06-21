package caveworld.block;

import java.util.Random;

import caveworld.client.particle.EntityUniversalChestFX;
import caveworld.config.Config;
import caveworld.core.Caveworld;
import caveworld.entity.TileEntityUniversalChest;
import caveworld.inventory.InventoryUniversalChest;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockUniversalChest extends BlockContainer
{
	public final InventoryUniversalChest inventory = new InventoryUniversalChest();

	private NBTTagCompound data;

	public BlockUniversalChest(String name)
	{
		super(Material.rock);
		this.setBlockName(name);
		this.setBlockTextureName("caveworld:universal_chest");
		this.setHardness(13.5F);
		this.setResistance(800.0F);
		this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		this.setStepSound(soundTypePiston);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	public void setData(NBTTagCompound nbt)
	{
		data = nbt;
	}

	public NBTTagCompound getData()
	{
		if (data == null)
		{
			data = new NBTTagCompound();
		}

		return data;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return Config.RENDER_TYPE_CHEST;
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack)
	{
		byte metadata = 0;

		switch (MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3)
		{
			case 0:
				metadata = 2;
				break;
			case 1:
				metadata = 5;
				break;
			case 2:
				metadata = 3;
				break;
			case 3:
				metadata = 4;
				break;
		}

		world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		TileEntityUniversalChest chest = (TileEntityUniversalChest)world.getTileEntity(x, y, z);

		if (chest != null)
		{
			if (world.getBlock(x, y + 1, z).isNormalCube() || world.isRemote)
			{
				return true;
			}

			player.displayGUIChest(inventory.setAssociatedChest(chest));
		}

		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityUniversalChest();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
		for (int i = 0; i < 3; ++i)
		{
			int var1 = random.nextInt(2) * 2 - 1;
			int var2 = random.nextInt(2) * 2 - 1;
			double ptX = x + 0.5D + 0.25D * var1;
			double ptY = y + random.nextFloat();
			double ptZ = z + 0.5D + 0.25D * var2;
			double motionX = random.nextFloat() * 1.0F * var1;
			double motionY = (random.nextFloat() - 0.5D) * 0.125D;
			double motionZ = random.nextFloat() * 1.0F * var2;
			EntityFX particle = new EntityUniversalChestFX(world, ptX, ptY, ptZ, motionX, motionY, motionZ);

			FMLClientHandler.instance().getClient().effectRenderer.addEffect(particle);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer)
	{
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		float f = 0.1F;
		double ptX = x + world.rand.nextDouble() * (getBlockBoundsMaxX() - getBlockBoundsMinX() - f * 2.0F) + f + getBlockBoundsMinX();
		double ptY = y + world.rand.nextDouble() * (getBlockBoundsMaxY() - getBlockBoundsMinY() - f * 2.0F) + f + getBlockBoundsMinY();
		double ptZ = z + world.rand.nextDouble() * (getBlockBoundsMaxZ() - getBlockBoundsMinZ() - f * 2.0F) + f + getBlockBoundsMinZ();

		switch (target.sideHit)
		{
			case 0:
				ptY = y + getBlockBoundsMinY() - f;
				break;
			case 1:
				ptY = y + getBlockBoundsMaxY() + f;
				break;
			case 2:
				ptZ = z + getBlockBoundsMinZ() - f;
				break;
			case 3:
				ptZ = z + getBlockBoundsMaxZ() + f;
				break;
			case 4:
				ptX = x + getBlockBoundsMinX() - f;
				break;
			case 5:
				ptX = x + getBlockBoundsMaxX() + f;
				break;
		}

		effectRenderer.addEffect(new EntityDiggingFX(world, ptX, ptY, ptZ, 0.0D, 0.0D, 0.0D, CaveBlocks.cavenium_ore, 3).applyColourMultiplier(x, y, z).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World world, int x, int y, int z, int metadata, EffectRenderer effectRenderer)
	{
		byte b = 4;

		for (int i = 0; i < b; ++i)
		{
			for (int j = 0; j < b; ++j)
			{
				for (int k = 0; k < b; ++k)
				{
					double ptX = x + (i + 0.5D) / b;
					double ptY = y + (j + 0.5D) / b;
					double ptZ = z + (k + 0.5D) / b;

					effectRenderer.addEffect(new EntityDiggingFX(world, ptX, ptY, ptZ, ptX - x - 0.5D, ptY - y - 0.5D, ptZ - z - 0.5D, CaveBlocks.cavenium_ore, 3).applyColourMultiplier(x, y, z));
				}
			}
		}

		return true;
	}
}