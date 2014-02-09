package com.kegare.caveworld.util;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.regex.Pattern;

import net.minecraftforge.classloading.FMLForgePlugin;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.common.Loader;

public class Version implements Runnable
{
	private static final Version instance = new Version();

	public static Optional<String> CURRENT = Optional.absent();
	public static Optional<String> LATEST = Optional.absent();

	public static boolean DEV_DEBUG = false;

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

		new Thread(instance).start();
	}

	public static boolean isOutdated()
	{
		Pattern pattern = Pattern.compile("[^0-9]");
		String current = pattern.matcher(CURRENT.or("1.0.0")).replaceAll("");
		String latest = pattern.matcher(LATEST.or("1.0.0")).replaceAll("");

		return Integer.valueOf(current) < Integer.valueOf(latest);
	}

	@Override
	public void run()
	{
		try
		{
			URL url = new URL(Caveworld.metadata.updateUrl);
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			connection.setUseCaches(false);
			Properties properties = new Properties();
			String latest;

			CaveLog.info("Beginning version check.");

			for (int count = 0; count < 3 && !LATEST.isPresent(); ++count)
			{
				connection = url.openConnection();
				connection.setDoInput(true);
				connection.setUseCaches(false);

				properties.clear();
				properties.load(connection.getInputStream());

				latest = properties.getProperty("caveworld.latest");

				if (!Strings.isNullOrEmpty(latest))
				{
					LATEST = Optional.of(latest);

					break;
				}

				CaveLog.warning("Version check attempt " + count + " failed, trying again in 10 seconds..");

				Thread.sleep(10000);
			}
		}
		catch (Exception e)
		{
			CaveLog.severe(e);
		}
	}
}