package kegare.caveworld.core;

import java.util.List;

import kegare.caveworld.util.Version;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.Sys;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.Loader;

public class CommandCaveworld implements ICommand
{
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
		if (args.length <= 0)
		{
			Sys.openURL(Caveworld.metadata.url);
		}
		else if ("version".equalsIgnoreCase(args[0]))
		{
			StringBuilder message = new StringBuilder();
			message.append(EnumChatFormatting.AQUA).append(" Caveworld ").append(EnumChatFormatting.RESET);
			message.append(Version.CURRENT).append(" for ").append(Loader.instance().getMCVersionString());
			message.append(EnumChatFormatting.GRAY).append(" (Latest: ").append(Version.LATEST).append(")");

			Caveworld.proxy.addChatMessage(message.toString());
			Caveworld.proxy.addChatMessage("  " + Caveworld.metadata.description);
			Caveworld.proxy.addChatMessage("  " + EnumChatFormatting.DARK_GRAY + Caveworld.metadata.url);
		}
		else
		{
			throw new WrongUsageException(getCommandUsage(sender));
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
		return Lists.newArrayList("version");
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return false;
	}
}