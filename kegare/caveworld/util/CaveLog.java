package kegare.caveworld.util;

import cpw.mods.fml.common.FMLLog;

import java.util.logging.Level;
import java.util.logging.Logger;

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

	public static void severe(String log, Throwable throwable)
	{
		getLogger().severe(log);

		for (StackTraceElement stacktrace : throwable.getStackTrace())
		{
			getLogger().severe("\t" + stacktrace);
		}
	}

	public static void severe(Throwable throwable)
	{
		getLogger().severe(throwable.getClass().getSimpleName() + " occured in " + throwable.getStackTrace()[0].getClassName());

		for (StackTraceElement stacktrace : throwable.getStackTrace())
		{
			getLogger().severe("\t" + stacktrace);
		}
	}

	public static void warning(String log)
	{
		getLogger().warning(log);
	}
}