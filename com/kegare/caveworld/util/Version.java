package com.kegare.caveworld.util;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.kegare.caveworld.core.Caveworld;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class Version
{
	public static Optional<String> CURRENT = Optional.absent();
	public static Optional<String> LATEST = Optional.absent();

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
					String name = file.getName();

					if (name.substring(name.lastIndexOf('_') + 1, name.lastIndexOf('.')).startsWith("dev"))
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

					if (version != null)
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
						status = Status.FAILED;
					}
				}
				catch (Exception e)
				{
					CaveLog.severe(e);

					status = Status.FAILED;
				}
			}
		}.start();
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