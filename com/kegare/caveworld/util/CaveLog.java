package com.kegare.caveworld.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("static-access")
public class CaveLog
{
	public static final Logger logger = LogManager.getLogger("Caveworld");

	public static void log(Level level, String log)
	{
		logger.log(level, log);
	}

	public static void info(String log)
	{
		logger.info(log);
	}

	public static void severe(String log)
	{
		logger.error(log);
	}

	public static void severe(String log, Throwable throwable)
	{
		logger.error(log);

		for (StackTraceElement stacktrace : throwable.getStackTrace())
		{
			logger.error("\t" + stacktrace);
		}
	}

	public static void severe(Throwable throwable)
	{
		logger.error(throwable.getClass().getSimpleName() + " occured in " + throwable.getStackTrace()[0].getClassName());

		for (StackTraceElement stacktrace : throwable.getStackTrace())
		{
			logger.error("\t" + stacktrace);
		}
	}

	public static void warning(String log)
	{
		logger.warn(log);
	}
}