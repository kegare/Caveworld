/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.client;

import caveworld.client.gui.GuiIngameCaveMenu;
import caveworld.client.gui.MenuType;
import caveworld.world.WorldProviderAquaCavern;
import caveworld.world.WorldProviderCaveland;
import caveworld.world.WorldProviderCavenia;
import caveworld.world.WorldProviderCavern;
import caveworld.world.WorldProviderCaveworld;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PortalMenuMessage implements IMessage, IMessageHandler<PortalMenuMessage, IMessage>
{
	private int type, x, y, z;

	public PortalMenuMessage() {}

	public PortalMenuMessage(int type, int x, int y, int z)
	{
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		type = buffer.readInt();
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(type);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PortalMenuMessage message, MessageContext ctx)
	{
		GuiIngameCaveMenu menu = new GuiIngameCaveMenu();

		switch (message.type)
		{
			case WorldProviderCaveworld.TYPE:
				menu.setMenuType(MenuType.CAVEWORLD_PORTAL);
				break;
			case WorldProviderCavern.TYPE:
				menu.setMenuType(MenuType.CAVERN_PORTAL);
				break;
			case WorldProviderAquaCavern.TYPE:
				menu.setMenuType(MenuType.AQUA_CAVERN_PORTAL);
				break;
			case WorldProviderCaveland.TYPE:
				menu.setMenuType(MenuType.CAVELAND_PORTAL);
				break;
			case WorldProviderCavenia.TYPE:
				menu.setMenuType(MenuType.CAVENIA_PORTAL);
				break;
		}

		FMLClientHandler.instance().showGuiScreen(menu.setPortalCoord(message.x, message.y, message.z));

		return null;
	}
}