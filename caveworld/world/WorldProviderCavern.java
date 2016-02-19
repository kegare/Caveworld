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
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderCavern extends WorldProviderCaveworld
{
	public static final int TYPE = 1;
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler("Cavern");

	public WorldProviderCavern()
	{
		this.dimensionId = CaveworldAPI.getCavernDimension();
		this.hasNoSky = true;
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
		return "Cavern";
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
}