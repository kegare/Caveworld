package caveworld.world;

import caveworld.block.CaveBlocks;
import net.minecraft.world.WorldServer;

public class TeleporterAquaCavern extends TeleporterCaveworld
{
	public TeleporterAquaCavern(WorldServer worldServer)
	{
		super(worldServer);
		this.portalBlock = CaveBlocks.aqua_cavern_portal;
	}

	public TeleporterAquaCavern(WorldServer worldServer, boolean brick)
	{
		super(worldServer, brick);
		this.portalBlock = CaveBlocks.aqua_cavern_portal;
	}
}