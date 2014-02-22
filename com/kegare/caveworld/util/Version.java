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

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.kegare.caveworld.core.Caveworld;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class Version
{
	private static Optional<String> CURRENT = Optional.absent();
	private static Optional<String> LATEST = Optional.absent();

	public static boolean DEV_DEBUG = false;

	private static Status status = Status.PENDING;

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
		CURRENT = Optional.of(Caveworld.metadata.version);

		if (FMLForgePlugin.RUNTIME_DEOBF)
		{
			try
			{
				File file = Loader.instance().activeModContainer().getSource();

				if (file.exists() && file.isFile())
				{
					String name = file.getName().substring(0, file.getName().lastIndexOf('.'));

					if (name.endsWith("dev"))
					{
						DEV_DEBUG = true;
					}
					else if (name.substring(name.lastIndexOf('-') + 1).startsWith("dev"))
					{
						DEV_DEBUG = true;
					}
					else if (name.substring(name.lastIndexOf('_') + 1).startsWith("dev"))
					{
						DEV_DEBUG = true;
					}
				}
			}
			catch (Exception e)
			{
				DEV_DEBUG = false;
			}
		}
		else
		{
			DEV_DEBUG = true;
		}
	}

	public static void versionCheck()
	{
		initialize();

		new Thread("Caveworld Version Check")
		{
			@SuppressWarnings("unchecked")
			@Override
			public void run()
			{
				try
				{
					URL url = new URL(Caveworld.metadata.updateUrl);
					InputStream con = url.openStream();
					String data = new String(ByteStreams.toByteArray(con));
					con.close();

					Map<String, Object> json = new Gson().fromJson(data, Map.class);

					if (json.containsKey("homepage"))
					{
						Caveworld.metadata.url = (String)json.get("homepage");
					}

					Map<String, String> versions = (Map<String, String>)json.get("versions");

					String version = versions.get(MinecraftForge.MC_VERSION);
					ArtifactVersion current = new DefaultArtifactVersion(CURRENT.or("1.0.0"));

					if (!Strings.isNullOrEmpty(version))
					{
						ArtifactVersion latest = new DefaultArtifactVersion(version);
						int diff = latest.compareTo(current);

						if (diff == 0)
						{
							status = Status.UP_TO_DATE;
						}
						else if (diff < 0)
						{
							status = Status.AHEAD;
						}
						else
						{
							status = Status.OUTDATED;
						}

						LATEST = Optional.of(version);
					}
					else
					{
						version = versions.get("latest");

						if (!Strings.isNullOrEmpty(version))
						{
							LATEST = Optional.of(version);
						}

						status = Status.FAILED;
					}
				}
				catch (Exception e)
				{
					CaveLog.log(Level.WARN, e, "An error occurred trying to version check");

					status = Status.FAILED;
				}
			}
		}.start();
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
		return status == null ? Status.FAILED : status;
	}

	public static boolean isOutdated()
	{
		return status == Status.OUTDATED;
	}
}