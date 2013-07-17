package kegare.caveworld.core;

import java.util.List;

import kegare.caveworld.util.Color;
import kegare.caveworld.util.Version;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import org.lwjgl.Sys;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.Loader;

public class CommandCaveworld implements ICommand
{
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
		return Lists.newArrayList("caveworld");
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length <= 0)
		{
			Sys.openURL(Caveworld.metadata.url);
		}
		else if (args[0].equalsIgnoreCase("version"))
		{
			if (sender instanceof MinecraftServer)
			{
				MinecraftServer server = (MinecraftServer)sender;
				StringBuilder message = new StringBuilder();
				message.append(" Caveworld ").append(Version.CURRENT).append(" for ");
				message.append(Loader.instance().getMCVersionString()).append(" (Latest: ").append(Version.LATEST).append(")");

				server.logInfo(message.toString());
				server.logInfo("  " + Caveworld.metadata.description);
				server.logInfo("  " + Caveworld.metadata.url);
			}
			else if (sender instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)sender;
				StringBuilder message = new StringBuilder();
				message.append(Color.AQUA).append(" Caveworld ").append(Color.WHITE).append(Version.CURRENT);
				message.append(Color.WHITE).append(" for ").append(Loader.instance().getMCVersionString());
				message.append(Color.LIGHT_GREY).append(" (Latest: ").append(Version.LATEST).append(")");

				player.addChatMessage(message.toString());
				player.addChatMessage("  " + Caveworld.metadata.description);
				player.addChatMessage("  " + Color.DARK_GREY + Caveworld.metadata.url);
			}
		}
		else
		{
			throw new WrongUsageException(getCommandUsage(sender));
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return sender instanceof MinecraftServer || sender instanceof EntityPlayer;
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

	@Override
	public int compareTo(Object command)
	{
		return getCommandName().compareTo(((ICommand)command).getCommandName());
	}
}