package kegare.caveworld.world;

import kegare.caveworld.core.Config;
import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeCaveworldDecorator extends BiomeDecorator
{
	public BiomeCaveworldDecorator(BiomeGenBase biome)
	{
		super(biome);
	}

	@Override
	protected void decorate()
	{
		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(currentWorld, randomGenerator, chunk_X, chunk_Z));

		generateOres();

		if (TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, EventType.SHROOM))
		{
			for (int i = 0; i < 12; ++i)
			{
				if (randomGenerator.nextInt(4) == 0)
				{
					int x = chunk_X + randomGenerator.nextInt(16) + 8;
					int y = randomGenerator.nextInt(128);
					int z = chunk_Z + randomGenerator.nextInt(16) + 8;

					mushroomBrownGen.generate(currentWorld, randomGenerator, x, y, z);
				}

				if (randomGenerator.nextInt(8) == 0)
				{
					int x = chunk_X + randomGenerator.nextInt(16) + 8;
					int y = randomGenerator.nextInt(128);
					int z = chunk_Z + randomGenerator.nextInt(16) + 8;

					mushroomRedGen.generate(currentWorld, randomGenerator, x, y, z);
				}
			}
		}

		if (Config.generateLakes && TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, EventType.LAKE))
		{
			for (int i = 0; i < 50; ++i)
			{
				int x = chunk_X + randomGenerator.nextInt(16) + 8;
				int y = randomGenerator.nextInt(randomGenerator.nextInt(100) + 8);
				int z = chunk_Z + randomGenerator.nextInt(16) + 8;

				(new WorldGenLiquids(Block.waterMoving.blockID)).generate(currentWorld, randomGenerator, x, y, z);
			}

			for (int i = 0; i < 20; ++i)
			{
				int x = chunk_X + randomGenerator.nextInt(16) + 8;
				int y = randomGenerator.nextInt(randomGenerator.nextInt(randomGenerator.nextInt(84) + 8) + 8);
				int z = chunk_Z + randomGenerator.nextInt(16) + 8;

				(new WorldGenLiquids(Block.lavaMoving.blockID)).generate(currentWorld, randomGenerator, x, y, z);
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(currentWorld, randomGenerator, chunk_X, chunk_Z));
	}
}