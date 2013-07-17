package kegare.caveworld.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;

public class CaveLog
{
	public static Logger getLogger()
	{
		Logger logger = Logger.getLogger("Caveworld");
		logger.setParent(FMLLog.getLogger());

		return logger;
	}

	public static void log(Level level, String log)
	{
		getLogger().log(level, log);
	}

	public static void info(String log)
	{
		getLogger().info(log);
	}

	public static void severe(String log)
	{
		getLogger().severe(log);
	}

	public static void warning(String log)
	{
		getLogger().warning(log);
	}

	public static void exception(Exception e)
	{
		getLogger().severe(e.getClass().getSimpleName() + " occured in " + e.getStackTrace()[0].getClassName());

		for (StackTraceElement stacktrace : e.getStackTrace())
		{
			getLogger().severe("\t" + stacktrace);
		}
	}
}