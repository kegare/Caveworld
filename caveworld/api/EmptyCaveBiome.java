package caveworld.api;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;

public class EmptyCaveBiome implements ICaveBiome
{
	private final BiomeGenBase biome;

	public EmptyCaveBiome()
	{
		this.biome = BiomeGenBase.plains;
	}

	public EmptyCaveBiome(BiomeGenBase biome)
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

	@Override
	public NBTTagCompound saveNBTData()
	{
		return new NBTTagCompound();
	}

	@Override
	public void loadNBTData(NBTTagCompound data) {}
}