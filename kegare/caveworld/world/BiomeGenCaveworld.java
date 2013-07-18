package kegare.caveworld.world;

import net.minecraft.block.Block;
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
		this.setTemperatureRainfall(0.2F, 0.0F);
		this.setDisableRain();
		this.topBlock = (byte)Block.stone.blockID;
		this.fillerBlock = (byte)Block.stone.blockID;
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