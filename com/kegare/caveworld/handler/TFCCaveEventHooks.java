/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.handler;

import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;

import com.bioxx.tfc.Core.TFC_Core;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TFCCaveEventHooks
{
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLoad(ChunkEvent.Load event)
	{
		if(!event.world.isRemote)
		{
			TFC_Core.addCDM(event.world);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onUnload(ChunkEvent.Unload event)
	{
		if(!event.world.isRemote)
		{
			TFC_Core.addCDM(event.world);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onDataLoad(ChunkDataEvent.Load event)
	{
		if(!event.world.isRemote)
		{
			TFC_Core.addCDM(event.world);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onDataSave(ChunkDataEvent.Save event)
	{
		if (!event.world.isRemote)
		{
			TFC_Core.addCDM(event.world);
		}
	}
}