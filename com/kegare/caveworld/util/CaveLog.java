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

import cpw.mods.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.util.Locale;

@SuppressWarnings("static-access")
public class CaveLog
{
	public static final CaveLog log = new CaveLog();

	private Logger myLog;

	private static boolean configured;

	private static void configureLogging()
	{
		log.myLog = LogManager.getLogger("Caveworld");
		ThreadContext.put("side", FMLCommonHandler.instance().getEffectiveSide().name().toLowerCase(Locale.ENGLISH));

		configured = true;
	}

	public static void log(String targetLog, Level level, String format, Object... data)
	{
		LogManager.getLogger(targetLog).log(level, String.format(format, data));
	}

	public static void log(Level level, String format, Object... data)
	{
		if (!configured)
		{
			configureLogging();
		}

		log.myLog.log(level, String.format(format, data));
	}

	public static void log(String targetLog, Level level, Throwable ex, String format, Object... data)
	{
		LogManager.getLogger(targetLog).log(level, String.format(format, data), ex);
	}

	public static void log(Level level, Throwable ex, String format, Object... data)
	{
		if (!configured)
		{
			configureLogging();
		}

		log.myLog.log(level, String.format(format, data), ex);
	}

	public static void severe(String format, Object... data)
	{
		log(Level.ERROR, format, data);
	}

	public static void warning(String format, Object... data)
	{
		log(Level.WARN, format, data);
	}

	public static void info(String format, Object... data)
	{
		log(Level.INFO, format, data);
	}

	public static void fine(String format, Object... data)
	{
		log(Level.DEBUG, format, data);
	}

	public static void finer(String format, Object... data)
	{
		log(Level.TRACE, format, data);
	}

	public Logger getLogger()
	{
		return myLog;
	}
}