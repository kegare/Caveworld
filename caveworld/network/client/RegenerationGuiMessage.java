package caveworld.network.client;

import caveworld.client.gui.GuiRegeneration;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class RegenerationGuiMessage implements IMessage, IMessageHandler<RegenerationGuiMessage, IMessage>
{
	private int type;

	public RegenerationGuiMessage() {}

	public RegenerationGuiMessage(EnumType type)
	{
		this.type = type.ordinal();
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		type = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(type);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(RegenerationGuiMessage message, MessageContext ctx)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		EnumType type = EnumType.get(message.type);

		if (type == EnumType.OPEN)
		{
			mc.displayGuiScreen(new GuiRegeneration());
		}
		else if (mc.currentScreen != null && mc.currentScreen instanceof GuiRegeneration)
		{
			GuiRegeneration gui = (GuiRegeneration)mc.currentScreen;

			gui.updateProgress(type);
		}

		return null;
	}

	public enum EnumType
	{
		OPEN,
		START,
		BACKUP,
		SUCCESS,
		FAILED;

		public static EnumType get(int type)
		{
			if (type < 0)
			{
				type = 0;
			}

			int max = values().length - 1;

			if (type > max)
			{
				type = max;
			}

			return values()[type];
		}
	}
}