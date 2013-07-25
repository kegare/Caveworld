package kegare.caveworld.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiomeGenCaveworld extends BiomeGenBase
{
	public BiomeGenCaveworld(int biomeID)
	{
		super(biomeID);
		this.setBiomeName("Caveworld");
		this.setColor(0x353535);
		this.setMinMaxHeight(2.0F, 2.0F);
		this.setTemperatureRainfall(0.2F, 0.0F);
		this.setDisableRain();
		this.topBlock = (byte)Block.stone.blockID;
		this.fillerBlock = (byte)Block.stone.blockID;
	}

	@Override
	public BiomeDecorator createBiomeDecorator()
	{
		return getModdedBiomeDecorator(new BiomeCaveworldDecorator(this));
	}

	@Override
	public void decorate(World world, Random random, int chunkX, int chunkZ)
	{
		super.decorate(world, random, chunkX, chunkZ);

		for (int i = 0; i < random.nextInt(6) + 4; ++i)
		{
			int x = chunkX + random.nextInt(16);
			int y = random.nextInt(28) + 4;
			int z = chunkZ + random.nextInt(16);

			if (world.getBlockId(x, y, z) == Block.stone.blockID)
			{
				world.setBlock(x, y, z, Block.oreEmerald.blockID, 0, 2);
			}
		}

		for (int i = 0; i < random.nextInt(4) + 6; ++i)
		{
			int x = chunkX + random.nextInt(16);
			int y = random.nextInt(124) + 4;
			int z = chunkZ + random.nextInt(16);

			if (world.getBlockId(x, y, z) == Block.stone.blockID)
			{
				world.setBlock(x, y, z, Block.silverfish.blockID, 0, 2);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getSkyColorByTemp(float par1)
	{
		return 0;
	}

	@Override
	public boolean canSpawnLightningBolt()
	{
		return false;
	}

	@Override
	public boolean isHighHumidity()
	{
		return false;
	}
}