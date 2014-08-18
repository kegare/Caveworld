/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.core;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.DimensionManager;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.world.WorldProviderCaveworld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

public class CommandCaveworld implements ICommand
{
	@Override
	public int compareTo(Object obj)
	{
		return getCommandName().compareTo(((ICommand)obj).getCommandName());
	}

	@Override
	public String getCommandName()
	{
		return "caveworld";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		throw new CommandNotFoundException();
	}

	@Override
	public List getCommandAliases()
	{
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length <= 0 || args[0].equalsIgnoreCase("version"))
		{
			ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, Caveworld.metadata.url);
			IChatComponent component;
			IChatComponent message = new ChatComponentText(" ");

			component = new ChatComponentText("Caveworld");
			component.getChatStyle().setColor(EnumChatFormatting.AQUA);
			message.appendSibling(component);
			message.appendText(" " + Version.getCurrent());

			if (Version.DEV_DEBUG)
			{
				message.appendText(" ");
				component = new ChatComponentText("dev");
				component.getChatStyle().setColor(EnumChatFormatting.RED);
				message.appendSibling(component);
			}

			message.appendText(" for " + Loader.instance().getMCVersionString() + " ");
			component = new ChatComponentText("(Latest: " + Version.getLatest() + ")");
			component.getChatStyle().setColor(EnumChatFormatting.GRAY);
			message.appendSibling(component);
			message.getChatStyle().setChatClickEvent(click);
			sender.addChatMessage(message);

			message = new ChatComponentText("  ");
			component = new ChatComponentText(Caveworld.metadata.description);
			component.getChatStyle().setChatClickEvent(click);
			message.appendSibling(component);
			sender.addChatMessage(message);

			message = new ChatComponentText("  ");
			component = new ChatComponentText(Caveworld.metadata.url);
			component.getChatStyle().setColor(EnumChatFormatting.DARK_GRAY).setChatClickEvent(click);
			message.appendSibling(component);
			sender.addChatMessage(message);
		}
		else if (args[0].equalsIgnoreCase("forum") || args[0].equalsIgnoreCase("url"))
		{
			try
			{
				Desktop.getDesktop().browse(new URI(Caveworld.metadata.url));
			}
			catch (Exception ignored) {}
		}
		else if (args[0].equalsIgnoreCase("regenerate"))
		{
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			IChatComponent component;

			if (sender instanceof EntityPlayerMP)
			{
				if (server.isDedicatedServer())
				{
					component = new ChatComponentTranslation("commands.generic.permission");
					component.getChatStyle().setColor(EnumChatFormatting.RED);
					sender.addChatMessage(component);

					return;
				}
			}

			try
			{
				int dim = Config.dimensionCaveworld;

				CaveBlocks.caveworld_portal.portalDisabled = true;

				component = new ChatComponentTranslation("caveworld.regenerate.regenerating");
				component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
				server.getConfigurationManager().sendChatMsg(component);

				if (DimensionManager.getWorld(dim) != null)
				{
					EntityPlayerMP player;

					for (Iterator iterator = server.getConfigurationManager().playerEntityList.iterator(); iterator.hasNext();)
					{
						player = (EntityPlayerMP)iterator.next();

						if (player != null && player.dimension == dim)
						{
							server.getConfigurationManager().transferPlayerToDimension(player, 0);
							player.playerNetServerHandler.playerEntity = server.getConfigurationManager().respawnPlayer(player, 0, true);
						}
					}

					DimensionManager.unloadWorld(dim);
					DimensionManager.unloadWorlds(server.worldTickTimes);
				}

				File dir = WorldProviderCaveworld.getDimDir();

				if (dir != null)
				{
					FileUtils.deleteDirectory(dir);
				}

				DimensionManager.initDimension(dim);
				DimensionManager.getWorld(dim).saveAllChunks(true, null);

				component = new ChatComponentTranslation("caveworld.regenerate.regenerated");
				component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
				server.getConfigurationManager().sendChatMsg(component);

				CaveBlocks.caveworld_portal.portalDisabled = false;
			}
			catch (Exception e)
			{
				component = new ChatComponentTranslation("caveworld.regenerate.failed");
				component.getChatStyle().setColor(EnumChatFormatting.RED).setItalic(true);
				server.getConfigurationManager().sendChatMsg(component);

				CaveLog.log(Level.ERROR, e, component.getUnformattedText());
			}
		}
		else if (Version.DEV_DEBUG && args[0].equalsIgnoreCase("mp") && args.length > 1 && sender instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
			MinecraftServer server = player.mcServer;

			if (server.getConfigurationManager().func_152596_g(player.getGameProfile()))
			{
				int value = Integer.valueOf(args[1]);

				if (value != 0)
				{
					CaveworldAPI.addMiningPoint(player, value);
				}
			}
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return sender instanceof MinecraftServer || sender instanceof EntityPlayerMP;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, "version", "forum", "regenerate") : null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return false;
	}
}