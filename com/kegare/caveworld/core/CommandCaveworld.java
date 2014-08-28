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
import java.net.URI;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
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

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.world.WorldProviderCaveworld;

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
	public void processCommand(ICommandSender sender, final String[] args)
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
			catch (Exception e) {}
		}
		else if (args[0].equalsIgnoreCase("regenerate"))
		{
			if (sender instanceof EntityPlayerMP)
			{
				if (((EntityPlayerMP)sender).mcServer.isDedicatedServer())
				{
					IChatComponent component = new ChatComponentTranslation("commands.generic.permission");
					component.getChatStyle().setColor(EnumChatFormatting.RED);
					sender.addChatMessage(component);

					return;
				}
			}

			boolean backup = true;

			if (args.length > 1)
			{
				try
				{
					backup = CommandBase.parseBoolean(sender, args[1]);
				}
				catch (CommandException e)
				{
					backup = true;
				}
			}

			WorldProviderCaveworld.regenerate(backup);
		}
		else if (args[0].equalsIgnoreCase("mp") && args.length > 1 && sender instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);

			if (player.mcServer.isSinglePlayer() && player.getServerForPlayer().getWorldInfo().areCommandsAllowed())
			{
				int value = Integer.parseInt(args[1]);

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