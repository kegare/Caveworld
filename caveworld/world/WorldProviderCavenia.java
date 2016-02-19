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
import caveworld.core.CaveNetworkRegistry;
import caveworld.network.client.CaveAdjustMessage;
import caveworld.network.client.CaveMusicMessage;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderCavenia extends WorldProviderCaveworld
{
	public static final int TYPE = 4;
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler("Cavenia");

	public WorldProviderCavenia()
	{
		this.dimensionId = CaveworldAPI.getCaveniaDimension();
		this.hasNoSky = true;
	}

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.deepOcean, 0.0F);
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderCavenia(worldObj);
	}

	@Override
	public String getDimensionName()
	{
		return "Cavenia";
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
		return 100;
	}

	@Override
	public void calculateInitialWeather()
	{
		if (!worldObj.isRemote)
		{
			musicTime = 300;
		}

		worldObj.prevRainingStrength = 0.0F;
		worldObj.rainingStrength = 0.0F;
		worldObj.prevThunderingStrength = 0.0F;
		worldObj.thunderingStrength = 0.0F;
	}

	@Override
	public void updateWeather()
	{
		if (!worldObj.isRemote)
		{
			if (--musicTime <= 0)
			{
				musicTime = 300;

				CaveNetworkRegistry.sendToDimension(new CaveMusicMessage("cavemusic.battle" + (worldObj.rand.nextInt(2) + 1), false), dimensionId);
			}
		}

		worldObj.prevRainingStrength = 0.0F;
		worldObj.rainingStrength = 0.0F;
		worldObj.prevThunderingStrength = 0.0F;
		worldObj.thunderingStrength = 0.0F;
	}
}