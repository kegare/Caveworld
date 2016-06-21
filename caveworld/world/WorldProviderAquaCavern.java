package caveworld.world;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveBiomeManager;
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.client.CaveAdjustMessage;
import caveworld.network.client.CaveMusicMessage;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderAquaCavern extends WorldProviderCaveworld
{
	public static final String NAME = "Aqua Cavern";
	public static final int TYPE = 2;
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler(NAME);

	public WorldProviderAquaCavern()
	{
		this.dimensionId = CaveworldAPI.getAquaCavernDimension();
		this.hasNoSky = true;

		saveHandler.setDimension(dimensionId);
	}

	@Override
	public float getBrightness()
	{
		return ChunkProviderAquaCavern.caveBrightness;
	}

	@Override
	public ICaveBiomeManager getBiomeManager()
	{
		return CaveworldAPI.biomeAquaCavernManager;
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderAquaCavern(worldObj);
	}

	@Override
	public String getDimensionName()
	{
		return NAME;
	}

	@Override
	public void adjustData()
	{
		if (!worldObj.isRemote && saveHandler.getRawData() == null)
		{
			saveHandler.getData();

			CaveNetworkRegistry.sendToAll(new CaveAdjustMessage(TYPE, saveHandler));
		}
	}

	@Override
	public long getSeed()
	{
		adjustData();

		return saveHandler.getWorldSeed();
	}

	@Override
	public int getActualHeight()
	{
		adjustData();

		return saveHandler.getSubsurfaceHeight() + 1;
	}

	@Override
	public void updateWeather()
	{
		if (!worldObj.isRemote)
		{
			if (--musicTime <= 0)
			{
				musicTime = worldObj.rand.nextInt(5000) + 10000;

				CaveNetworkRegistry.sendToDimension(new CaveMusicMessage("cavemusic.aqua"), dimensionId);
			}
		}

		worldObj.prevRainingStrength = 0.0F;
		worldObj.rainingStrength = 0.0F;
		worldObj.prevThunderingStrength = 0.0F;
		worldObj.thunderingStrength = 0.0F;
	}
}