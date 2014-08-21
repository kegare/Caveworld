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