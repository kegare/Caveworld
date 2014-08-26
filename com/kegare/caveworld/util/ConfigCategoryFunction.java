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

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import com.google.common.base.Function;

public class ConfigCategoryFunction implements Function<String, ConfigCategory>
{
	private final Configuration config;

	public ConfigCategoryFunction(Configuration config)
	{
		this.config = config;
	}

	@Override
	public ConfigCategory apply(String input)
	{
		return config.getCategory(input);
	}
}