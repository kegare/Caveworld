package caveworld.network.common;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveVein;
import caveworld.api.ICaveVeinManager;
import caveworld.config.Config;
import caveworld.config.manager.CaveVeinManager;
import caveworld.network.CaveNetworkRegistry;
import caveworld.world.WorldProviderAquaCavern;
import caveworld.world.WorldProviderCavern;
import caveworld.world.WorldProviderCaveworld;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class VeinAdjustMessage implements IMessage, IMessageHandler<VeinAdjustMessage, IMessage>
{
	private NBTTagCompound data;

	public VeinAdjustMessage() {}

	public VeinAdjustMessage(ICaveVeinManager manager)
	{
		this.data = new NBTTagCompound();
		this.data.setInteger("Type", manager.getType());
		this.data.setTag("Veins", manager.saveToNBT());
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
	public IMessage onMessage(VeinAdjustMessage message, MessageContext ctx)
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
		final NBTTagList list = nbt.getTagList("Veins", NBT.TAG_COMPOUND);
		final ICaveVeinManager manager;

		switch (type)
		{
			case WorldProviderCaveworld.TYPE:
				manager = CaveworldAPI.veinManager;
				break;
			case WorldProviderCavern.TYPE:
				manager = CaveworldAPI.veinCavernManager;
				break;
			case WorldProviderAquaCavern.TYPE:
				manager = CaveworldAPI.veinAquaCavernManager;
				break;
			default:
				manager = null;
				break;
		}

		if (manager != null)
		{
			boolean prev = manager.isReadOnly();
			final int size = manager.getCaveVeins().size();

			manager.setReadOnly(false);
			manager.clearCaveVeins();

			if (server)
			{
				ICaveVeinManager temp = new CaveVeinManager();
				temp.loadFromNBT(list);

				if (size != temp.getCaveVeins().size())
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

				for (ICaveVein vein : temp.getCaveVeins())
				{
					manager.addCaveVein(vein);
				}

				Config.saveConfig(manager.getConfig());

				CaveNetworkRegistry.sendToOthers(new VeinAdjustMessage(manager), ctx.getServerHandler().playerEntity);
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