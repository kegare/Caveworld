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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.minecraft.util.MathHelper;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;

public class Version implements Callable<Version.Status>
{
	private static Optional<String> CURRENT = Optional.absent();
	private static Optional<String> LATEST = Optional.absent();

	public static boolean DEV_DEBUG = false;

	private static Optional<Status> status = Optional.fromNullable(Status.PENDING);

	public static enum Status
	{
		PENDING,
		FAILED,
		UP_TO_DATE,
		OUTDATED,
		AHEAD
	}

	private static void initialize()
	{
		CURRENT = Optional.of(Strings.nullToEmpty(Caveworld.metadata.version));
		LATEST = Optional.fromNullable(CURRENT.orNull());

		ModContainer mod = CaveUtils.getModContainer();
		File file = mod == null ? null : mod.getSource();

		if (file != null && file.exists())
		{
			if (file.isFile())
			{
				String name = FilenameUtils.getBaseName(file.getName());

				if (StringUtils.endsWithIgnoreCase(name, "dev"))
				{
					DEV_DEBUG = true;
				}
			}
			else if (file.isDirectory())
			{
				DEV_DEBUG = true;
			}
		}
		else if (!FMLForgePlugin.RUNTIME_DEOBF)
		{
			DEV_DEBUG = true;
		}

		if (DEV_DEBUG)
		{
			Caveworld.metadata.version += "-dev";
		}
	}

	public static void versionCheck()
	{
		if (!CURRENT.isPresent() || !LATEST.isPresent())
		{
			initialize();
		}

		ExecutorService pool = Executors.newSingleThreadExecutor();
		Future<Status> task = pool.submit(new Version());

		try
		{
			status = Optional.of(task.get());
		}
		catch (Exception e)
		{
			CaveLog.log(Level.WARN, e, "An error occurred trying to version check");

			status = Optional.of(Status.FAILED);
		}
		finally
		{
			pool.shutdown();

			try
			{
				if (!pool.awaitTermination(1, TimeUnit.MINUTES))
				{
					pool.shutdownNow();
				}
			}
			catch (InterruptedException e)
			{
				pool.shutdownNow();
			}

			CaveLog.fine("Version status checked: %s", getStatus().name());
		}
	}

	public static String getCurrent()
	{
		return CURRENT.orNull();
	}

	public static String getLatest()
	{
		return LATEST.or(getCurrent());
	}

	public static Status getStatus()
	{
		return status.orNull();
	}

	public static boolean isOutdated()
	{
		return getStatus() == Status.OUTDATED;
	}

	@Override
	public Status call() throws Exception
	{
		URL url = new URL(Caveworld.metadata.updateUrl);
		Map<String, Object> data = null;

		try (InputStream input = url.openStream())
		{
			byte[] dat = ByteStreams.toByteArray(input);

			if (dat != null && dat.length > 0)
			{
				data = new Gson().fromJson(new String(dat), Map.class);
			}
		}
		finally
		{
			if (data == null)
			{
				return Status.FAILED;
			}
		}

		if (data.containsKey("homepage"))
		{
			Caveworld.metadata.url = (String)data.get("homepage");
		}

		Map<String, String> versions = Maps.newHashMap();

		if (data.containsKey("versions"))
		{
			versions = (Map<String, String>)data.get("versions");
		}

		String version = versions.get(MinecraftForge.MC_VERSION);
		ArtifactVersion current = new DefaultArtifactVersion(CURRENT.or("1.0.0"));

		if (!Strings.isNullOrEmpty(version))
		{
			ArtifactVersion latest = new DefaultArtifactVersion(version);

			LATEST = Optional.of(version);

			switch (MathHelper.clamp_int(latest.compareTo(current), -1, 1))
			{
				case 0:
					return Status.UP_TO_DATE;
				case -1:
					return Status.AHEAD;
				case 1:
					return Status.OUTDATED;
				default:
					return Status.FAILED;
			}
		}

		version = versions.get("latest");

		if (!Strings.isNullOrEmpty(version))
		{
			LATEST = Optional.of(version);
		}

		return Status.FAILED;
	}
}