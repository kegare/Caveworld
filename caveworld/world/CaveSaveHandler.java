/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.Locale;

import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;

import caveworld.util.CaveLog;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;

public class CaveSaveHandler
{
	private String name;

	private NBTTagCompound data;
	private long worldSeed;
	private int subsurfaceHeight;

	public CaveSaveHandler(String name)
	{
		this.name = name;
	}

	public NBTTagCompound getData()
	{
		if (data == null)
		{
			readFromFile();
			loadFromNBT();
		}

		return data;
	}

	protected NBTTagCompound getRawData()
	{
		return data;
	}

	public long getWorldSeed()
	{
		return worldSeed;
	}

	public CaveSaveHandler setWorldSeed(long seed)
	{
		worldSeed = seed;

		return this;
	}

	public int getSubsurfaceHeight()
	{
		return subsurfaceHeight;
	}

	public CaveSaveHandler setSubsurfaceHeight(int height)
	{
		subsurfaceHeight = height;

		return this;
	}

	public File getSaveDir()
	{
		File root = DimensionManager.getCurrentSaveRootDirectory();

		if (root == null || !root.exists() || root.isFile())
		{
			return null;
		}

		if (Strings.isNullOrEmpty(name))
		{
			return root;
		}

		File dir = new File(root, "DIM-" + name);

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return dir.isDirectory() ? dir : null;
	}

	public File getSaveFile()
	{
		File dir = getSaveDir();

		if (dir != null)
		{
			File file = new File(dir, name.toLowerCase(Locale.ENGLISH).replaceAll(" ", "") + ".dat");

			return file;
		}

		return null;
	}

	public void readFromFile()
	{
		File file = getSaveFile();

		if (file != null && file.exists() && file.isFile() && file.canRead())
		{
			try (FileInputStream input = new FileInputStream(file))
			{
				data = CompressedStreamTools.readCompressed(input);
			}
			catch (Exception e)
			{
				CaveLog.log(Level.ERROR, e, "An error occurred trying to reading " + name + " dimension data");
			}
		}

		if (data == null)
		{
			data = new NBTTagCompound();
		}
	}

	public void writeToFile()
	{
		File file = getSaveFile();

		if (file != null && data != null)
		{
			try (FileOutputStream output = new FileOutputStream(file))
			{
				CompressedStreamTools.writeCompressed(data, output);
			}
			catch (Exception e)
			{
				CaveLog.log(Level.ERROR, e, "An error occurred trying to reading " + name + " dimension data");
			}
			finally
			{
				data = null;
			}
		}
	}

	public void loadFromNBT()
	{
		loadFromNBT(data);
	}

	public void loadFromNBT(NBTTagCompound nbt)
	{
		if (nbt == null)
		{
			return;
		}

		if (!nbt.hasKey("Seed"))
		{
			nbt.setLong("Seed", new SecureRandom().nextLong());
		}

		if (!nbt.hasKey("SubsurfaceHeight"))
		{
			if (subsurfaceHeight <= 0)
			{
				subsurfaceHeight = 127;
			}

			nbt.setInteger("SubsurfaceHeight", subsurfaceHeight);
		}

		worldSeed = nbt.getLong("Seed");
		subsurfaceHeight = nbt.getInteger("SubsurfaceHeight");
	}
}