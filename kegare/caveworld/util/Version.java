package kegare.caveworld.util;

import java.net.URL;
import java.util.Properties;

import kegare.caveworld.core.Caveworld;

public class Version
{
	public static String CURRENT;
	public static String LATEST;

	public static void versionCheck()
	{
		try
		{
			URL url = new URL("https://dl.dropboxusercontent.com/u/51943112/kegare/kegare.info");
			Properties mappings = new Properties();

			mappings.load(url.openStream());

			CURRENT = Caveworld.metadata.version;
			LATEST = mappings.getProperty("caveworld.latest", CURRENT);

			Caveworld.metadata.url = mappings.getProperty("caveworld.url", Caveworld.metadata.url);
		}
		catch (Exception e)
		{
			CaveLog.exception(e);
		}
	}

	public static boolean isOutdated()
	{
		return !CURRENT.equals(LATEST);
	}
}