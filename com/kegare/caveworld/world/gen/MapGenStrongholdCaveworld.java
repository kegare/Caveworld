/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.world.gen;

import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

import com.google.common.collect.Lists;
import com.kegare.caveworld.world.gen.StructureStrongholdPiecesCaveworld.Stairs2;

public class MapGenStrongholdCaveworld extends MapGenStronghold
{
	private ChunkCoordIntPair[] structureCoords;
	private double structureDistance;
	private int structureSpread;

	private boolean initStructureCoords;

	public MapGenStrongholdCaveworld()
	{
		this.structureCoords = new ChunkCoordIntPair[5];
		this.structureDistance = 32.0D;
		this.structureSpread = 1;
	}

	@Override
	public String func_143025_a()
	{
		return "Caveworld.Stronghold";
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
	{
		if (!initStructureCoords)
		{
			Random random = new Random();
			random.setSeed(worldObj.getSeed());
			double var1 = random.nextDouble() * Math.PI * 2.0D;
			int var2 = 1;

			for (int i = 0; i < structureCoords.length; ++i)
			{
				double var3 = (1.3D * var2 + random.nextDouble()) * structureDistance * var2;
				int x = (int)Math.round(Math.cos(var1) * var3);
				int z = (int)Math.round(Math.sin(var1) * var3);

				structureCoords[i] = new ChunkCoordIntPair(x, z);
				var1 += Math.PI * 2D * var2 / structureSpread;

				if (i == structureSpread)
				{
					var2 += 2 + random.nextInt(5);
					structureSpread += 1 + random.nextInt(2);
				}
			}

			initStructureCoords = true;
		}

		for (ChunkCoordIntPair coord : structureCoords)
		{
			if (chunkX == coord.chunkXPos && chunkZ == coord.chunkZPos)
			{
				return true;
			}
		}

		return false;
	}

	@Override
	protected List getCoordList()
	{
		List list = Lists.newArrayList();

		for (ChunkCoordIntPair coord : structureCoords)
		{
			if (coord != null)
			{
				list.add(coord.func_151349_a(64));
			}
		}

		return list;
	}

	@Override
	protected StructureStart getStructureStart(int chunkX, int chunkZ)
	{
		Start start;

		for (start = new Start(worldObj, rand, chunkX, chunkZ); start.getComponents().isEmpty() || ((Stairs2)start.getComponents().get(0)).strongholdPortalRoom == null; start = new Start(worldObj, rand, chunkX, chunkZ))
		{
			;
		}

		return start;
	}

	public static class Start extends StructureStart
	{
		public Start() {}

		public Start(World world, Random random, int chunkX, int chunkZ)
		{
			super(chunkX, chunkZ);
			StructureStrongholdPiecesCaveworld.prepareStructurePieces();
			Stairs2 stairs2 = new Stairs2(0, random, chunkX * 16 + 2, chunkZ * 16 + 2);
			this.components.add(stairs2);
			stairs2.buildComponent(stairs2, components, random);
			List list = stairs2.field_75026_c;

			while (!list.isEmpty())
			{
				StructureComponent structureComponent = (StructureComponent)list.remove(random.nextInt(list.size()));

				structureComponent.buildComponent(stairs2, components, random);
			}

			this.updateBoundingBox();
			this.markAvailableHeight(world, random, 10);
		}
	}
}