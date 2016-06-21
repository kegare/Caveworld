package caveworld.world;

import caveworld.api.CaveworldAPI;
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.client.CaveAdjustMessage;
import caveworld.network.client.CaveMusicMessage;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderCaveland extends WorldProviderCaveworld
{
	public static final String NAME = "Caveland";
	public static final int TYPE = 3;
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler(NAME);

	public WorldProviderCaveland()
	{
		this.dimensionId = CaveworldAPI.getCavelandDimension();
		this.hasNoSky = true;

		saveHandler.setDimension(dimensionId);
	}

	@Override
	public float getBrightness()
	{
		return ChunkProviderCaveland.caveBrightness;
	}

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManager(worldObj);
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderCaveland(worldObj);
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

				CaveNetworkRegistry.sendToDimension(new CaveMusicMessage("cavemusic.hope"), dimensionId);
			}
		}

		worldObj.prevRainingStrength = 0.0F;
		worldObj.rainingStrength = 0.0F;
		worldObj.prevThunderingStrength = 0.0F;
		worldObj.thunderingStrength = 0.0F;
	}
}