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
import caveworld.world.TeleporterCavern;
import caveworld.world.WorldProviderCavern;
import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class BlockPortalCavern extends BlockCavePortal implements IBlockPortal
{
	public BlockPortalCavern(String name)
	{
		super(name);
		this.setBlockTextureName("caveworld:caveworld_portal");
	}

	@Override
	public int getType()
	{
		return WorldProviderCavern.TYPE;
	}

	@Override
	public MenuType getMenuType()
	{
		return MenuType.CAVERN_PORTAL;
	}

	@Override
	public boolean isEntityInCave(Entity entity)
	{
		return CaveworldAPI.isEntityInCavern(entity);
	}

	@Override
	public int getDimension()
	{
		return CaveworldAPI.getCavernDimension();
	}

	@Override
	public int getLastDimension(Entity entity)
	{
		return CaveworldAPI.getCavernLastDimension(entity);
	}

	@Override
	public void setLastDimension(Entity entity, int dim)
	{
		CaveworldAPI.setCavernLastDimension(entity, dim);
	}

	@Override
	public Teleporter getTeleporter(WorldServer worldServer, boolean brick)
	{
		return new TeleporterCavern(worldServer, brick);
	}
}