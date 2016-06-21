package caveworld.block;

import java.util.List;
import java.util.Random;

import caveworld.core.Caveworld;
import caveworld.world.gen.WorldGenPervertedForest;
import caveworld.world.gen.WorldGenPervertedTaiga;
import caveworld.world.gen.WorldGenPervertedTrees;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BlockPervertedSapling extends BlockSapling implements IBlockPreverted
{
	@SideOnly(Side.CLIENT)
	public IIcon[] icons;

	public BlockPervertedSapling(String name)
	{
		super();
		this.setBlockName(name);
		this.setBlockTextureName("sapling");
		this.setHardness(0.0F);
		this.setStepSound(soundTypeGrass);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		icons = new IIcon[BlockPervertedLog.types.length];

		for (int i = 0; i < icons.length; ++i)
		{
			icons[i] = iconRegister.registerIcon(getTextureName() + "_" + BlockPervertedLog.types[i]);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		return icons[MathHelper.clamp_int(metadata & 7, 0, 3)];
	}

	@Override
	public Block getBasedBlock()
	{
		return Blocks.sapling;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if (!world.isRemote)
		{
			checkAndDropBlock(world, x, y, z);

			func_149879_c(world, x, y, z, random);
		}
	}

	@Override
	public void func_149878_d(World world, int x, int y, int z, Random random)
	{
		if (!TerrainGen.saplingGrowTree(world, random, x, y, z))
		{
			return;
		}

		int meta = world.getBlockMetadata(x, y, z) & 7;
		WorldGenerator worldGen = new WorldGenPervertedTrees(true);

		switch (meta)
		{
			case 0:
			default:
				break;
			case 1:
				worldGen = new WorldGenPervertedTaiga(true);
				break;
			case 2:
				worldGen = new WorldGenPervertedForest(true);
				break;
			case 3:
				worldGen = new WorldGenPervertedTrees(true, 4 + random.nextInt(7), 3, 3, false);
				break;
		}

		Block block = Blocks.air;

		world.setBlock(x, y, z, block, 0, 4);

		if (!worldGen.generate(world, random, x, y, z))
		{
			world.setBlock(x, y, z, this, meta, 4);
		}
	}

	@Override
	public int damageDropped(int metadata)
	{
		return MathHelper.clamp_int(metadata & 7, 0, 3);
	}

	@Override
	public boolean func_149852_a(World world, Random random, int x, int y, int z)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < BlockPervertedLog.types.length; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}
}