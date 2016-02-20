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
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.client.CaveAdjustMessage;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderCavern extends WorldProviderCaveworld
{
	public static final String NAME = "Cavern";
	public static final int TYPE = 1;
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler(NAME);

	public WorldProviderCavern()
	{
		this.dimensionId = CaveworldAPI.getCavernDimension();
		this.hasNoSky = true;

		saveHandler.setDimension(dimensionId);
	}

	@Override
	public ICaveBiomeManager getBiomeManager()
	{
		return CaveworldAPI.biomeCavernManager;
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderCavern(worldObj);
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
}