package caveworld.world;

import caveworld.block.CaveBlocks;
import net.minecraft.world.WorldServer;

public class TeleporterCaveland extends TeleporterCaveworld
{
	public TeleporterCaveland(WorldServer worldServer)
	{
		super(worldServer);
		this.portalBlock = CaveBlocks.caveland_portal;
	}

	public TeleporterCaveland(WorldServer worldServer, boolean brick)
	{
		super(worldServer, brick);
		this.portalBlock = CaveBlocks.caveland_portal;
	}
}