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
import caveworld.block.CaveBlocks;
import caveworld.util.CaveUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
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
		ChunkCoordinates coord = CaveworldAPI.getLastPos(entity, portalBlock.getType());

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

			if (entity instanceof EntityPlayerMP)
			{
				CaveUtils.setPlayerLocation((EntityPlayerMP)entity, x, y, z);
			}
			else
			{
				entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
			}
		}
		else
		{
			if (entity instanceof EntityPlayerMP)
			{
				CaveUtils.setPlayerLocation((EntityPlayerMP)entity, x, y, z);
			}
			else
			{
				entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
			}
		}
	}
}