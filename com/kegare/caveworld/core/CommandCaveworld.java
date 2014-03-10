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

import com.google.common.collect.Lists;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.Version;
import cpw.mods.fml.common.Loader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.WorldServer;

import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.Locale;

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
		if (args.length == 0 || args[0].equalsIgnoreCase("version"))
		{
			ChatStyle style = new ChatStyle();
			style.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Caveworld.metadata.url));
			StringBuilder builder = new StringBuilder();

			builder.append(EnumChatFormatting.AQUA).append(" Caveworld ").append(EnumChatFormatting.RESET).append(Version.getCurrent());

			if (Version.DEV_DEBUG)
			{
				builder.append(EnumChatFormatting.RED).append(" dev").append(EnumChatFormatting.RESET);
			}

			builder.append(" for ").append(Loader.instance().getMCVersionString());
			builder.append(EnumChatFormatting.GRAY).append(" (Latest: ").append(Version.getLatest()).append(")");

			sender.addChatMessage(new ChatComponentText(sender instanceof EntityPlayerMP ? builder.toString() : CaveUtils.stripControlCodes(builder.toString())).setChatStyle(style));
			sender.addChatMessage(new ChatComponentText("  " + Caveworld.metadata.description).setChatStyle(style));
			sender.addChatMessage(new ChatComponentText("  " + Caveworld.metadata.url).setChatStyle(style.createDeepCopy().setColor(EnumChatFormatting.DARK_GRAY)));
		}
		else if (args[0].equalsIgnoreCase("forum") || args[0].equalsIgnoreCase("url"))
		{
			try
			{
				Desktop.getDesktop().browse(new URI(Caveworld.metadata.url));
			}
			catch (Exception ignored) {}
		}
		else if (Version.DEV_DEBUG && sender instanceof EntityPlayerMP && ((EntityPlayerMP)sender).capabilities.isCreativeMode)
		{
			EntityPlayerMP player = (EntityPlayerMP)sender;

			if (args[0].equalsIgnoreCase("mineshaft"))
			{
				WorldServer world = player.getServerForPlayer();
				int x = MathHelper.floor_double(player.posX);
				int y = MathHelper.floor_double(player.posY);
				int z = MathHelper.floor_double(player.posZ);
				ChunkPosition pos = world.getChunkProvider().func_147416_a(world, "Mineshaft", x, y, z);

				if (pos != null) player.playerNetServerHandler.setPlayerLocation(pos.chunkPosX, pos.chunkPosY + 1.5D, pos.chunkPosZ, player.rotationYaw, player.rotationPitch);
			}
			else if (args[0].equalsIgnoreCase("stronghold"))
			{
				WorldServer world = player.getServerForPlayer();
				int x = MathHelper.floor_double(player.posX);
				int y = MathHelper.floor_double(player.posY);
				int z = MathHelper.floor_double(player.posZ);
				ChunkPosition pos = world.getChunkProvider().func_147416_a(world, "Stronghold", x, y, z);

				if (pos != null) player.playerNetServerHandler.setPlayerLocation(pos.chunkPosX, pos.chunkPosY + 1.5D, pos.chunkPosZ, player.rotationYaw, player.rotationPitch);
			}
			else if (args[0].equalsIgnoreCase("mining") && args.length > 2)
			{
				String str = args[2];
				boolean flag = str.toUpperCase(Locale.ENGLISH).endsWith("L");

				if (flag)
				{
					str = str.substring(0, str.length() - 1);
				}

				int value = 0;

				if (args[1].equalsIgnoreCase("add"))
				{
					value = CommandBase.parseIntBounded(sender, str, Short.MIN_VALUE, Short.MAX_VALUE);
				}
				else if (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("reduce"))
				{
					value = - CommandBase.parseIntBounded(sender, str, Short.MIN_VALUE, Short.MAX_VALUE);
				}

				if (value != 0)
				{
					if (flag)
					{
						CaveMiningManager.addMiningLevel(player, value);
					}
					else
					{
						CaveMiningManager.addMiningCount(player, value);
					}
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
		return Lists.newArrayList("version", "forum");
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return false;
	}
}