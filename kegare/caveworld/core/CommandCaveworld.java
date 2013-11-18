package kegare.caveworld.core;

import java.util.List;

import kegare.caveworld.util.Version;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
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
		if (sender instanceof MinecraftServer)
		{
			ChatMessageComponent message = new ChatMessageComponent();
			message.addText(" Caveworld ").addText(Version.CURRENT).addText(" for ").addText(Loader.instance().getMCVersionString());
			message.addText(" (Latest: ").addText(Version.LATEST).addText(")");

			sender.sendChatToPlayer(message);
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("  ").addText(Caveworld.metadata.description));
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("  ").addText(Caveworld.metadata.url));
		}
		else
		{
			StringBuilder message = new StringBuilder();
			message.append(EnumChatFormatting.AQUA).append(" Caveworld ").append(EnumChatFormatting.RESET);
			message.append(Version.CURRENT).append(" for ").append(Loader.instance().getMCVersionString());
			message.append(EnumChatFormatting.GRAY).append(" (Latest: ").append(Version.LATEST).append(")");

			sender.sendChatToPlayer(ChatMessageComponent.createFromText(message.toString()));
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("  ").addText(Caveworld.metadata.description));
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("  ").addText(Caveworld.metadata.url).setColor(EnumChatFormatting.DARK_GRAY));
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
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return false;
	}
}