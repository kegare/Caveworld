package caveworld.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Joiner;

import caveworld.network.CaveNetworkRegistry;
import caveworld.network.client.RegenerationGuiMessage;
import caveworld.network.client.RegenerationGuiMessage.EnumType;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public class DimensionHelper
{
	public static boolean regenerate(int dim, boolean backup)
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		WorldServer world = server.worldServerForDimension(dim);
		File dir = new File(DimensionManager.getCurrentSaveRootDirectory(), world.provider.getSaveFolder());

		IChatComponent name, component;

		name = new ChatComponentText(world.provider.getDimensionName());
		name.getChatStyle().setBold(true);

		for (EntityPlayerMP player : (List<EntityPlayerMP>)server.getConfigurationManager().playerEntityList)
		{
			if (player.dimension == dim)
			{
				CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.FAILED));

				component = new ChatComponentTranslation("caveworld.regeneration.failed", name);
				component.getChatStyle().setColor(EnumChatFormatting.GRAY);

				server.getConfigurationManager().sendChatMsg(component);

				return false;
			}
		}

		if (server.isSinglePlayer())
		{
			CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.OPEN));
		}

		CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.START));

		component = new ChatComponentTranslation("caveworld.regeneration.start", name);
		component.getChatStyle().setColor(EnumChatFormatting.GRAY);

		server.getConfigurationManager().sendChatMsg(component);

		try
		{
			world.saveAllChunks(true, null);
		}
		catch (Exception e) {}

		world.flush();

		MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(world));

		DimensionManager.setWorld(dim, null);

		if (dir.exists())
		{
			if (backup)
			{
				final Pattern pattern = Pattern.compile("^" + dir.getName() + "_bak-..*\\.zip$");

				File parent = dir.getParentFile();
				File[] files = parent.listFiles(new FilenameFilter()
				{
					@Override
					public boolean accept(File dir, String name)
					{
						return pattern.matcher(name).matches();
					}
				});

				if (files != null && files.length >= 5)
				{
					Arrays.sort(files, new Comparator<File>()
					{
						@Override
						public int compare(File o1, File o2)
						{
							int i = CaveUtils.compareWithNull(o1, o2);

							if (i == 0 && o1 != null && o2 != null)
							{
								try
								{
									i = Files.getLastModifiedTime(o1.toPath()).compareTo(Files.getLastModifiedTime(o2.toPath()));
								}
								catch (IOException e) {}
							}

							return i;
						}
					});

					try
					{
						FileUtils.forceDelete(files[0]);
					}
					catch (IOException e) {}
				}

				Calendar calendar = Calendar.getInstance();
				String year = Integer.toString(calendar.get(Calendar.YEAR));
				String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
				String day = String.format("%02d", calendar.get(Calendar.DATE));
				String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
				String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
				String second = String.format("%02d", calendar.get(Calendar.SECOND));
				File bak = new File(parent, dir.getName() + "_bak-" + Joiner.on("").join(year, month, day) + "-" + Joiner.on("").join(hour, minute, second) + ".zip");

				if (bak.exists())
				{
					FileUtils.deleteQuietly(bak);
				}

				CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.BACKUP));

				component = new ChatComponentTranslation("caveworld.regeneration.backup", name);
				component.getChatStyle().setColor(EnumChatFormatting.GRAY);

				server.getConfigurationManager().sendChatMsg(component);

				if (CaveUtils.archiveDirZip(dir, bak))
				{
					ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_FILE, FilenameUtils.normalize(bak.getParentFile().getPath()));

					component = new ChatComponentTranslation("caveworld.regeneration.backup.success", name);
					component.getChatStyle().setColor(EnumChatFormatting.GRAY).setChatClickEvent(click);

					server.getConfigurationManager().sendChatMsg(component);
				}
				else
				{
					component = new ChatComponentTranslation("caveworld.regeneration.backup.failed", name);
					component.getChatStyle().setColor(EnumChatFormatting.RED);

					server.getConfigurationManager().sendChatMsg(component);
				}
			}

			try
			{
				FileUtils.deleteDirectory(dir);
			}
			catch (IOException e)
			{
				CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.FAILED));

				component = new ChatComponentTranslation("caveworld.regeneration.failed", name);
				component.getChatStyle().setColor(EnumChatFormatting.GRAY);

				server.getConfigurationManager().sendChatMsg(component);

				return false;
			}
		}

		if (DimensionManager.shouldLoadSpawn(dim))
		{
			world = server.worldServerForDimension(dim);

			try
			{
				world.saveAllChunks(true, null);
			}
			catch (Exception e) {}

			world.flush();
		}

		CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.SUCCESS));

		component = new ChatComponentTranslation("caveworld.regeneration.success", name);
		component.getChatStyle().setColor(EnumChatFormatting.GRAY);

		server.getConfigurationManager().sendChatMsg(component);

		return true;
	}
}