/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.block;

import java.util.Random;

import caveworld.api.CaverAPI;
import caveworld.api.CaveworldAPI;
import caveworld.client.gui.MenuType;
import caveworld.world.TeleporterCavenia;
import caveworld.world.WorldProviderCavenia;
import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockPortalCavenia extends BlockCavePortal
{
	public BlockPortalCavenia(String name)
	{
		super(name);
		this.setBlockTextureName("caveworld:cavenia_portal");
	}

	@Override
	public int getType()
	{
		return WorldProviderCavenia.TYPE;
	}

	@Override
	public MenuType getMenuType()
	{
		return MenuType.CAVENIA_PORTAL;
	}

	@Override
	public boolean isEntityInCave(Entity entity)
	{
		return CaveworldAPI.isEntityInCavenia(entity);
	}

	@Override
	public int getDimension()
	{
		return CaveworldAPI.getCaveniaDimension();
	}

	@Override
	public int getLastDimension(Entity entity)
	{
		return CaverAPI.getCaveniaLastDimension(entity);
	}

	@Override
	public void setLastDimension(Entity entity, int dim)
	{
		CaverAPI.setCaveniaLastDimension(entity, dim);
	}

	@Override
	public Teleporter getTeleporter(WorldServer worldServer, boolean brick)
	{
		return new TeleporterCavenia(worldServer, brick);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {}
}