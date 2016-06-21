package caveworld.network.server;

import caveworld.api.CaveworldAPI;
import caveworld.network.client.RegenerationGuiMessage;
import caveworld.network.client.RegenerationGuiMessage.EnumType;
import caveworld.util.DimensionHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class RegenerationMessage implements IMessage, IMessageHandler<RegenerationMessage, IMessage>
{
	private boolean backup = true;
	private boolean caveworld = true;
	private boolean cavern = true;
	private boolean aquaCavern = true;
	private boolean caveland = true;
	private boolean cavenia = true;

	public RegenerationMessage() {}

	public RegenerationMessage(boolean backup)
	{
		this.backup = backup;
	}

	public RegenerationMessage(boolean backup, boolean caveworld, boolean cavern, boolean aquaCavern, boolean caveland, boolean cavenia)
	{
		this(backup);
		this.caveworld = caveworld;
		this.cavern = cavern;
		this.aquaCavern = aquaCavern;
		this.caveland = caveland;
		this.cavenia = cavenia;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		backup = buffer.readBoolean();
		caveworld = buffer.readBoolean();
		cavern = buffer.readBoolean();
		aquaCavern = buffer.readBoolean();
		caveland = buffer.readBoolean();
		cavenia = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(backup);
		buffer.writeBoolean(caveworld);
		buffer.writeBoolean(cavern);
		buffer.writeBoolean(aquaCavern);
		buffer.writeBoolean(caveland);
		buffer.writeBoolean(cavenia);
	}

	@Override
	public IMessage onMessage(RegenerationMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;

		if (player.mcServer.isSinglePlayer() || player.mcServer.getConfigurationManager().func_152596_g(player.getGameProfile()))
		{
			if (message.caveworld)
			{
				DimensionHelper.regenerate(CaveworldAPI.getDimension(), message.backup);
			}

			if (message.cavern)
			{
				DimensionHelper.regenerate(CaveworldAPI.getCavernDimension(), message.backup);
			}

			if (message.aquaCavern)
			{
				DimensionHelper.regenerate(CaveworldAPI.getAquaCavernDimension(), message.backup);
			}

			if (message.caveland)
			{
				DimensionHelper.regenerate(CaveworldAPI.getCavelandDimension(), message.backup);
			}

			if (message.cavenia)
			{
				DimensionHelper.regenerate(CaveworldAPI.getCaveniaDimension(), message.backup);
			}

			return null;
		}

		return new RegenerationGuiMessage(EnumType.FAILED);
	}
}