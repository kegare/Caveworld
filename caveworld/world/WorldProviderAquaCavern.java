/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveBiomeManager;
import caveworld.core.CaveNetworkRegistry;
import caveworld.network.client.CaveAdjustMessage;
import caveworld.network.client.CaveMusicMessage;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderAquaCavern extends WorldProviderCaveworld
{
	public static final int TYPE = 2;
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler("Aqua Cavern");

	public WorldProviderAquaCavern()
	{
		this.dimensionId = CaveworldAPI.getAquaCavernDimension();
		this.hasNoSky = true;
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
		return "Aqua Cavern";
	}

	@Override
	public long getSeed()
	{
		if (!worldObj.isRemote && saveHandler.getRawData() == null)
		{
			CaveNetworkRegistry.sendToAll(new CaveAdjustMessage(TYPE, dimensionId, saveHandler));
		}

		return saveHandler.getWorldSeed();
	}

	@Override
	public int getActualHeight()
	{
		if (!worldObj.isRemote && saveHandler.getRawData() == null)
		{
			CaveNetworkRegistry.sendToAll(new CaveAdjustMessage(TYPE, dimensionId, saveHandler));
		}

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