/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.util;

import net.minecraft.world.biome.BiomeGenBase;

import com.google.common.base.Function;

public class BiomeIdFunction implements Function<BiomeGenBase, Integer>
{
	@Override
	public Integer apply(BiomeGenBase input)
	{
		return input.biomeID;
	}
}