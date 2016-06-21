package caveworld.world;

import caveworld.api.CaverAPI;
import caveworld.api.CaveworldAPI;
import caveworld.block.CaveBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;

public class TeleporterCavenia extends TeleporterCaveworld
{
	public TeleporterCavenia(WorldServer worldServer)
	{
		super(worldServer);
		this.portalBlock = CaveBlocks.cavenia_portal;
	}

	public TeleporterCavenia(WorldServer worldServer, boolean brick)
	{
		super(worldServer, brick);
		this.portalBlock = CaveBlocks.cavenia_portal;
	}

	@Override
	public void init(Entity entity)
	{
		int x = 0;
		int y = 0;
		int z = 0;
		ChunkCoordinates coord = CaverAPI.getLastPos(entity, portalBlock.getType());

		if (coord != null)
		{
			x = coord.posX;
			y = coord.posY;
			z = coord.posZ;
		}

		if (CaveworldAPI.isEntityInCavenia(entity))
		{
			if (coord == null)
			{
				x = 50;
				y = 0;
				z = 50;
			}
		}

		setLocationAndAngles(entity, x, y, z);
	}
}