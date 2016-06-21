package caveworld.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import caveworld.core.Caveworld;
import caveworld.world.ChunkProviderAquaCavern;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

public class CaveEntityRegistry
{
	public static final Map<Integer, Class<? extends Entity>> entities = Maps.newHashMap();
	public static final Map<Integer, EggInfo> mobs = Maps.newHashMap();
	public static final List<SpawnListEntry> spawns = Lists.newArrayList();

	private static int entityId;

	public static void registerEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
	{
		EntityRegistry.registerModEntity(entityClass, entityName, entityId++, Caveworld.instance, trackingRange, updateFrequency, sendsVelocityUpdates);

		entities.put(entityId, entityClass);
	}

	public static void registerMob(Class<? extends Entity> entityClass, String entityName)
	{
		registerEntity(entityClass, entityName, 128, 1, true);

		mobs.put(entityId, null);
	}

	public static void registerMob(Class<? extends Entity> entityClass, String entityName, int primaryColor, int secondaryColor)
	{
		registerMob(entityClass, entityName);

		mobs.put(entityId, new EggInfo(primaryColor, secondaryColor));
	}

	public static void registerEntities()
	{
		registerMob(EntityCaveman.class, "Caveman", 0xAAAAAA, 0xCCCCCC);
		registerMob(EntityArcherZombie.class, "ArcherZombie", 0x00A0A0, 0xAAAAAA);
		registerMob(EntityCavenicSkeleton.class, "CavenicSkeleton", 0xAAAAAA, 0xDDDDDD);
		registerMob(EntityMasterCavenicSkeleton.class, "MasterCavenicSkeleton", 0xAAAAAA, 0xDDDDDD);
		registerMob(EntityCrazyCavenicSkeleton.class, "CrazyCavenicSkeleton");
		registerMob(EntityCavenicCreeper.class, "CavenicCreeper", 0xAAAAAA, 0x2E8B57);
		registerMob(EntityMasterCavenicCreeper.class, "MasterCavenicCreeper", 0xAAAAAA, 0x2E8B57);
		registerMob(EntityCavenicZombie.class, "CavenicZombie", 0xAAAAAA, 0x00A0A0);
		registerMob(EntityCavenicSpider.class, "CavenicSpider", 0xAAAAAA, 0x811F1F);
		registerMob(EntityAquaSquid.class, "Squid");

		ChunkProviderAquaCavern.waterSpawns.add(new SpawnListEntry(EntityAquaSquid.class, 100, 4, 4));
	}

	public static void addVallilaSpawns()
	{
		spawns.add(new SpawnListEntry(EntitySpider.class, 100, 4, 4));
		spawns.add(new SpawnListEntry(EntityZombie.class, 100, 4, 4));
		spawns.add(new SpawnListEntry(EntitySkeleton.class, 100, 4, 4));
		spawns.add(new SpawnListEntry(EntityCreeper.class, 100, 4, 4));
		spawns.add(new SpawnListEntry(EntitySlime.class, 100, 4, 4));
		spawns.add(new SpawnListEntry(EntityEnderman.class, 10, 1, 4));
		spawns.add(new SpawnListEntry(EntityWitch.class, 5, 1, 1));
	}

	public static void addSpawn(Class <? extends EntityLiving> entityClass, int weightedProb, int min, int max, BiomeGenBase... biomes)
	{
		boolean flag = false;

		for (SpawnListEntry entry : spawns)
		{
			if (entry.entityClass == entityClass)
			{
				entry.itemWeight = weightedProb;
				entry.minGroupCount = min;
				entry.maxGroupCount = max;
				flag = true;
			}
		}

		if (!flag)
		{
			spawns.add(new SpawnListEntry(entityClass, weightedProb, min, max));
		}
	}

	public static void removeSpawn(Class <? extends EntityLiving> entityClass, BiomeGenBase... biomes)
	{
		Iterator<SpawnListEntry> iterator = spawns.iterator();

		while (iterator.hasNext())
		{
			SpawnListEntry entry = iterator.next();

			if (entry.entityClass == entityClass)
			{
				iterator.remove();
			}
		}
	}

	public static class EggInfo
	{
		public final int primaryColor;
		public final int secondaryColor;

		public EggInfo(int primaryColor, int secondaryColor)
		{
			this.primaryColor = primaryColor;
			this.secondaryColor = secondaryColor;
		}
	}
}