package com.kegare.caveworld.core;

import com.kegare.caveworld.util.Version;
import cpw.mods.fml.common.Loader;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.regex.Pattern;

public class CommandCaveworld implements ICommand
{
	private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

	@Override
	public int compareTo(Object command)
	{
		return getCommandName().compareTo(((ICommand)command).getCommandName());
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
		ChatStyle style = new ChatStyle();
		style.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Caveworld.metadata.url));
		StringBuilder message = new StringBuilder();

		message.append(EnumChatFormatting.AQUA).append(" Caveworld ").append(EnumChatFormatting.RESET).append(Version.CURRENT.orNull());

		if (Version.DEV_DEBUG)
		{
			message.append(EnumChatFormatting.RED).append(" dev").append(EnumChatFormatting.RESET);
		}

		message.append(" for ").append(Loader.instance().getMCVersionString());
		message.append(EnumChatFormatting.GRAY).append(" (Latest: ").append(Version.LATEST.orNull()).append(")");

		sender.addChatMessage(new ChatComponentText(sender instanceof EntityPlayerMP ? message.toString() : stripControlCodes(message.toString())).setChatStyle(style));
		sender.addChatMessage(new ChatComponentText("  " + Caveworld.metadata.description).setChatStyle(style));
		sender.addChatMessage(new ChatComponentText("  " + Caveworld.metadata.url).setChatStyle(style.createDeepCopy().setColor(EnumChatFormatting.DARK_GRAY)));
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return sender instanceof MinecraftServer || sender instanceof EntityPlayerMP;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return false;
	}

	public static String stripControlCodes(String str)
	{
		return patternControlCode.matcher(str).replaceAll("");
	}
}