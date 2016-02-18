/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.block;

import caveworld.api.CaveworldAPI;
import caveworld.client.gui.MenuType;
import caveworld.world.TeleporterAquaCavern;
import caveworld.world.WorldProviderAquaCavern;
import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class BlockPortalAquaCavern extends BlockCavePortal
{
	public BlockPortalAquaCavern(String name)
	{
		super(name);
		this.setBlockTextureName("caveworld:aqua_cavern_portal");
	}

	@Override
	public int getType()
	{
		return WorldProviderAquaCavern.TYPE;
	}

	@Override
	public MenuType getMenuType()
	{
		return MenuType.AQUA_CAVERN_PORTAL;
	}

	@Override
	public boolean isEntityInCave(Entity entity)
	{
		return CaveworldAPI.isEntityInAquaCavern(entity);
	}

	@Override
	public int getDimension()
	{
		return CaveworldAPI.getAquaCavernDimension();
	}

	@Override
	public int getLastDimension(Entity entity)
	{
		return CaveworldAPI.getAquaCavernLastDimension(entity);
	}

	@Override
	public void setLastDimension(Entity entity, int dim)
	{
		CaveworldAPI.setAquaCavernLastDimension(entity, dim);
	}

	@Override
	public Teleporter getTeleporter(WorldServer worldServer, boolean brick)
	{
		return new TeleporterAquaCavern(worldServer, brick);
	}
}