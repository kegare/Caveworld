/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.packet;

import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class CavePacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Caveworld.MODID);

	public static void register()
	{
		int type = 0;

		INSTANCE.registerMessage(ConfigSyncPacket.class, ConfigSyncPacket.class, type++, Side.CLIENT);
		INSTANCE.registerMessage(CaveNotifyPacket.class, CaveNotifyPacket.class, type++, Side.CLIENT);
		INSTANCE.registerMessage(CaveDimSyncPacket.class, CaveDimSyncPacket.class, type++, Side.CLIENT);
		INSTANCE.registerMessage(CaveBiomeSyncPacket.class, CaveBiomeSyncPacket.class, type++, Side.CLIENT);
		INSTANCE.registerMessage(CaveOreSyncPacket.class, CaveOreSyncPacket.class, type++, Side.CLIENT);
		INSTANCE.registerMessage(CaveMiningSyncPacket.class, CaveMiningSyncPacket.class, type++, Side.CLIENT);
		INSTANCE.registerMessage(CaveMiningSyncPacket.class, CaveMiningSyncPacket.class, type++, Side.SERVER);
		INSTANCE.registerMessage(PlayCaveSoundPacket.class, PlayCaveSoundPacket.class, type++, Side.CLIENT);
	}
}