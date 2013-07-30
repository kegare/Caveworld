package kegare.caveworld.util;

import java.net.URL;
import java.util.Properties;

import kegare.caveworld.core.Caveworld;

import com.google.common.base.Strings;

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
		}
		catch (Exception e)
		{
			CaveLog.exception(e);
		}
	}

	public static boolean isOutdated()
	{
		return !Strings.isNullOrEmpty(CURRENT) && !Strings.isNullOrEmpty(LATEST) ? !CURRENT.equals(LATEST) : false;
	}
}