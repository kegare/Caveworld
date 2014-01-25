package kegare.caveworld.util;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import kegare.caveworld.core.Caveworld;

import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.regex.Pattern;

public class Version implements Runnable
{
	private static final Version instance = new Version();

	public static Optional<String> CURRENT = Optional.absent();
	public static Optional<String> LATEST = Optional.absent();

	public static void versionCheck()
	{
		(new Thread(instance)).start();
	}

	public static boolean isOutdated()
	{
		Pattern pattern = Pattern.compile("[^0-9]");
		String current = pattern.matcher(CURRENT.or("1.0.0")).replaceAll("");
		String latest = pattern.matcher(LATEST.or("1.0.0")).replaceAll("");

		return Ints.tryParse(current) < Ints.tryParse(latest);
	}

	@Override
	public void run()
	{
		try
		{
			CURRENT = Optional.of(Caveworld.metadata.version);

			URL url = new URL(Caveworld.metadata.updateUrl);
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			connection.setUseCaches(false);
			Properties properties = new Properties();
			String latest;

			for (int count = 0; count < 3 && !LATEST.isPresent(); ++count)
			{
				if (count == 0)
				{
					CaveLog.info("Beginning version check.");
				}

				connection = url.openConnection();
				connection.setDoInput(true);
				connection.setUseCaches(false);

				properties.clear();
				properties.load(connection.getInputStream());

				latest = properties.getProperty("caveworld.latest");

				if (Strings.isNullOrEmpty(latest))
				{
					CaveLog.warning("Version check attempt " + count + " failed, trying again in 10 seconds..");

					Thread.sleep(10000);
				}
				else
				{
					LATEST = Optional.of(latest);
				}
			}
		}
		catch (Exception e)
		{
			CaveLog.severe(e);
		}
	}
}