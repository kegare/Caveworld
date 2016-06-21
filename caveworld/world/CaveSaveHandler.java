package caveworld.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;

import caveworld.config.Config;
import caveworld.util.CaveLog;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;

public class CaveSaveHandler
{
	protected Random random = new SecureRandom();

	protected String name;
	protected int dimension;

	protected NBTTagCompound data;
	protected long worldSeed;
	protected int subsurfaceHeight;

	public CaveSaveHandler(String name)
	{
		this.name = name;
	}

	public CaveSaveHandler setDimension(int dim)
	{
		dimension = dim;

		return this;
	}

	public String getSaveFolder()
	{
		return Config.cauldron ? "DIM" + dimension : "DIM-" + name;
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

		File dir = new File(root, getSaveFolder());

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
		}

		data = null;
	}

	public void readFromBuffer(ByteBuf buffer)
	{
		worldSeed = buffer.readLong();
		subsurfaceHeight = buffer.readInt();
	}

	public void writeToBuffer(ByteBuf buffer)
	{
		buffer.writeLong(worldSeed);
		buffer.writeInt(subsurfaceHeight);
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
			nbt.setLong("Seed", random.nextLong());
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