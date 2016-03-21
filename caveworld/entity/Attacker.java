/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.entity;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

public class Attacker
{
	private final String uuid;

	private String name;
	private float damage;

	public Attacker(String uuid)
	{
		this.uuid = uuid;
	}

	public Attacker(UUID uuid)
	{
		this(uuid.toString());
	}

	public Attacker(NBTTagCompound nbt)
	{
		this(nbt.getString("UUID"));
		this.name = nbt.getString("Name");
		this.damage = nbt.getFloat("Damage");
	}

	public String getUniqueID()
	{
		return uuid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String str)
	{
		name = str;
	}

	public float getDamage()
	{
		return damage;
	}

	public void setDamage(float value)
	{
		damage = value;
	}

	public void addDamage(float value)
	{
		damage += value;
	}

	public NBTTagCompound getNBTData()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setString("UUID", uuid);
		nbt.setString("Name", name);
		nbt.setFloat("Damage", damage);

		return nbt;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj == null || !(obj instanceof Attacker))
		{
			return false;
		}

		Attacker attacker = (Attacker)obj;

		return uuid.equals(attacker.uuid);
	}

	@Override
	public int hashCode()
	{
		return uuid.hashCode();
	}
}