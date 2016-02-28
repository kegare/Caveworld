/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.mceconomy;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import caveworld.core.Config;
import caveworld.network.CaveNetworkRegistry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class ProductAdjustMessage implements IMessage, IMessageHandler<ProductAdjustMessage, IMessage>
{
	private NBTTagCompound data;

	public ProductAdjustMessage() {}

	public ProductAdjustMessage(IShopProductManager manager)
	{
		this.data = new NBTTagCompound();
		this.data.setInteger("Type", manager.getType());
		this.data.setTag("Products", manager.saveToNBT());
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeTag(buffer, data);
	}

	@Override
	public IMessage onMessage(ProductAdjustMessage message, MessageContext ctx)
	{
		boolean server = false;

		if (ctx.side.isServer())
		{
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;

			if (Config.remoteConfig && player.mcServer.getConfigurationManager().func_152596_g(player.getGameProfile()))
			{
				server = true;
			}
			else return null;
		}

		final NBTTagCompound nbt = message.data;
		final int type = nbt.getInteger("Type");
		final NBTTagList list = nbt.getTagList("Products", NBT.TAG_COMPOUND);
		final IShopProductManager manager;

		switch (type)
		{
			case 0:
				manager = MCEconomyPlugin.productManager;
				break;
			default:
				manager = null;
				break;
		}

		if (manager != null)
		{
			boolean prev = manager.isReadOnly();
			final int size = manager.getProducts().size();

			manager.setReadOnly(false);
			manager.clearProducts();

			if (server)
			{
				IShopProductManager temp = new ShopProductManager();
				temp.loadFromNBT(list);

				if (size != temp.getProducts().size())
				{
					try
					{
						FileUtils.forceDelete(new File(manager.getConfig().toString()));

						manager.getConfig().load();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}

				for (IShopProduct product : temp.getProducts())
				{
					manager.addShopProduct(product);
				}

				Config.saveConfig(manager.getConfig());

				CaveNetworkRegistry.sendToOthers(new ProductAdjustMessage(manager), ctx.getServerHandler().playerEntity);
			}
			else
			{
				manager.loadFromNBT(list);
			}

			manager.setReadOnly(prev);
		}

		return null;
	}
}