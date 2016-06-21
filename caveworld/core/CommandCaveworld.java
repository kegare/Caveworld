package caveworld.core;

import java.util.List;

import com.google.common.base.Joiner;

import caveworld.api.CaverAPI;
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.client.CaveworldMenuMessage;
import caveworld.network.client.RegenerationGuiMessage;
import caveworld.util.Version;
import cpw.mods.fml.common.Loader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandCaveworld extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "caveworld";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return String.format("/%s <%s>", getCommandName(), Joiner.on('|').join(getCommands()));
	}

	public String[] getCommands()
	{
		return new String[] {"version", "menu", "regenerate"};
	}

	@Override
	public void processCommand(ICommandSender sender, final String[] args)
	{
		if ((args.length <= 0 || args[0].equalsIgnoreCase("menu")) && sender instanceof EntityPlayerMP)
		{
			CaveNetworkRegistry.sendTo(new CaveworldMenuMessage(), (EntityPlayerMP)sender);
		}
		else if (args[0].equalsIgnoreCase("version"))
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
		else if (args[0].equalsIgnoreCase("regenerate") && sender instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)sender;
			MinecraftServer server = player.mcServer;

			if (server.isSinglePlayer() || server.getConfigurationManager().func_152596_g(player.getGameProfile()))
			{
				CaveNetworkRegistry.sendTo(new RegenerationGuiMessage(RegenerationGuiMessage.EnumType.OPEN), player);
			}
			else
			{
				IChatComponent component = new ChatComponentTranslation("commands.generic.permission");
				component.getChatStyle().setColor(EnumChatFormatting.RED);
				sender.addChatMessage(component);
			}
		}
		else if (args[0].equalsIgnoreCase("mp") && args.length > 1 && sender instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)sender;

			if (player.getServerForPlayer().getWorldInfo().areCommandsAllowed())
			{
				int value = parseInt(sender, args[1]);

				if (value != 0)
				{
					CaverAPI.addMiningPoint(player, value);
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
		return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, getCommands()) : null;
	}
}