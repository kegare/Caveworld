package kegare.caveworld.util;

import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.regex.Pattern;

import kegare.caveworld.core.Caveworld;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

public class Version
{
	public static String CURRENT;
	public static String LATEST;

	static
	{
		if (Strings.isNullOrEmpty(CURRENT))
		{
			CURRENT = Caveworld.metadata.version;
		}

		if (Strings.isNullOrEmpty(LATEST))
		{
			Properties properties = new Properties();

			try
			{
				URL url = new URL(Caveworld.metadata.updateUrl);
				URLConnection connection = url.openConnection();

				connection.setDoInput(true);
				connection.setUseCaches(false);
				properties.load(connection.getInputStream());
			}
			catch (Exception e)
			{
				CaveLog.exception(e);
			}

			LATEST = properties.getProperty("caveworld.latest", CURRENT);
		}
	}

	public static boolean isOutdated()
	{
		Pattern pattern = Pattern.compile("[^0-9]");
		String current = pattern.matcher(CURRENT).replaceAll("");
		String latest = pattern.matcher(LATEST).replaceAll("");

		return Ints.tryParse(current) < Ints.tryParse(latest);
	}
}