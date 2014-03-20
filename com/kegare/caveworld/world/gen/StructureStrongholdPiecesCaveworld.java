/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.world.gen;

import static net.minecraftforge.common.ChestGenHooks.*;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureComponent.BlockSelector;
import net.minecraft.world.gen.structure.StructureStrongholdPieces.Stronghold.Door;
import net.minecraftforge.common.ChestGenHooks;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class StructureStrongholdPiecesCaveworld
{
	private static final PieceWeight[] pieceWeightArray = new PieceWeight[]
	{
		new PieceWeight(PortalRoom.class, 20, 1)
		{
			@Override
			public boolean canSpawnMoreStructuresOfType(int type)
			{
				return super.canSpawnMoreStructuresOfType(type) && type > 5;
			}
		},
		new PieceWeight(Library.class, 10, 3)
		{
			@Override
			public boolean canSpawnMoreStructuresOfType(int type)
			{
				return super.canSpawnMoreStructuresOfType(type) && type > 4;
			}
		},
		new PieceWeight(Straight.class, 40, 0),
		new PieceWeight(LeftTurn.class, 20, 0),
		new PieceWeight(RightTurn.class, 20, 0),
		new PieceWeight(RoomCrossing.class, 10, 6),
		new PieceWeight(Crossing.class, 5, 4),
		new PieceWeight(Stairs.class, 5, 5),
		new PieceWeight(StairsStraight.class, 5, 5),
		new PieceWeight(ChestCorridor.class, 6, 6),
		new PieceWeight(Prison.class, 5, 5)
	};

	private static final LinkedHashSet<PieceWeight> structurePieces = Sets.newLinkedHashSet();

	private static Class strongholdComponentType;
	private static int totalWeight;

	private static final Stones strongholdStones = new Stones();

	public static void registerStrongholdPieces()
	{
		MapGenStructureIO.func_143031_a(Stairs2.class, "CW:SHStart");
		MapGenStructureIO.func_143031_a(PortalRoom.class, "CW:SHPR");
		MapGenStructureIO.func_143031_a(Library.class, "CW:SHLi");
		MapGenStructureIO.func_143031_a(Straight.class, "CW:SHS");
		MapGenStructureIO.func_143031_a(LeftTurn.class, "CW:SHLT");
		MapGenStructureIO.func_143031_a(RightTurn.class, "CW:SHRT");
		MapGenStructureIO.func_143031_a(RoomCrossing.class, "CW:SHRC");
		MapGenStructureIO.func_143031_a(Crossing.class, "CW:SH5C");
		MapGenStructureIO.func_143031_a(Stairs.class, "CW:SHSD");
		MapGenStructureIO.func_143031_a(StairsStraight.class, "CW:SHSSD");
		MapGenStructureIO.func_143031_a(Corridor.class, "CW:SHFC");
		MapGenStructureIO.func_143031_a(ChestCorridor.class, "CW:SHCC");
		MapGenStructureIO.func_143031_a(Prison.class, "CW:SHPH");
	}

	public static void prepareStructurePieces()
	{
		structurePieces.clear();

		for (PieceWeight pieceWeight : pieceWeightArray)
		{
			pieceWeight.instancesSpawned = 0;
			structurePieces.add(pieceWeight);
		}

		strongholdComponentType = null;
	}

	private static boolean canAddStructurePieces()
	{
		totalWeight = 0;

		PieceWeight pieceWeight;
		boolean flag = false;

		for (Iterator<PieceWeight> iterator = structurePieces.iterator(); iterator.hasNext(); totalWeight += pieceWeight.pieceWeight)
		{
			pieceWeight = iterator.next();

			if (pieceWeight.instancesLimit > 0 && pieceWeight.instancesSpawned < pieceWeight.instancesLimit)
			{
				flag = true;
			}
		}

		return flag;
	}

	private static Stronghold getStrongholdComponentFromWeightedPiece(Class clazz, List list, Random random, int x, int y, int z, int mode, int type)
	{
		Object object = null;

		if (clazz == PortalRoom.class)
		{
			object = PortalRoom.findValidPlacement(list, random, x, y, z, mode, type);
		}
		else if (clazz == Library.class)
		{
			object = Library.findValidPlacement(list, random, x, y, z, mode, type);
		}
		else if (clazz == Straight.class)
		{
			object = Straight.findValidPlacement(list, random, x, y, z, mode, type);
		}
		else if (clazz == LeftTurn.class)
		{
			object = LeftTurn.findValidPlacement(list, random, x, y, z, mode, type);
		}
		else if (clazz == RightTurn.class)
		{
			object = RightTurn.findValidPlacement(list, random, x, y, z, mode, type);
		}
		else if (clazz == RoomCrossing.class)
		{
			object = RoomCrossing.findValidPlacement(list, random, x, y, z, mode, type);
		}
		else if (clazz == Crossing.class)
		{
			object = Crossing.findValidPlacement(list, random, x, y, z, mode, type);
		}
		else if (clazz == Stairs.class)
		{
			object = Stairs.getStrongholdStairsComponent(list, random, x, y, z, mode, type);
		}
		else if (clazz == StairsStraight.class)
		{
			object = StairsStraight.findValidPlacement(list, random, x, y, z, mode, type);
		}
		else if (clazz == ChestCorridor.class)
		{
			object = ChestCorridor.findValidPlacement(list, random, x, y, z, mode, type);
		}
		else if (clazz == Prison.class)
		{
			object = Prison.findValidPlacement(list, random, x, y, z, mode, type);
		}

		return (Stronghold)object;
	}

	private static Stronghold getNextComponent(Stairs2 stairs2, List list, Random random, int x, int y, int z, int mode, int type)
	{
		if (!canAddStructurePieces())
		{
			return null;
		}
		else
		{
			if (strongholdComponentType != null)
			{
				Stronghold stronghold = getStrongholdComponentFromWeightedPiece(strongholdComponentType, list, random, x, y, z, mode, type);
				strongholdComponentType = null;

				if (stronghold != null)
				{
					return stronghold;
				}
			}

			for (int i = 0; i < 5; ++i)
			{
				int weight = random.nextInt(totalWeight);

				for (PieceWeight pieceWeight : structurePieces)
				{
					weight -= pieceWeight.pieceWeight;

					if (weight < 0)
					{
						if (!pieceWeight.canSpawnMoreStructuresOfType(type) || pieceWeight == stairs2.strongholdPieceWeight)
						{
							break;
						}

						Stronghold stronghold1 = getStrongholdComponentFromWeightedPiece(pieceWeight.pieceClass, list, random, x, y, z, mode, type);

						if (stronghold1 != null)
						{
							++pieceWeight.instancesSpawned;
							stairs2.strongholdPieceWeight = pieceWeight;

							if (!pieceWeight.canSpawnMoreStructures())
							{
								structurePieces.remove(pieceWeight);
							}

							return stronghold1;
						}
					}
				}
			}

			StructureBoundingBox structureBoundingBox = Corridor.func_74992_a(list, random, x, y, z, mode);

			if (structureBoundingBox != null && structureBoundingBox.minY > 1)
			{
				return new Corridor(type, random, structureBoundingBox, mode);
			}
			else
			{
				return null;
			}
		}
	}

	private static StructureComponent getNextValidComponent(Stairs2 stairs2, List list, Random random, int x, int y, int z, int mode, int type)
	{
		if (type > 50)
		{
			return null;
		}
		else if (Math.abs(x - stairs2.getBoundingBox().minX) <= 112 && Math.abs(z - stairs2.getBoundingBox().minZ) <= 112)
		{
			Stronghold stronghold = getNextComponent(stairs2, list, random, x, y, z, mode, type + 1);

			if (stronghold != null)
			{
				list.add(stronghold);
				stairs2.field_75026_c.add(stronghold);
			}

			return stronghold;
		}
		else
		{
			return null;
		}
	}

	public static class Stairs extends Stronghold
	{
		private boolean field_75024_a;

		public Stairs() {}

		public Stairs(int type, Random random, int par3, int par4)
		{
			super(type);
			this.field_75024_a = true;
			this.coordBaseMode = random.nextInt(4);
			this.door = Door.OPENING;

			switch (coordBaseMode)
			{
				case 0:
				case 2:
					this.boundingBox = new StructureBoundingBox(par3, 64, par4, par3 + 5 - 1, 74, par4 + 5 - 1);

					break;
				default:
					this.boundingBox = new StructureBoundingBox(par3, 64, par4, par3 + 5 - 1, 74, par4 + 5 - 1);
			}
		}

		public Stairs(int type, Random random, StructureBoundingBox structureBoundingBox, int mode)
		{
			super(type);
			this.field_75024_a = false;
			this.coordBaseMode = mode;
			this.door = getRandomDoor(random);
			this.boundingBox = structureBoundingBox;
		}

		@Override
		protected void func_143012_a(NBTTagCompound nbtTagCompound)
		{
			super.func_143012_a(nbtTagCompound);
			nbtTagCompound.setBoolean("Source", field_75024_a);
		}

		@Override
		protected void func_143011_b(NBTTagCompound nbtTagCompound)
		{
			super.func_143011_b(nbtTagCompound);
			field_75024_a = nbtTagCompound.getBoolean("Source");
		}

		@Override
		public void buildComponent(StructureComponent structureComponent, List list, Random random)
		{
			if (field_75024_a)
			{
				strongholdComponentType = Crossing.class;
			}

			getNextComponentNormal((Stairs2)structureComponent, list, random, 1, 1);
		}

		public static Stairs getStrongholdStairsComponent(List list, Random random, int x, int y, int z, int mode, int type)
		{
			StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -7, 0, 5, 11, 5, mode);

			return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null ? new Stairs(type, random, structureBoundingBox, mode) : null;
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(world, structureBoundingBox))
			{
				return false;
			}
			else
			{
				fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 4, 10, 4, true, random, strongholdStones);
				placeDoor(world, random, structureBoundingBox, door, 1, 7, 0);
				placeDoor(world, random, structureBoundingBox, Door.OPENING, 1, 1, 4);
				placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 6, 1, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 5, 1, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 1, 6, 1, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 5, 2, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 4, 3, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 1, 5, 3, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 4, 3, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 3, 3, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 3, 4, 3, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 3, 2, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 2, 1, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 3, 3, 1, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 2, 1, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 1, 1, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 1, 2, 1, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 1, 2, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 1, 1, 3, structureBoundingBox);

				return true;
			}
		}
	}

	public static class Straight extends Stronghold
	{
		private boolean expandsX;
		private boolean expandsZ;

		public Straight() {}

		public Straight(int type, Random random, StructureBoundingBox structureBoundingBox, int mode)
		{
			super(type);
			this.coordBaseMode = mode;
			this.door = getRandomDoor(random);
			this.boundingBox = structureBoundingBox;
			this.expandsX = random.nextInt(2) == 0;
			this.expandsZ = random.nextInt(2) == 0;
		}

		@Override
		protected void func_143012_a(NBTTagCompound nbtTagCompound)
		{
			super.func_143012_a(nbtTagCompound);
			nbtTagCompound.setBoolean("Left", expandsX);
			nbtTagCompound.setBoolean("Right", expandsZ);
		}

		@Override
		protected void func_143011_b(NBTTagCompound nbtTagCompound)
		{
			super.func_143011_b(nbtTagCompound);
			expandsX = nbtTagCompound.getBoolean("Left");
			expandsZ = nbtTagCompound.getBoolean("Right");
		}

		@Override
		public void buildComponent(StructureComponent structureComponent, List list, Random random)
		{
			getNextComponentNormal((Stairs2) structureComponent, list, random, 1, 1);

			if (expandsX)
			{
				getNextComponentX((Stairs2)structureComponent, list, random, 1, 2);
			}

			if (expandsZ)
			{
				getNextComponentZ((Stairs2)structureComponent, list, random, 1, 2);
			}
		}

		public static Straight findValidPlacement(List list, Random random, int x, int y, int z, int mode, int type)
		{
			StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 7, mode);

			return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(list, structureboundingbox) == null ? new Straight(type, random, structureboundingbox, mode) : null;
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(world, structureBoundingBox))
			{
				return false;
			}
			else
			{
				fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 4, 4, 6, true, random, strongholdStones);
				placeDoor(world, random, structureBoundingBox, door, 1, 1, 0);
				placeDoor(world, random, structureBoundingBox, Door.OPENING, 1, 1, 6);
				func_151552_a(world, structureBoundingBox, random, 0.1F, 1, 2, 1, Blocks.torch, 0);
				func_151552_a(world, structureBoundingBox, random, 0.1F, 3, 2, 1, Blocks.torch, 0);
				func_151552_a(world, structureBoundingBox, random, 0.1F, 1, 2, 5, Blocks.torch, 0);
				func_151552_a(world, structureBoundingBox, random, 0.1F, 3, 2, 5, Blocks.torch, 0);

				if (expandsX)
				{
					fillWithBlocks(world, structureBoundingBox, 0, 1, 2, 0, 3, 4, Blocks.air, Blocks.air, false);
				}

				if (expandsZ)
				{
					fillWithBlocks(world, structureBoundingBox, 4, 1, 2, 4, 3, 4, Blocks.air, Blocks.air, false);
				}

				return true;
			}
		}
	}

	public static class Library extends Stronghold
	{
		private boolean isLargeRoom;

		public Library() {}

		public Library(int type, Random random, StructureBoundingBox structureBoundingBox, int mode)
		{
			super(type);
			this.coordBaseMode = mode;
			this.door = getRandomDoor(random);
			this.boundingBox = structureBoundingBox;
			this.isLargeRoom = structureBoundingBox.getYSize() > 6;
		}

		@Override
		protected void func_143012_a(NBTTagCompound nbtTagCompound)
		{
			super.func_143012_a(nbtTagCompound);
			nbtTagCompound.setBoolean("Tall", isLargeRoom);
		}

		@Override
		protected void func_143011_b(NBTTagCompound nbtTagCompound)
		{
			super.func_143011_b(nbtTagCompound);
			isLargeRoom = nbtTagCompound.getBoolean("Tall");
		}

		public static Library findValidPlacement(List list, Random random, int x, int y, int z, int mode, int type)
		{
			StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 14, 11, 15, mode);

			if (!canStrongholdGoDeeper(structureBoundingBox) || StructureComponent.findIntersecting(list, structureBoundingBox) != null)
			{
				structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 14, 6, 15, mode);

				if (!canStrongholdGoDeeper(structureBoundingBox) || StructureComponent.findIntersecting(list, structureBoundingBox) != null)
				{
					return null;
				}
			}

			return new Library(type, random, structureBoundingBox, mode);
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(world, structureBoundingBox))
			{
				return false;
			}
			else
			{
				byte var1 = 11;

				if (!isLargeRoom)
				{
					var1 = 6;
				}

				fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 13, var1 - 1, 14, true, random, strongholdStones);
				placeDoor(world, random, structureBoundingBox, door, 4, 1, 0);
				randomlyFillWithBlocks(world, structureBoundingBox, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.web, Blocks.web, false);

				int i;

				for (i = 1; i <= 13; ++i)
				{
					if ((i - 1) % 4 == 0)
					{
						fillWithBlocks(world, structureBoundingBox, 1, 1, i, 1, 4, i, Blocks.planks, Blocks.planks, false);
						fillWithBlocks(world, structureBoundingBox, 12, 1, i, 12, 4, i, Blocks.planks, Blocks.planks, false);
						placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 3, i, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.torch, 0, 11, 3, i, structureBoundingBox);

						if (isLargeRoom)
						{
							fillWithBlocks(world, structureBoundingBox, 1, 6, i, 1, 9, i, Blocks.planks, Blocks.planks, false);
							fillWithBlocks(world, structureBoundingBox, 12, 6, i, 12, 9, i, Blocks.planks, Blocks.planks, false);
						}
					}
					else
					{
						fillWithBlocks(world, structureBoundingBox, 1, 1, i, 1, 4, i, Blocks.bookshelf, Blocks.bookshelf, false);
						fillWithBlocks(world, structureBoundingBox, 12, 1, i, 12, 4, i, Blocks.bookshelf, Blocks.bookshelf, false);

						if (isLargeRoom)
						{
							fillWithBlocks(world, structureBoundingBox, 1, 6, i, 1, 9, i, Blocks.bookshelf, Blocks.bookshelf, false);
							fillWithBlocks(world, structureBoundingBox, 12, 6, i, 12, 9, i, Blocks.bookshelf, Blocks.bookshelf, false);
						}
					}
				}

				for (i = 3; i < 12; i += 2)
				{
					fillWithBlocks(world, structureBoundingBox, 3, 1, i, 4, 3, i, Blocks.bookshelf, Blocks.bookshelf, false);
					fillWithBlocks(world, structureBoundingBox, 6, 1, i, 7, 3, i, Blocks.bookshelf, Blocks.bookshelf, false);
					fillWithBlocks(world, structureBoundingBox, 9, 1, i, 10, 3, i, Blocks.bookshelf, Blocks.bookshelf, false);
				}

				if (isLargeRoom)
				{
					fillWithBlocks(world, structureBoundingBox, 1, 5, 1, 3, 5, 13, Blocks.planks, Blocks.planks, false);
					fillWithBlocks(world, structureBoundingBox, 10, 5, 1, 12, 5, 13, Blocks.planks, Blocks.planks, false);
					fillWithBlocks(world, structureBoundingBox, 4, 5, 1, 9, 5, 2, Blocks.planks, Blocks.planks, false);
					fillWithBlocks(world, structureBoundingBox, 4, 5, 12, 9, 5, 13, Blocks.planks, Blocks.planks, false);
					placeBlockAtCurrentPosition(world, Blocks.planks, 0, 9, 5, 11, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.planks, 0, 8, 5, 11, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.planks, 0, 9, 5, 10, structureBoundingBox);
					fillWithBlocks(world, structureBoundingBox, 3, 6, 2, 3, 6, 12, Blocks.fence, Blocks.fence, false);
					fillWithBlocks(world, structureBoundingBox, 10, 6, 2, 10, 6, 10, Blocks.fence, Blocks.fence, false);
					fillWithBlocks(world, structureBoundingBox, 4, 6, 2, 9, 6, 2, Blocks.fence, Blocks.fence, false);
					fillWithBlocks(world, structureBoundingBox, 4, 6, 12, 8, 6, 12, Blocks.fence, Blocks.fence, false);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, 9, 6, 11, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, 8, 6, 11, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, 9, 6, 10, structureBoundingBox);
					i = getMetadataWithOffset(Blocks.ladder, 3);
					placeBlockAtCurrentPosition(world, Blocks.ladder, i, 10, 1, 13, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.ladder, i, 10, 2, 13, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.ladder, i, 10, 3, 13, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.ladder, i, 10, 4, 13, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.ladder, i, 10, 5, 13, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.ladder, i, 10, 6, 13, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.ladder, i, 10, 7, 13, structureBoundingBox);
					byte var2 = 7;
					byte var3 = 7;
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, var2 - 1, 9, var3, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, var2, 9, var3, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, var2 - 1, 8, var3, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, var2, 8, var3, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, var2 - 1, 7, var3, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, var2, 7, var3, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, var2 - 2, 7, var3, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, var2 + 1, 7, var3, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, var2 - 1, 7, var3 - 1, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, var2 - 1, 7, var3 + 1, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, var2, 7, var3 - 1, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.fence, 0, var2, 7, var3 + 1, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.torch, 0, var2 - 2, 8, var3, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.torch, 0, var2 + 1, 8, var3, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.torch, 0, var2 - 1, 8, var3 - 1, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.torch, 0, var2 - 1, 8, var3 + 1, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.torch, 0, var2, 8, var3 - 1, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.torch, 0, var2, 8, var3 + 1, structureBoundingBox);
				}

				ChestGenHooks info = ChestGenHooks.getInfo(STRONGHOLD_LIBRARY);

				generateStructureChestContents(world, structureBoundingBox, random, 3, 3, 5, info.getItems(random), info.getCount(random));

				if (isLargeRoom)
				{
					placeBlockAtCurrentPosition(world, Blocks.air, 0, 12, 9, 1, structureBoundingBox);
					generateStructureChestContents(world, structureBoundingBox, random, 12, 8, 1, info.getItems(random), info.getCount(random));
				}

				return true;
			}
		}
	}

	public static class PortalRoom extends Stronghold
	{
		private boolean hasSpawner;

		public PortalRoom() {}

		public PortalRoom(int type, Random random, StructureBoundingBox structureBoundingBox, int mode)
		{
			super(type);
			this.coordBaseMode = mode;
			this.boundingBox = structureBoundingBox;
		}

		@Override
		protected void func_143012_a(NBTTagCompound nbtTagCompound)
		{
			super.func_143012_a(nbtTagCompound);
			nbtTagCompound.setBoolean("Mob", hasSpawner);
		}

		@Override
		protected void func_143011_b(NBTTagCompound nbtTagCompound)
		{
			super.func_143011_b(nbtTagCompound);
			hasSpawner = nbtTagCompound.getBoolean("Mob");
		}

		@Override
		public void buildComponent(StructureComponent structureComponent, List list, Random random)
		{
			if (structureComponent != null)
			{
				((Stairs2)structureComponent).strongholdPortalRoom = this;
			}
		}

		public static PortalRoom findValidPlacement(List list, Random random, int x, int y, int z, int mode, int type)
		{
			StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 11, 8, 16, mode);

			return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null ? new PortalRoom(type, random, structureBoundingBox, mode) : null;
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox)
		{
			fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 10, 7, 15, false, random, strongholdStones);
			placeDoor(world, random, structureBoundingBox, Door.GRATES, 4, 1, 0);
			fillWithRandomizedBlocks(world, structureBoundingBox, 1, 6, 1, 1, 6, 14, false, random, strongholdStones);
			fillWithRandomizedBlocks(world, structureBoundingBox, 9, 6, 1, 9, 6, 14, false, random, strongholdStones);
			fillWithRandomizedBlocks(world, structureBoundingBox, 2, 6, 1, 8, 6, 2, false, random, strongholdStones);
			fillWithRandomizedBlocks(world, structureBoundingBox, 2, 6, 14, 8, 6, 14, false, random, strongholdStones);
			fillWithRandomizedBlocks(world, structureBoundingBox, 1, 1, 1, 2, 1, 4, false, random, strongholdStones);
			fillWithRandomizedBlocks(world, structureBoundingBox, 8, 1, 1, 9, 1, 4, false, random, strongholdStones);
			fillWithBlocks(world, structureBoundingBox, 1, 1, 1, 1, 1, 3, Blocks.flowing_lava, Blocks.flowing_lava, false);
			fillWithBlocks(world, structureBoundingBox, 9, 1, 1, 9, 1, 3, Blocks.flowing_lava, Blocks.flowing_lava, false);
			fillWithRandomizedBlocks(world, structureBoundingBox, 3, 1, 8, 7, 1, 12, false, random, strongholdStones);
			fillWithBlocks(world, structureBoundingBox, 4, 1, 9, 6, 1, 11, Blocks.flowing_lava, Blocks.flowing_lava, false);

			int i;

			for (i = 3; i < 14; i += 2)
			{
				fillWithBlocks(world, structureBoundingBox, 0, 3, i, 0, 4, i, Blocks.iron_bars, Blocks.iron_bars, false);
				fillWithBlocks(world, structureBoundingBox, 10, 3, i, 10, 4, i, Blocks.iron_bars, Blocks.iron_bars, false);
			}

			for (i = 2; i < 9; i += 2)
			{
				fillWithBlocks(world, structureBoundingBox, i, 3, 15, i, 4, 15, Blocks.iron_bars, Blocks.iron_bars, false);
			}

			i = getMetadataWithOffset(Blocks.stone_brick_stairs, 3);
			fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 5, 6, 1, 7, false, random, strongholdStones);
			fillWithRandomizedBlocks(world, structureBoundingBox, 4, 2, 6, 6, 2, 7, false, random, strongholdStones);
			fillWithRandomizedBlocks(world, structureBoundingBox, 4, 3, 7, 6, 3, 7, false, random, strongholdStones);

			for (int j = 4; j <= 6; ++j)
			{
				placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, i, j, 1, 4, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, i, j, 2, 5, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, i, j, 3, 6, structureBoundingBox);
			}

			if (!hasSpawner)
			{
				i = getYWithOffset(3);
				int x = getXWithOffset(5, 6);
				int z = getZWithOffset(5, 6);

				if (structureBoundingBox.isVecInside(x, i, z))
				{
					hasSpawner = true;

					world.setBlock(x, i, z, Blocks.mob_spawner, 0, 2);
					TileEntityMobSpawner spawner = (TileEntityMobSpawner)world.getTileEntity(x, i, z);

					if (spawner != null)
					{
						spawner.func_145881_a().setEntityName("Silverfish");
					}
				}
			}

			return true;
		}
	}

	public static class ChestCorridor extends Stronghold
	{
		private boolean hasMadeChest;

		public ChestCorridor() {}

		public ChestCorridor(int type, Random random, StructureBoundingBox structureBoundingBox, int mode)
		{
			super(type);
			this.coordBaseMode = mode;
			this.door = getRandomDoor(random);
			this.boundingBox = structureBoundingBox;
		}

		@Override
		protected void func_143012_a(NBTTagCompound nbtTagCompound)
		{
			super.func_143012_a(nbtTagCompound);
			nbtTagCompound.setBoolean("Chest", hasMadeChest);
		}

		@Override
		protected void func_143011_b(NBTTagCompound nbtTagCompound)
		{
			super.func_143011_b(nbtTagCompound);
			hasMadeChest = nbtTagCompound.getBoolean("Chest");
		}

		@Override
		public void buildComponent(StructureComponent structureComponent, List list, Random random)
		{
			getNextComponentNormal((Stairs2)structureComponent, list, random, 1, 1);
		}

		public static ChestCorridor findValidPlacement(List list, Random random, int x, int y, int z, int mode, int type)
		{
			StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 7, mode);

			return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(list, structureboundingbox) == null ? new ChestCorridor(type, random, structureboundingbox, mode) : null;
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(world, structureBoundingBox))
			{
				return false;
			}
			else
			{
				fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 4, 4, 6, true, random, strongholdStones);
				placeDoor(world, random, structureBoundingBox, door, 1, 1, 0);
				placeDoor(world, random, structureBoundingBox, Door.OPENING, 1, 1, 6);
				fillWithBlocks(world, structureBoundingBox, 3, 1, 2, 3, 1, 4, Blocks.stonebrick, Blocks.stonebrick, false);
				placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 3, 1, 1, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 3, 1, 5, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 3, 2, 2, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 3, 2, 4, structureBoundingBox);

				int i;

				for (i = 2; i <= 4; ++i)
				{
					placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 2, 1, i, structureBoundingBox);
				}

				if (!hasMadeChest)
				{
					i = getYWithOffset(2);
					int x = getXWithOffset(3, 3);
					int z = getZWithOffset(3, 3);

					if (structureBoundingBox.isVecInside(x, i, z))
					{
						hasMadeChest = true;
						generateStructureChestContents(world, structureBoundingBox, random, 3, 2, 3, ChestGenHooks.getItems(STRONGHOLD_CORRIDOR, random), ChestGenHooks.getCount(STRONGHOLD_CORRIDOR, random));
					}
				}

				return true;
			}
		}
	}

	public static class RoomCrossing extends Stronghold
	{
		protected int roomType;

		public RoomCrossing() {}

		public RoomCrossing(int type, Random random, StructureBoundingBox structureBoundingBox, int mode)
		{
			super(type);
			this.coordBaseMode = mode;
			this.door = getRandomDoor(random);
			this.boundingBox = structureBoundingBox;
			this.roomType = random.nextInt(5);
		}

		@Override
		protected void func_143012_a(NBTTagCompound nbtTagCompound)
		{
			super.func_143012_a(nbtTagCompound);
			nbtTagCompound.setInteger("Type", roomType);
		}

		@Override
		protected void func_143011_b(NBTTagCompound nbtTagCompound)
		{
			super.func_143011_b(nbtTagCompound);
			roomType = nbtTagCompound.getInteger("Type");
		}

		@Override
		public void buildComponent(StructureComponent structureComponent, List list, Random random)
		{
			getNextComponentNormal((Stairs2)structureComponent, list, random, 4, 1);
			getNextComponentX((Stairs2)structureComponent, list, random, 1, 4);
			getNextComponentZ((Stairs2)structureComponent, list, random, 1, 4);
		}

		public static RoomCrossing findValidPlacement(List list, Random random, int x, int y, int z, int mode, int type)
		{
			StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 11, 7, 11, mode);

			return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(list, structureboundingbox) == null ? new RoomCrossing(type, random, structureboundingbox, mode) : null;
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(world, structureBoundingBox))
			{
				return false;
			}
			else
			{
				fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 10, 6, 10, true, random, strongholdStones);
				placeDoor(world, random, structureBoundingBox, door, 4, 1, 0);
				fillWithBlocks(world, structureBoundingBox, 4, 1, 10, 6, 3, 10, Blocks.air, Blocks.air, false);
				fillWithBlocks(world, structureBoundingBox, 0, 1, 4, 0, 3, 6, Blocks.air, Blocks.air, false);
				fillWithBlocks(world, structureBoundingBox, 10, 1, 4, 10, 3, 6, Blocks.air, Blocks.air, false);

				int i;

				switch (roomType)
				{
					case 0:
						placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 5, 1, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 5, 2, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 5, 3, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.torch, 0, 4, 3, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.torch, 0, 6, 3, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.torch, 0, 5, 3, 4, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.torch, 0, 5, 3, 6, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 4, 1, 4, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 4, 1, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 4, 1, 6, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 6, 1, 4, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 6, 1, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 6, 1, 6, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 5, 1, 4, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 5, 1, 6, structureBoundingBox);

						break;
					case 1:
						for (i = 0; i < 5; ++i)
						{
							placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 1, 3 + i, structureBoundingBox);
							placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 7, 1, 3 + i, structureBoundingBox);
							placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3 + i, 1, 3, structureBoundingBox);
							placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3 + i, 1, 7, structureBoundingBox);
						}

						placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 5, 1, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 5, 2, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 5, 3, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.flowing_water, 0, 5, 4, 5, structureBoundingBox);

						break;
					case 2:
						for (i = 1; i <= 9; ++i)
						{
							placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 1, 3, i, structureBoundingBox);
							placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 9, 3, i, structureBoundingBox);
						}

						for (i = 1; i <= 9; ++i)
						{
							placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, i, 3, 1, structureBoundingBox);
							placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, i, 3, 9, structureBoundingBox);
						}

						placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 5, 1, 4, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 5, 1, 6, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 5, 3, 4, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 5, 3, 6, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, 1, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 6, 1, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, 3, 5, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 6, 3, 5, structureBoundingBox);

						for (i = 1; i <= 3; ++i)
						{
							placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, i, 4, structureBoundingBox);
							placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 6, i, 4, structureBoundingBox);
							placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, i, 6, structureBoundingBox);
							placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 6, i, 6, structureBoundingBox);
						}

						placeBlockAtCurrentPosition(world, Blocks.torch, 0, 5, 3, 5, structureBoundingBox);

						for (i = 2; i <= 8; ++i)
						{
							placeBlockAtCurrentPosition(world, Blocks.planks, 0, 2, 3, i, structureBoundingBox);
							placeBlockAtCurrentPosition(world, Blocks.planks, 0, 3, 3, i, structureBoundingBox);

							if (i <= 3 || i >= 7)
							{
								placeBlockAtCurrentPosition(world, Blocks.planks, 0, 4, 3, i, structureBoundingBox);
								placeBlockAtCurrentPosition(world, Blocks.planks, 0, 5, 3, i, structureBoundingBox);
								placeBlockAtCurrentPosition(world, Blocks.planks, 0, 6, 3, i, structureBoundingBox);
							}

							placeBlockAtCurrentPosition(world, Blocks.planks, 0, 7, 3, i, structureBoundingBox);
							placeBlockAtCurrentPosition(world, Blocks.planks, 0, 8, 3, i, structureBoundingBox);
						}

						placeBlockAtCurrentPosition(world, Blocks.ladder, getMetadataWithOffset(Blocks.ladder, 4), 9, 1, 3, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.ladder, getMetadataWithOffset(Blocks.ladder, 4), 9, 2, 3, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.ladder, getMetadataWithOffset(Blocks.ladder, 4), 9, 3, 3, structureBoundingBox);
						generateStructureChestContents(world, structureBoundingBox, random, 3, 4, 8, ChestGenHooks.getItems(STRONGHOLD_CROSSING, random), ChestGenHooks.getCount(STRONGHOLD_CROSSING, random));
				}

				return true;
			}
		}
	}

	public static class StairsStraight extends Stronghold
	{
		public StairsStraight() {}

		public StairsStraight(int type, Random random, StructureBoundingBox structureBoundingBox, int mode)
		{
			super(type);
			this.coordBaseMode = mode;
			this.door = getRandomDoor(random);
			this.boundingBox = structureBoundingBox;
		}

		@Override
		public void buildComponent(StructureComponent structureComponent, List list, Random random)
		{
			getNextComponentNormal((Stairs2)structureComponent, list, random, 1, 1);
		}

		public static StairsStraight findValidPlacement(List list, Random random, int x, int y, int z, int mode, int type)
		{
			StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -7, 0, 5, 11, 8, mode);

			return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(list, structureboundingbox) == null ? new StairsStraight(type, random, structureboundingbox, mode) : null;
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(world, structureBoundingBox))
			{
				return false;
			}
			else
			{
				fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 4, 10, 7, true, random, strongholdStones);
				placeDoor(world, random, structureBoundingBox, door, 1, 7, 0);
				placeDoor(world, random, structureBoundingBox, Door.OPENING, 1, 1, 7);

				int i = getMetadataWithOffset(Blocks.stone_stairs, 2);

				for (int j = 0; j < 6; ++j)
				{
					placeBlockAtCurrentPosition(world, Blocks.stone_stairs, i, 1, 6 - j, 1 + j, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stone_stairs, i, 2, 6 - j, 1 + j, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stone_stairs, i, 3, 6 - j, 1 + j, structureBoundingBox);

					if (j < 5)
					{
						placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 5 - j, 1 + j, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 5 - j, 1 + j, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 5 - j, 1 + j, structureBoundingBox);
					}
				}

				return true;
			}
		}
	}

	public static class Stairs2 extends Stairs
	{
		public PieceWeight strongholdPieceWeight;
		public PortalRoom strongholdPortalRoom;
		public List field_75026_c = Lists.newArrayList();

		public Stairs2() {}

		public Stairs2(int type, Random random, int par3, int par4)
		{
			super(0, random, par3, par4);
		}

		@Override
		public ChunkPosition func_151553_a()
		{
			return strongholdPortalRoom != null ? strongholdPortalRoom.func_151553_a() : super.func_151553_a();
		}
	}

	public static class Prison extends Stronghold
	{
		public Prison() {}

		public Prison(int type, Random random, StructureBoundingBox structureBoundingBox, int mode)
		{
			super(type);
			this.coordBaseMode = mode;
			this.door = getRandomDoor(random);
			this.boundingBox = structureBoundingBox;
		}

		@Override
		public void buildComponent(StructureComponent structureComponent, List list, Random random)
		{
			getNextComponentNormal((Stairs2)structureComponent, list, random, 1, 1);
		}

		public static Prison findValidPlacement(List list, Random random, int x, int y, int z, int mode, int type)
		{
			StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 9, 5, 11, mode);

			return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(list, structureboundingbox) == null ? new Prison(type, random, structureboundingbox, mode) : null;
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(world, structureBoundingBox))
			{
				return false;
			}
			else
			{
				fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 8, 4, 10, true, random, strongholdStones);
				placeDoor(world, random, structureBoundingBox, door, 1, 1, 0);
				fillWithBlocks(world, structureBoundingBox, 1, 1, 10, 3, 3, 10, Blocks.air, Blocks.air, false);
				fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 1, 4, 3, 1, false, random, strongholdStones);
				fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 3, 4, 3, 3, false, random, strongholdStones);
				fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 7, 4, 3, 7, false, random, strongholdStones);
				fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 9, 4, 3, 9, false, random, strongholdStones);
				fillWithBlocks(world, structureBoundingBox, 4, 1, 4, 4, 3, 6, Blocks.iron_bars, Blocks.iron_bars, false);
				fillWithBlocks(world, structureBoundingBox, 5, 1, 5, 7, 3, 5, Blocks.iron_bars, Blocks.iron_bars, false);
				placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, 4, 3, 2, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, 4, 3, 8, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.iron_door, getMetadataWithOffset(Blocks.iron_door, 3), 4, 1, 2, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.iron_door, getMetadataWithOffset(Blocks.iron_door, 3) + 8, 4, 2, 2, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.iron_door, getMetadataWithOffset(Blocks.iron_door, 3), 4, 1, 8, structureBoundingBox);
				placeBlockAtCurrentPosition(world, Blocks.iron_door, getMetadataWithOffset(Blocks.iron_door, 3) + 8, 4, 2, 8, structureBoundingBox);

				return true;
			}
		}
	}

	public static class LeftTurn extends Stronghold
	{
		public LeftTurn() {}

		public LeftTurn(int type, Random random, StructureBoundingBox structureBoundingBox, int mode)
		{
			super(type);
			this.coordBaseMode = mode;
			this.door = getRandomDoor(random);
			this.boundingBox = structureBoundingBox;
		}

		@Override
		public void buildComponent(StructureComponent structureComponent, List list, Random random)
		{
			if (coordBaseMode != 2 && coordBaseMode != 3)
			{
				getNextComponentZ((Stairs2)structureComponent, list, random, 1, 1);
			}
			else
			{
				getNextComponentX((Stairs2)structureComponent, list, random, 1, 1);
			}
		}

		public static LeftTurn findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
		{
			StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, 5, par5);

			return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new LeftTurn(par6, par1Random, structureboundingbox, par5) : null;
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(world, structureBoundingBox))
			{
				return false;
			}
			else
			{
				fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 4, 4, 4, true, random, strongholdStones);
				placeDoor(world, random, structureBoundingBox, door, 1, 1, 0);

				if (coordBaseMode != 2 && coordBaseMode != 3)
				{
					fillWithBlocks(world, structureBoundingBox, 4, 1, 1, 4, 3, 3, Blocks.air, Blocks.air, false);
				}
				else
				{
					fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 0, 3, 3, Blocks.air, Blocks.air, false);
				}

				return true;
			}
		}
	}

	public static class RightTurn extends LeftTurn
	{
		@Override
		public void buildComponent(StructureComponent structureComponent, List list, Random random)
		{
			if (coordBaseMode != 2 && coordBaseMode != 3)
			{
				getNextComponentX((Stairs2)structureComponent, list, random, 1, 1);
			}
			else
			{
				getNextComponentZ((Stairs2)structureComponent, list, random, 1, 1);
			}
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(world, structureBoundingBox))
			{
				return false;
			}
			else
			{
				fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 4, 4, 4, true, random, strongholdStones);
				placeDoor(world, random, structureBoundingBox, door, 1, 1, 0);

				if (coordBaseMode != 2 && coordBaseMode != 3)
				{
					fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 0, 3, 3, Blocks.air, Blocks.air, false);
				}
				else
				{
					fillWithBlocks(world, structureBoundingBox, 4, 1, 1, 4, 3, 3, Blocks.air, Blocks.air, false);
				}

				return true;
			}
		}
	}

	private static class Stones extends BlockSelector
	{
		private Stones() {}

		@Override
		public void selectBlocks(Random random, int x, int y, int z, boolean flag)
		{
			if (flag)
			{
				field_151562_a = Blocks.stonebrick;
				float var1 = random.nextFloat();

				if (var1 < 0.2F)
				{
					selectedBlockMetaData = 2;
				}
				else if (var1 < 0.5F)
				{
					selectedBlockMetaData = 1;
				}
				else if (var1 < 0.55F)
				{
					field_151562_a = Blocks.monster_egg;
					selectedBlockMetaData = 2;
				}
				else
				{
					selectedBlockMetaData = 0;
				}
			}
			else
			{
				field_151562_a = Blocks.air;
				selectedBlockMetaData = 0;
			}
		}
	}

	public abstract static class Stronghold extends StructureComponent
	{
		protected Door door;

		public Stronghold()
		{
			this.door = Door.OPENING;
		}

		protected Stronghold(int type)
		{
			super(type);
			this.door = Door.OPENING;
		}

		@Override
		protected void func_143012_a(NBTTagCompound nbtTagCompound)
		{
			nbtTagCompound.setString("EntryDoor", door.name());
		}

		@Override
		protected void func_143011_b(NBTTagCompound nbtTagCompound)
		{
			door = Door.valueOf(nbtTagCompound.getString("EntryDoor"));
		}

		protected void placeDoor(World world, Random random, StructureBoundingBox structureBoundingBox, Door enumDoor, int x, int y, int z)
		{
			switch (SwitchDoor.doorEnum[enumDoor.ordinal()])
			{
				case 1:
				default:
					fillWithBlocks(world, structureBoundingBox, x, y, z, x + 3 - 1, y + 3 - 1, z, Blocks.air, Blocks.air, false);

					break;
				case 2:
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x, y, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x, y + 1, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x, y + 2, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x + 1, y + 2, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x + 2, y + 2, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x + 2, y + 1, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x + 2, y, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.wooden_door, 0, x + 1, y, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.wooden_door, 8, x + 1, y + 1, z, structureBoundingBox);

					break;
				case 3:
					placeBlockAtCurrentPosition(world, Blocks.air, 0, x + 1, y, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.air, 0, x + 1, y + 1, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, x, y, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, x, y + 1, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, x, y + 2, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, x + 1, y + 2, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, x + 2, y + 2, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, x + 2, y + 1, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, x + 2, y, z, structureBoundingBox);

					break;
				case 4:
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x, y, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x, y + 1, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x, y + 2, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x + 1, y + 2, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x + 2, y + 2, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x + 2, y + 1, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x + 2, y, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.iron_door, 0, x + 1, y, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.iron_door, 8, x + 1, y + 1, z, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stone_button, getMetadataWithOffset(Blocks.stone_button, 4), x + 2, y + 1, z + 1, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stone_button, getMetadataWithOffset(Blocks.stone_button, 3), x + 2, y + 1, z - 1, structureBoundingBox);
			}
		}

		protected Door getRandomDoor(Random random)
		{
			int i = random.nextInt(5);

			switch (i)
			{
				case 0:
				case 1:
				default:
					return Door.OPENING;
				case 2:
					return Door.WOOD_DOOR;
				case 3:
					return Door.GRATES;
				case 4:
					return Door.IRON_DOOR;
			}
		}

		protected StructureComponent getNextComponentNormal(Stairs2 stairs2, List list, Random random, int par4, int par5)
		{
			switch (coordBaseMode)
			{
				case 0:
					return getNextValidComponent(stairs2, list, random, boundingBox.minX + par4, boundingBox.minY + par5, boundingBox.maxZ + 1, coordBaseMode, getComponentType());
				case 1:
					return getNextValidComponent(stairs2, list, random, boundingBox.minX - 1, boundingBox.minY + par5, boundingBox.minZ + par4, coordBaseMode, getComponentType());
				case 2:
					return getNextValidComponent(stairs2, list, random, boundingBox.minX + par4, boundingBox.minY + par5, boundingBox.minZ - 1, coordBaseMode, getComponentType());
				case 3:
					return getNextValidComponent(stairs2, list, random, boundingBox.maxX + 1, boundingBox.minY + par5, boundingBox.minZ + par4, coordBaseMode, getComponentType());
				default:
					return null;
			}
		}

		protected StructureComponent getNextComponentX(Stairs2 stairs2, List list, Random random, int par4, int par5)
		{
			switch (coordBaseMode)
			{
				case 0:
					return getNextValidComponent(stairs2, list, random, boundingBox.minX - 1, boundingBox.minY + par4, boundingBox.minZ + par5, 1, getComponentType());
				case 1:
					return getNextValidComponent(stairs2, list, random, boundingBox.minX + par5, boundingBox.minY + par4, boundingBox.minZ - 1, 2, getComponentType());
				case 2:
					return getNextValidComponent(stairs2, list, random, boundingBox.minX - 1, boundingBox.minY + par4, boundingBox.minZ + par5, 1, getComponentType());
				case 3:
					return getNextValidComponent(stairs2, list, random, boundingBox.minX + par5, boundingBox.minY + par4, boundingBox.minZ - 1, 2, getComponentType());
				default:
					return null;
			}
		}

		protected StructureComponent getNextComponentZ(Stairs2 stairs2, List list, Random random, int par4, int par5)
		{
			switch (coordBaseMode)
			{
				case 0:
					return getNextValidComponent(stairs2, list, random, boundingBox.maxX + 1, boundingBox.minY + par4, boundingBox.minZ + par5, 3, getComponentType());
				case 1:
					return getNextValidComponent(stairs2, list, random, boundingBox.minX + par5, boundingBox.minY + par4, boundingBox.maxZ + 1, 0, getComponentType());
				case 2:
					return getNextValidComponent(stairs2, list, random, boundingBox.maxX + 1, boundingBox.minY + par4, boundingBox.minZ + par5, 3, getComponentType());
				case 3:
					return getNextValidComponent(stairs2, list, random, boundingBox.minX + par5, boundingBox.minY + par4, boundingBox.maxZ + 1, 0, getComponentType());
				default:
					return null;
			}
		}

		protected static boolean canStrongholdGoDeeper(StructureBoundingBox structureBoundingBox)
		{
			return structureBoundingBox != null && structureBoundingBox.minY > 10;
		}
	}

	public static class Crossing extends Stronghold
	{
		private boolean field_74996_b;
		private boolean field_74997_c;
		private boolean field_74995_d;
		private boolean field_74999_h;

		public Crossing() {}

		public Crossing(int type, Random random, StructureBoundingBox structureBoundingBox, int mode)
		{
			super(type);
			this.coordBaseMode = mode;
			this.door = getRandomDoor(random);
			this.boundingBox = structureBoundingBox;
			this.field_74996_b = random.nextBoolean();
			this.field_74997_c = random.nextBoolean();
			this.field_74995_d = random.nextBoolean();
			this.field_74999_h = random.nextInt(3) > 0;
		}

		@Override
		protected void func_143012_a(NBTTagCompound nbtTagCompound)
		{
			super.func_143012_a(nbtTagCompound);
			nbtTagCompound.setBoolean("leftLow", field_74996_b);
			nbtTagCompound.setBoolean("leftHigh", field_74997_c);
			nbtTagCompound.setBoolean("rightLow", field_74995_d);
			nbtTagCompound.setBoolean("rightHigh", field_74999_h);
		}

		@Override
		protected void func_143011_b(NBTTagCompound nbtTagCompound)
		{
			super.func_143011_b(nbtTagCompound);
			field_74996_b = nbtTagCompound.getBoolean("leftLow");
			field_74997_c = nbtTagCompound.getBoolean("leftHigh");
			field_74995_d = nbtTagCompound.getBoolean("rightLow");
			field_74999_h = nbtTagCompound.getBoolean("rightHigh");
		}

		@Override
		public void buildComponent(StructureComponent structureComponent, List list, Random random)
		{
			int var1 = 3;
			int var2 = 5;

			if (coordBaseMode == 1 || coordBaseMode == 2)
			{
				var1 = 8 - var1;
				var2 = 8 - var2;
			}

			getNextComponentNormal((Stairs2)structureComponent, list, random, 5, 1);

			if (field_74996_b)
			{
				getNextComponentX((Stairs2)structureComponent, list, random, var1, 1);
			}

			if (field_74997_c)
			{
				getNextComponentX((Stairs2)structureComponent, list, random, var2, 7);
			}

			if (field_74995_d)
			{
				getNextComponentZ((Stairs2)structureComponent, list, random, var1, 1);
			}

			if (field_74999_h)
			{
				getNextComponentZ((Stairs2)structureComponent, list, random, var2, 7);
			}
		}

		public static Crossing findValidPlacement(List list, Random random, int x, int y, int z, int mode, int type)
		{
			StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -3, 0, 10, 9, 11, mode);

			return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(list, structureboundingbox) == null ? new Crossing(type, random, structureboundingbox, mode) : null;
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(world, structureBoundingBox))
			{
				return false;
			}
			else
			{
				fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 9, 8, 10, true, random, strongholdStones);
				placeDoor(world, random, structureBoundingBox, door, 4, 3, 0);

				if (field_74996_b)
				{
					fillWithBlocks(world, structureBoundingBox, 0, 3, 1, 0, 5, 3, Blocks.air, Blocks.air, false);
				}

				if (field_74995_d)
				{
					fillWithBlocks(world, structureBoundingBox, 9, 3, 1, 9, 5, 3, Blocks.air, Blocks.air, false);
				}

				if (field_74997_c)
				{
					fillWithBlocks(world, structureBoundingBox, 0, 5, 7, 0, 7, 9, Blocks.air, Blocks.air, false);
				}

				if (field_74999_h)
				{
					fillWithBlocks(world, structureBoundingBox, 9, 5, 7, 9, 7, 9, Blocks.air, Blocks.air, false);
				}

				fillWithBlocks(world, structureBoundingBox, 5, 1, 10, 7, 3, 10, Blocks.air, Blocks.air, false);
				fillWithRandomizedBlocks(world, structureBoundingBox, 1, 2, 1, 8, 2, 6, false, random, strongholdStones);
				fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 5, 4, 4, 9, false, random, strongholdStones);
				fillWithRandomizedBlocks(world, structureBoundingBox, 8, 1, 5, 8, 4, 9, false, random, strongholdStones);
				fillWithRandomizedBlocks(world, structureBoundingBox, 1, 4, 7, 3, 4, 9, false, random, strongholdStones);
				fillWithRandomizedBlocks(world, structureBoundingBox, 1, 3, 5, 3, 3, 6, false, random, strongholdStones);
				fillWithBlocks(world, structureBoundingBox, 1, 3, 4, 3, 3, 4, Blocks.stone_slab, Blocks.stone_slab, false);
				fillWithBlocks(world, structureBoundingBox, 1, 4, 6, 3, 4, 6, Blocks.stone_slab, Blocks.stone_slab, false);
				fillWithRandomizedBlocks(world, structureBoundingBox, 5, 1, 7, 7, 1, 8, false, random, strongholdStones);
				fillWithBlocks(world, structureBoundingBox, 5, 1, 9, 7, 1, 9, Blocks.stone_slab, Blocks.stone_slab, false);
				fillWithBlocks(world, structureBoundingBox, 5, 2, 7, 7, 2, 7, Blocks.stone_slab, Blocks.stone_slab, false);
				fillWithBlocks(world, structureBoundingBox, 4, 5, 7, 4, 5, 9, Blocks.stone_slab, Blocks.stone_slab, false);
				fillWithBlocks(world, structureBoundingBox, 8, 5, 7, 8, 5, 9, Blocks.stone_slab, Blocks.stone_slab, false);
				fillWithBlocks(world, structureBoundingBox, 5, 5, 7, 7, 5, 9, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
				placeBlockAtCurrentPosition(world, Blocks.torch, 0, 6, 5, 6, structureBoundingBox);

				return true;
			}
		}
	}

	public static class Corridor extends Stronghold
	{
		private int field_74993_a;

		public Corridor() {}

		public Corridor(int type, Random random, StructureBoundingBox structureBoundingBox, int mode)
		{
			super(type);
			this.coordBaseMode = mode;
			this.boundingBox = structureBoundingBox;
			this.field_74993_a = mode != 2 && mode != 0 ? structureBoundingBox.getXSize() : structureBoundingBox.getZSize();
		}

		@Override
		protected void func_143012_a(NBTTagCompound nbtTagCompound)
		{
			super.func_143012_a(nbtTagCompound);
			nbtTagCompound.setInteger("Steps", field_74993_a);
		}

		@Override
		protected void func_143011_b(NBTTagCompound nbtTagCompound)
		{
			super.func_143011_b(nbtTagCompound);
			field_74993_a = nbtTagCompound.getInteger("Steps");
		}

		public static StructureBoundingBox func_74992_a(List list, Random random, int x, int y, int z, int mode)
		{
			StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 4, mode);
			StructureComponent structureComponent = StructureComponent.findIntersecting(list, structureBoundingBox);

			if (structureComponent == null)
			{
				return null;
			}
			else
			{
				if (structureComponent.getBoundingBox().minY == structureBoundingBox.minY)
				{
					for (int i = 3; i >= 1; --i)
					{
						structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, i - 1, mode);

						if (!structureComponent.getBoundingBox().intersectsWith(structureBoundingBox))
						{
							return StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, i, mode);
						}
					}
				}

				return null;
			}
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox)
		{
			if (isLiquidInStructureBoundingBox(world, structureBoundingBox))
			{
				return false;
			}
			else
			{
				for (int i = 0; i < field_74993_a; ++i)
				{
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 0, 0, i, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 0, i, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 0, i, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 0, i, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 4, 0, i, structureBoundingBox);

					for (int j = 1; j <= 3; ++j)
					{
						placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 0, j, i, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, j, i, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, j, i, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.air, 0, 3, j, i, structureBoundingBox);
						placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 4, j, i, structureBoundingBox);
					}

					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 0, 4, i, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 4, i, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 4, i, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 4, i, structureBoundingBox);
					placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 4, 4, i, structureBoundingBox);
				}

				return true;
			}
		}
	}

	static final class SwitchDoor
	{
		static final int[] doorEnum = new int[Door.values().length];

		static
		{
			try
			{
				doorEnum[Door.OPENING.ordinal()] = 1;
			}
			catch (NoSuchFieldError ignored) {}

			try
			{
				doorEnum[Door.WOOD_DOOR.ordinal()] = 2;
			}
			catch (NoSuchFieldError ignored) {}

			try
			{
				doorEnum[Door.GRATES.ordinal()] = 3;
			}
			catch (NoSuchFieldError ignored) {}

			try
			{
				doorEnum[Door.IRON_DOOR.ordinal()] = 4;
			}
			catch (NoSuchFieldError ignored) {}
		}
	}

	private static class PieceWeight
	{
		public final Class pieceClass;
		public final int pieceWeight;
		public final int instancesLimit;
		public int instancesSpawned;

		public PieceWeight(Class clazz, int weight, int limit)
		{
			this.pieceClass = clazz;
			this.pieceWeight = weight;
			this.instancesLimit = limit;
		}

		public boolean canSpawnMoreStructuresOfType(int type)
		{
			return instancesLimit == 0 || instancesSpawned < instancesLimit;
		}

		public boolean canSpawnMoreStructures()
		{
			return instancesLimit == 0 || instancesSpawned < instancesLimit;
		}
	}
}