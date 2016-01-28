/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world;

import caveworld.block.CaveBlocks;
import net.minecraft.world.WorldServer;

public class TeleporterCavern extends TeleporterCaveworld
{
	public TeleporterCavern(WorldServer worldServer)
	{
		super(worldServer);
		this.portalBlock = CaveBlocks.cavern_portal;
	}

	public TeleporterCavern(WorldServer worldServer, boolean brick)
	{
		super(worldServer, brick);
		this.portalBlock = CaveBlocks.cavern_portal;
	}
}