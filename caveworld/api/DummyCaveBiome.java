package caveworld.api;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class DummyCaveBiome implements ICaveBiome
{
	private final BiomeGenBase biome;

	public DummyCaveBiome()
	{
		this.biome = BiomeGenBase.plains;
	}

	public DummyCaveBiome(BiomeGenBase biome)
	{
		this.biome = biome;
	}

	@Override
	public BiomeGenBase getBiome()
	{
		return biome;
	}

	@Override
	public int setGenWeight(int weight)
	{
		return getGenWeight();
	}

	@Override
	public int getGenWeight()
	{
		return 0;
	}

	@Override
	public BlockEntry setTerrainBlock(BlockEntry entry)
	{
		return getTerrainBlock();
	}

	@Override
	public BlockEntry getTerrainBlock()
	{
		return new BlockEntry(Blocks.stone, 0);
	}

	@Override
	public BlockEntry setTopBlock(BlockEntry entry)
	{
		return getTopBlock();
	}

	@Override
	public BlockEntry getTopBlock()
	{
		return getTerrainBlock();
	}
}